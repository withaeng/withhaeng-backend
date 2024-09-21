package com.withaeng.api.applicationservice.user.dto

import com.withaeng.domain.user.*
import com.withaeng.domain.user.dto.UpdateProfileCommand
import com.withaeng.domain.user.dto.UpdateTravelPreferenceCommand

data class UpdateProfileServiceRequest(
    val nickname: String?,
    val introduction: String?,
    val hasImage: Boolean = false,
)

fun UpdateProfileServiceRequest.toCommand(imageUrl: String? = null): UpdateProfileCommand =
    UpdateProfileCommand(
        nickname = nickname,
        introduction = introduction,
        profileImageUrl = imageUrl
    )

data class UpdateTravelPreferenceServiceRequest(
    val mbti: Set<UserMbti>? = emptySet(),
    val preferTravelType: UserPreferTravelType? = null,
    val preferTravelThemes: Set<UserPreferTravelTheme>? = emptySet(),
    val consumeStyle: UserConsumeStyle? = null,
    val foodRestrictions: Set<UserFoodRestriction>? = emptySet(),
    val smokingType: UserSmokingType? = null,
    val drinkingType: UserDrinkingType? = null,
)

fun UpdateTravelPreferenceServiceRequest.toCommand(): UpdateTravelPreferenceCommand =
    UpdateTravelPreferenceCommand(
        mbti = mbti,
        preferTravelType = preferTravelType,
        preferTravelThemes = preferTravelThemes,
        consumeStyle = consumeStyle,
        foodRestrictions = foodRestrictions,
        smokingType = smokingType,
        drinkingType = drinkingType
    )
