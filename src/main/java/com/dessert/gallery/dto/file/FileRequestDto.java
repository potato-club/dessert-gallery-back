package com.dessert.gallery.dto.file;

import com.dessert.gallery.entity.File;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class FileRequestDto {

    @Schema(description = "파일 이름")
    private String fileName;

    @Schema(description = "파일 Url")
    private String fileUrl;

    @Schema(description = "파일 삭제/교체 여부")
    private boolean deleted;

    public FileRequestDto(File file) {
        this.fileName = file.getFileName();
        this.fileUrl = file.getFileUrl();
        this.deleted = true;
    }

    public FileRequestDto(FileDto dto, boolean deleted) {
        this.fileName = dto.getFileName();
        this.fileUrl = dto.getFileUrl();
        this.deleted = deleted;
    }
}
