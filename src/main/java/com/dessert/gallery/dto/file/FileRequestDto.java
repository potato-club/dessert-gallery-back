package com.dessert.gallery.dto.file;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class FileRequestDto {

    @ApiModelProperty(value = "파일 이름")
    private String fileName;

    @ApiModelProperty(value = "파일 Url")
    private String fileUrl;

    @ApiModelProperty(value = "파일 삭제/교체 여부")
    private boolean deleted;
}
