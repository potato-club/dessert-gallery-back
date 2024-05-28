package com.dessert.gallery.dto.user.request;

import com.dessert.gallery.enums.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
public class UserUpdateRequestDto {

    @Schema(description = "닉네임")
    private String nickname;

    @Schema(description = "유저 역할")
    private UserRole userRole;

    @Schema(description = "신규 프로필 사진", defaultValue = " ")
    private List<MultipartFile> file;

    @Schema(description = "기존 프로필 사진 이름 (사진 변경 때만 필요)")
    private String fileName;

    @Schema(description = "기존 프로필 사진 URL (사진 변경 때만 필요)")
    private String fileUrl;
}
