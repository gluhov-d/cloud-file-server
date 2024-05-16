package com.github.gluhov.cloudfileserver.rest.file;

import com.github.gluhov.cloudfileserver.model.FileEntity;
import com.github.gluhov.cloudfileserver.model.Status;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class FileEntityTestData {
    public static final long FILE_ID = 1;
    public static final long FILE_NOT_FOUND_ID = 100;

    public static final FileEntity fileAdmin = new FileEntity(FILE_ID, Status.ACTIVE, LocalDateTime.now(), LocalDateTime.now(),  "1", "1", "https://gluhov-file-storage.s3.amazonaws.com/2023-11-03%2010.52.36.jpg", "2023-11-03 10.52.36.jpg", 1L);
    public static final FileEntity fileUser = new FileEntity(FILE_ID+1, Status.ACTIVE, LocalDateTime.now(), LocalDateTime.now(),  "2", "2", "https://gluhov-file-storage.s3.amazonaws.com/2023-11-03%2010.52.36.jpg", "2023-11-03 10.52.36.jpg", 2L);
    public static final FileEntity fileModerator = new FileEntity(FILE_ID+2, Status.ACTIVE, LocalDateTime.now(), LocalDateTime.now(),  "3", "3","https://gluhov-file-storage.s3.amazonaws.com/2023-11-03%2010.52.36.jpg", "2023-11-03 10.52.36.jpg", 3L);

    public static final List<FileEntity> adminFiles = Arrays.asList(fileAdmin);

    public static FileEntity getUpdated() { return new FileEntity(FILE_ID+1, Status.ACTIVE, LocalDateTime.now(), LocalDateTime.now(), "2", "2", "https://gluhov-file-storage.s3.amazonaws.com/2023-11-03%2010.52.36.jpg", "Updated", 2L);}
}
