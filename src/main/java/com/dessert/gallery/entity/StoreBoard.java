package com.dessert.gallery.entity;

import com.dessert.gallery.dto.board.BoardRequestDto;
import com.dessert.gallery.dto.board.BoardUpdateDto;
import com.dessert.gallery.dto.file.FileDto;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StoreBoard extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 1000)
    private String content;

    @Column(nullable = false)
    private String tags;

    @Column(columnDefinition = "integer default 0", nullable = false)
    private int view;

    @Column(columnDefinition = "TINYINT(1)")
    private boolean deleted;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @OneToMany(mappedBy = "storeBoard", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<File> images = new ArrayList<>();

    public StoreBoard(BoardRequestDto requestDto, Store store) {
        this.title = requestDto.getTitle();
        this.content = requestDto.getContent();
        this.tags = removeTagSpace(requestDto.getTags());
        this.store = store;
        this.deleted = false;
    }

    // 띄어쓰기 삭제
    private String removeTagSpace(String tags) {
        return tags.replaceAll("\\s", "");
    }

    public void updateImages(List<File> images) {
        this.images.addAll(images);
    }

    public void removeImage(FileDto dto) {
        this.images.removeIf(file ->
                file.getFileName().equals(dto.getFileName())
                        && file.getFileUrl().equals(dto.getFileUrl()));
    }

    public void updateBoard(BoardUpdateDto requestDto) {
        this.title = requestDto.getTitle();
        this.content = requestDto.getContent();
        this.tags = removeTagSpace(requestDto.getTags());
    }

    public int increaseView() {
        return this.view += 1;
    }

    public void deleteBoard() {
        this.deleted = true;
    }
}
