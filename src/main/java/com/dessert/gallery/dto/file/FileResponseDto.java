package com.dessert.gallery.dto.file;

import com.dessert.gallery.entity.File;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FileResponseDto {
    private String fileName;
    private String fileUrl;

    public FileResponseDto(File file) {
        this.fileName = file.getFileName();
        this.fileUrl = file.getFileUrl();
    }
}
