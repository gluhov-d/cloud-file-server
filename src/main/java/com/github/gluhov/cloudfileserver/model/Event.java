package com.github.gluhov.cloudfileserver.model;

import lombok.*;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("events")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Event extends BaseEntity{
    @Transient
    private User user;
    @Column("user_id")
    private Long userId;
    @Column("file_id")
    private Long fileId;
    @Transient
    private FileEntity file;

    @Builder
    public Event(Long id, Status status, LocalDateTime createdAt, LocalDateTime updatedAt, String createdBy,
                 String modifiedBy, User user, Long userId, Long fileId, FileEntity file) {
        super(id, status, createdAt, updatedAt, createdBy, modifiedBy);
        this.user = user;
        this.userId = userId;
        this.fileId = fileId;
        this.file = file;
    }
}