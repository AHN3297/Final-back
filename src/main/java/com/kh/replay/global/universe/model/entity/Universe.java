package com.kh.replay.global.universe.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

import java.time.LocalDateTime;

@Entity
@Table(name = "TB_UNIVERSE")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@DynamicInsert 
public class Universe {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_UNIVERSE_GEN")
    @SequenceGenerator(name = "SEQ_UNIVERSE_GEN", sequenceName = "SEQ_UNIVERSE_ID", allocationSize = 1)
    @Column(name = "UNIVERSE_ID")
    private Long universeId;

    @Column(name = "TITLE", nullable = false, length = 100)
    private String title;

    @Lob // CLOB 데이터 처리
    @Column(name = "LAYOUT_DATA")
    private String layoutData;

    @Column(name = "THEME_CODE", length = 50)
    private String themeCode;

    @Column(name = "STATUS", length = 1)
    @ColumnDefault("'Y'")
    private String status;

    @Column(name = "MEMBER_ID", nullable = false)
    private String memberId;

    @Column(name = "CREATED_AT", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "UPDATED_AT", nullable = false)
    private LocalDateTime updatedAt;
    
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
}