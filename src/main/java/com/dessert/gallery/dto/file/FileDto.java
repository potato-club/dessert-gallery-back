package com.dessert.gallery.dto.file;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FileDto {
    private String fileName;
    private String fileUrl;
}
