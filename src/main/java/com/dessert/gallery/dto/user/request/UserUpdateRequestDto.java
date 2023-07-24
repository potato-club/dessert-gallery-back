package com.dessert.gallery.dto.user.request;

import com.dessert.gallery.entity.User;
import com.dessert.gallery.enums.UserRole;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
public class UserUpdateRequestDto {

    @ApiModelProperty(value = "닉네임")
    private String nickname;

    @ApiModelProperty(value = "유저 역할")
    private UserRole userRole;

    @ApiModelProperty(value = "신규 프로필 사진")
    private List<MultipartFile> file;

    @ApiModelProperty(value = "기존 프로필 사진 이름 (사진 변경 때만 필요)")
    private String fileName;

    @ApiModelProperty(value = "기존 프로필 사진 URL (사진 변경 때만 필요)")
    private String fileUrl;
}
