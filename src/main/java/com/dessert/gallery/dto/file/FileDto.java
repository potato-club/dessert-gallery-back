package com.dessert.gallery.dto.file;

import com.dessert.gallery.entity.File;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Objects;

@Data
@Builder
@AllArgsConstructor
public class FileDto {
    @Schema(description = "이미지 파일 이름")
    private String fileName;
    @Schema(description = "이미지 파일 url")
    private String fileUrl;

    public FileDto(File file) {
        this.fileName = file.getFileName();
        this.fileUrl = file.getFileUrl();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (!(o instanceof FileDto))
            return false;

        FileDto dto = (FileDto) o;

        return Objects.equals(this.fileName, dto.getFileName()) && Objects.equals(this.fileUrl, dto.getFileUrl());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.fileName, this.fileUrl);
    }
}
