package com.dessert.gallery.dto.store;

import com.dessert.gallery.dto.file.FileDto;
import com.dessert.gallery.entity.Store;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class StoreResponseDto {
    @Schema(description = "가게 id")
    private Long id;
    @Schema(description = "가게 이름")
    private String name;
    @Schema(description = "가게 소개")
    private String introduction;
    @Schema(description = "가게 주소")
    private String address;
    @Schema(description = "가게 전화번호")
    private String phoneNumber;
    @Schema(description = "가게 프로필 이미지")
    private FileDto storeImage;
    @Schema(description = "가게 게시글 개수")
    private int postCount;
    @Schema(description = "가게 팔로워 수")
    private int followers;
    @Schema(description = "유저의 팔로우 여부")
    private boolean follow = false;

    public StoreResponseDto(Store store, int postCount, int followerCount) {
        this.id = store.getId();
        this.name = store.getName();
        this.introduction = store.getContent();
        this.address = store.getAddress();
        this.phoneNumber = store.getPhoneNumber();
        this.storeImage = store.getImage() == null ? null : new FileDto(store.getImage());
        this.postCount = postCount;
        this.followers = followerCount;
    }

    public void setFollow(boolean followState) {
        this.follow = followState;
    }
}
