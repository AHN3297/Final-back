package com.kh.replay.shortform.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

import java.time.LocalDateTime;

@Entity
@Table(name = "TB_SHORT_FORM")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@DynamicInsert
public class ShortForm {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_SHORT_FORM_GEN")
    @SequenceGenerator(name = "SEQ_SHORT_FORM_GEN", sequenceName = "SEQ_SHORT_FORM_ID", allocationSize = 1)
    @Column(name = "SHORT_FORM_ID")
    private Long shortFormId;

    @Column(name = "SHORT_FORM_TITLE", nullable = false, length = 100)
    private String shortFormTitle;

    @Column(name = "VIDEO_URL", nullable = false, length = 1000)
    private String videoUrl;

    @Column(name = "THUMBNAIL_URL", nullable = false, length = 1000)
    private String thumbnailUrl;

    @Column(name = "CAPTION", length = 1000)
    private String caption;

    @Column(name = "DURATION", nullable = false)
    private Long duration;

    @Column(name = "MEMBER_ID", nullable = false)
    private String memberId;

    @Column(name = "STATUS", length = 1)
    @ColumnDefault("'Y'")
    private String status;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;

    @Column(name = "UPDATED_AT", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
}