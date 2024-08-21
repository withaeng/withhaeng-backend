package com.travel.withaeng.domain.accompany

import com.travel.withaeng.converter.TagIdsConverter
import com.travel.withaeng.domain.BaseEntity
import jakarta.persistence.*
import org.hibernate.annotations.Comment
import org.hibernate.annotations.DynamicUpdate
import java.time.LocalDate

@DynamicUpdate
@Entity
@Table(name = "accompany")
class Accompany(

    @Column(name = "user_id", nullable = false)
    val userId: Long,

    @Column(name = "title", nullable = false)
    @Comment("동행 제목")
    var title: String,

    @Lob
    @Column(name = "content", nullable = false)
    @Comment("동행 내용")
    var content: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "accompany_status", nullable = false)
    @Comment("동행 모집 상태")
    var accompanyStatus: AccompanyStatus = AccompanyStatus.ING,

    @Column(name = "start_trip_date", nullable = false)
    @Comment("여행 시작 일자")
    var startTripDate: LocalDate,

    @Column(name = "end_trip_date", nullable = false)
    @Comment("여행 종료 일자")
    var endTripDate: LocalDate,

    @Column(name = "banner_image_url")
    @Comment("베너 이미지 URI")
    var bannerImageUrl: String?,

    @Column(name = "member_count", nullable = false)
    @Comment("모집 인원")
    var memberCount: Long = 0L,

    @Column(name = "view_count", nullable = false)
    @Comment("조회수")
    var viewCount: Long = 0L,

    @Column(name = "open_kakao_url", nullable = false)
    @Comment("오픈 카카오 채팅 URI")
    var openKakaoUrl: String,

    @Embedded
    @Comment("동행 장소 정보")
    var accompanyDestination: AccompanyDestination,

    @Column(name = "start_accompany_age", nullable = false)
    @Comment("동행 시작 연령")
    var startAccompanyAge: Int,

    @Column(name = "end_accompany_age", nullable = false)
    @Comment("동행 종료 연령")
    var endAccompanyAge: Int,

    @Convert(converter = TagIdsConverter::class)
    @Column(name = "tag_ids", nullable = false)
    @Comment("태그 목록")
    var tagIds: Set<Long> = setOf()

) : BaseEntity() {

    companion object {

        fun create(params: CreateAccompanyDto): Accompany {
            return Accompany(
                userId = params.userId,
                title = params.title,
                content = params.content,
                startTripDate = params.startTripDate,
                endTripDate = params.endTripDate,
                bannerImageUrl = params.bannerImageUrl,
                memberCount = params.memberCount,
                openKakaoUrl = params.openKakaoUrl,
                accompanyDestination = params.destination,
                startAccompanyAge = params.startAccompanyAge.value,
                endAccompanyAge = params.endAccompanyAge.value,
                tagIds = params.tagIds ?: setOf()
            )
        }

    }
}