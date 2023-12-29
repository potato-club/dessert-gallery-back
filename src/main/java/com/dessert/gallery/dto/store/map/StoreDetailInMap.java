package com.dessert.gallery.dto.store.map;

import com.dessert.gallery.dto.board.BoardListResponseDtoForMap;
import com.dessert.gallery.dto.file.FileDto;
import com.dessert.gallery.dto.notice.NoticeListDto;
import com.dessert.gallery.dto.review.ReviewResponseDtoForMap;
import com.dessert.gallery.entity.Store;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class StoreDetailInMap {
    @Schema(description = "가게 id")
    private Long id;
    @Schema(description = "가게 이름")
    private String name;
    @Schema(description = "가게 정보")
    private String info;
    @Schema(description = "가게 소개")
    private String content;
    @Schema(description = "가게 주소")
    private String address;
    @Schema(description = "가게 전화번호")
    private String phoneNumber;
    @Schema(description = "가게 프로필 이미지")
    private FileDto storeImage;
    @Schema(description = "가게 최신 게시글 4개")
    private List<BoardListResponseDtoForMap> posts;
    @Schema(description = "가게 최신 리뷰 2개")
    private List<ReviewResponseDtoForMap> reviews;
    @Schema(description = "가게 최신 공지 2개")
    private List<NoticeListDto> notices;

    public StoreDetailInMap(Store store, List<BoardListResponseDtoForMap> boards,
                            List<ReviewResponseDtoForMap> reviews, List<NoticeListDto> notices) {
        this.id = store.getId();
        this.name = store.getName();
        this.info = store.getInfo();
        this.content = store.getContent();
        this.address = store.getAddress();
        this.phoneNumber = store.getPhoneNumber();
        this.storeImage = store.getImage() == null ? null : new FileDto(store.getImage());
        this.posts = boards;
        this.reviews = reviews;
        this.notices = notices;
    }
}
