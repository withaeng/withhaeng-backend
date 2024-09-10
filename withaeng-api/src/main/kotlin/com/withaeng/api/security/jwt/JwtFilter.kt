package com.withaeng.api.security.jwt

import com.fasterxml.jackson.databind.ObjectMapper
import com.withaeng.api.common.ApiResponse
import com.withaeng.api.common.Constants.Authentication.BEARER_TYPE
import com.withaeng.api.security.authentication.JwtAuthentication
import com.withaeng.common.exception.WithaengException
import com.withaeng.common.exception.WithaengExceptionType
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter

class JwtFilter(
    private val jwtAgent: JwtAgent,
    private val objectMapper: ObjectMapper
) : OncePerRequestFilter() {

    private val log: Logger = LoggerFactory.getLogger(JwtFilter::class.java)

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        log.debug("JWT Filter doFilterInternal()")
        runCatching {
            setAuthenticationFromToken(request)
        }.onFailure { exception ->
            log.debug("Authentication Failed: Authorization value=${request.getAuthorization()}, Message=${exception.message}")
            SecurityContextHolder.clearContext()
            response.run {
                status = HttpStatus.UNAUTHORIZED.value()
                addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                val failResponse = ApiResponse.fail(WithaengExceptionType.AUTHENTICATION_FAILURE, exception.message)
                writer.write(objectMapper.writeValueAsString(failResponse))
            }
        }.onSuccess {
            filterChain.doFilter(request, response)
        }
    }

    private fun setAuthenticationFromToken(request: HttpServletRequest) {
        val authorization = request.getAuthorization()
            ?: throw BadCredentialsException(WithaengExceptionType.AUTHENTICATION_FAILURE.message)
        log.debug("Parsing token in header: $authorization - Request path: ${request.requestURI}")
        getToken(authorization)?.let { token ->
            jwtAgent.extractUserInfoFromToken(token)?.let { userInfo ->
                log.debug("userInfo: $userInfo")
                SecurityContextHolder.getContext().authentication = JwtAuthentication(userInfo)
            }
        } ?: throw WithaengException.of(WithaengExceptionType.AUTHENTICATION_FAILURE)

    }

    private fun getToken(authorization: String): String? {
        val (provider, token) = authorization.split(AUTH_PROVIDER_SPLIT_DELIMITER)
        if (provider.uppercase() != BEARER_TYPE.uppercase()) return null
        return token
    }

    private fun HttpServletRequest.getAuthorization(): String? = getHeader(HttpHeaders.AUTHORIZATION)

    companion object {
        private const val AUTH_PROVIDER_SPLIT_DELIMITER: String = " "
    }
}