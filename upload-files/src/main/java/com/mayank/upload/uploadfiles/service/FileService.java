package com.mayank.upload.uploadfiles.service;

import com.mayank.upload.uploadfiles.domain.FileDetails;
import com.mayank.upload.uploadfiles.dto.Request;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface FileService {

    FileDetails saveFile(MultipartFile file) throws IOException;

    FileDetails getFile(String id);

    List<FileDetails> getAllFiles();

    FileDetails updateFile(MultipartFile file, String name) throws IOException;

    FileDetails restoreFileVersion(String name, String version) throws IOException;

    String getFileContentsByName(String name, String version);

    FileDetails updateFile(Request requestDto, String name);
}