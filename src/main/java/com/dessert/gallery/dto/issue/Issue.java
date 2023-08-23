package com.dessert.gallery.dto.issue;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class Issue implements Serializable {

    @Schema(description = "깃 이슈 타이틀")
    private String title;

    @Schema(description = "깃 이슈 내용")
    private String body;

}
