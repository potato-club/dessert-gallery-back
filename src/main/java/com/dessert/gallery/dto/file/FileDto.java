package com.dessert.gallery.dto.file;

import com.dessert.gallery.entity.File;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class FileDto {
    @ApiModelProperty(value = "이미지 파일 이름")
    private String fileName;
    @ApiModelProperty(value = "이미지 파일 url")
    private String fileUrl;

    public FileDto(File file) {
        this.fileName = file.getFileName();
        this.fileUrl = file.getFileUrl();
    }
}
