package com.github.gluhov.cloudfileserver.model;

import lombok.*;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("files")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class FileEntity extends BaseEntity {
    private String location;
    private String name;
    @Column("user_id")
    private Long userId;

    @Builder
    public FileEntity(Long id, Status status, LocalDateTime createdAt, LocalDateTime updatedAt, String createdBy,
                      String modifiedBy, String location, String name, Long userId) {
        super(id, status, createdAt, updatedAt, createdBy, modifiedBy);
        this.location = location;
        this.name = name;
        this.userId = userId;
    }
}