package com.dessert.gallery.dto.issue;

import com.dessert.gallery.enums.DeveloperType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class Issue implements Serializable {

    @Schema(description = "깃 이슈 타이틀")
    private String title;

    @Schema(description = "깃 이슈 내용", example = "Error code: xxxx, Error Message : xxxx")
    private String body;

    @Schema(description = "깃 이슈 라벨", example = "add bug label and my nickname")
    private List<String> labels;

    @Schema(description = "깃 이슈 등록자", example = "Y, D, J")
    private DeveloperType type;
}
