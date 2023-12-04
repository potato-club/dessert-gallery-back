package com.dessert.gallery.dto.memo;

import com.dessert.gallery.entity.Memo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemoResponseDto {
    @Schema(description = "메모 id")
    private Long id;
    @Schema(description = "메모 내용")
    private String content;
    @Schema(description = "완료 상태")
    private boolean checked;

    public MemoResponseDto(Memo memo) {
        this.id = memo.getId();
        this.content = memo.getContent();
        this.checked = memo.isCompleted();
    }
}
