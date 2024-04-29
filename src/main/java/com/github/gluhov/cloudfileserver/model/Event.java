package com.github.gluhov.cloudfileserver.model;

import lombok.*;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("events")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
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
}