package com.dessert.gallery.dto.board;

import com.dessert.gallery.dto.file.FileDto;
import com.dessert.gallery.entity.StoreBoard;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class BoardResponseDto {
    @Schema(description = "게시글 제목")
    private String title;
    @Schema(description = "게시글 내용")
    private String content;
    @Schema(description = "게시글 이미지 파일")
    private List<FileDto> images;
    @Schema(description = "게시글 해시태그")
    private List<String> tags = new ArrayList<>();
    @Schema(description = "유저의 북마크 여부")
    private boolean isBookmark = false;
    @Schema(description = "가게 사장님 여부")
    private boolean isOwner = false;
    @Schema(description = "게시글 조회수")
    private int viewCount;

    public BoardResponseDto(StoreBoard board) {
        this.title = board.getTitle();
        this.content = board.getContent();
        this.images = board.getImages().isEmpty() ? null : board.getImages().stream()
                .map(FileDto::new)
                .collect(Collectors.toList());
        this.tags = convertTags(board.getTags());
        this.viewCount = board.getView();
    }

    public void updateView(int view) {
        this.viewCount = view;
    }

    public void addUserInfo(boolean isBookmark, boolean isOwner) {
        this.isBookmark = isBookmark;
        this.isOwner = isOwner;
    }

    private List<String> convertTags(String tags) {
        Pattern TAG_PATTERN = Pattern.compile("#([^#]*)");
        List<String> result = new ArrayList<>();

        Matcher matcher = TAG_PATTERN.matcher(tags);
        while (matcher.find()) {
            result.add("#" + matcher.group(1));
        }
        return result;
    }
}
