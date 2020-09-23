package com.mayank.upload.uploadfiles.service.impl;

import com.mayank.upload.uploadfiles.domain.FileDetails;
import com.mayank.upload.uploadfiles.dto.Request;
import com.mayank.upload.uploadfiles.repository.FileRepository;
import com.mayank.upload.uploadfiles.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class FileServiceImpl implements FileService {

    @Autowired
    private FileRepository fileRepository;

    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
    DecimalFormat df2 = new DecimalFormat("#.##");

    @Override
    public FileDetails saveFile(MultipartFile file) throws IOException {
        List<FileDetails> fileDetails = fileRepository.findAll();
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        Optional<FileDetails> optionalFileDetails = fileDetails.stream().filter(i -> i.getName().equals(fileName))
                .findFirst();
        FileDetails files = null;
        if (!optionalFileDetails.isPresent()) {
            files = fileRepository.save(new FileDetails(1.0, fileName, file.getContentType(), file.getBytes(), false,
                    dtf.format(LocalDateTime.now()), dtf.format(LocalDateTime.now())));
        }
        return files;
    }

    @Override
    public FileDetails getFile(String name) {
        return fileRepository.findByNameByOrderByVersionDesc(name, false).get(0);
    }

    @Override
    public List<FileDetails> getAllFiles() {
        List<Object[]> details = fileRepository.findAllFiles(false);

        List<FileDetails> fileDetails = new ArrayList<>();
        for(Object[] detail : details){
            fileDetails.add(new FileDetails(Double.valueOf(String.valueOf(detail[7])), String.valueOf(detail[4]),
                    String.valueOf(detail[6]), (byte[])detail[1], (Boolean)detail[3], String.valueOf(detail[5]),
                    String.valueOf(detail[2]), String.valueOf(detail[8]), ((BigInteger)detail[0]).longValue()));
        }

        return fileDetails;
    }

    @Override
    public FileDetails updateFile(MultipartFile file, String name) throws IOException {
        FileDetails fileDetails = fileRepository.findByNameByOrderByVersionDesc(name, false).get(0);
        FileDetails files = null;
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        if (fileName.equals(fileDetails.getName())) {
            if(!Arrays.equals(file.getBytes(), fileDetails.getData())){
                double version = fileDetails.getVersion();
                files = new FileDetails(Double.parseDouble(df2.format(version + 0.1)), fileName, file.getContentType(),
                        file.getBytes(), false, fileDetails.getStartTime(), dtf.format(LocalDateTime.now()));
                return fileRepository.save(files);
            }
        } else {
            files = saveFile(file);
        }

        return files;
    }

    @Override
    public FileDetails restoreFileVersion(String name, String version) throws IOException {
        List<FileDetails> fileDetailsList = fileRepository.findByNameByOrderByVersionDesc(name, false);

        FileDetails files = null;
        if (!fileDetailsList.isEmpty()) {
            fileDetailsList.stream().filter(i -> i.getVersion() > Double.parseDouble(version))
                    .forEach(updateFile -> updateFile.setDelete(true));
            fileRepository.saveAll(fileDetailsList);
            files = fileRepository.findByNameByOrderByVersionDesc(name, false).get(0);
        }
        return files;
    }

    @Override
    public String getFileContentsByName(String name, String version) {
        Optional<FileDetails> fileDetails = fileRepository.findByNameIgnoreCaseByOrderByVersionDesc(name, version, false);
        if(fileDetails.isPresent()){
            return new String(fileDetails.get().getData());
        }else {
            return null;
        }
    }

    @Override
    public FileDetails updateFile(Request requestDto, String name) {
        FileDetails fileDetails = fileRepository.findByNameByOrderByVersionDesc(name, false).get(0);
        FileDetails files = null;

        double version = fileDetails.getVersion();
        files = new FileDetails(Double.parseDouble(df2.format(version + 0.1)), name, "String",
                requestDto.getDataValue().getBytes(), false, fileDetails.getStartTime(), dtf.format(LocalDateTime.now()));
        return fileRepository.save(files);
    }
}