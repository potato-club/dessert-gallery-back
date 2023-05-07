package com.dessert.gallery.dto.file;

import com.dessert.gallery.entity.File;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FileDto {
    private String fileName;
    private String fileUrl;

    public FileDto(File file) {
        this.fileName = file.getFileName();
        this.fileUrl = file.getFileUrl();
    }
}
