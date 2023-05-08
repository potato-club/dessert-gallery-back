package com.dessert.gallery.dto.file;

import com.dessert.gallery.entity.File;
import lombok.Data;

@Data
public class FileDto {
    private String fileName;
    private String fileUrl;

    public FileDto(File file) {
        this.fileName = file.getFileName();
        this.fileUrl = file.getFileUrl();
    }
}
