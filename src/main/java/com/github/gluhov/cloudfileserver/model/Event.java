package com.github.gluhov.cloudfileserver.model;

import lombok.*;
import org.springframework.data.relational.core.mapping.Table;

@Table("events")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Builder(toBuilder = true)
public class Event extends BaseEntity {
    private User user;
    private FileEntity file;
}