package com.github.gluhov.cloudfileserver.model;

import lombok.*;
import org.springframework.data.relational.core.mapping.Table;

@Table("files")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Builder(toBuilder = true)
public class FileEntity extends BaseEntity {
    private String location;
}