package com.mayank.upload.uploadfiles.controller;

import com.mayank.upload.uploadfiles.domain.FileDetails;
import com.mayank.upload.uploadfiles.dto.Request;
import com.mayank.upload.uploadfiles.dto.Response;
import com.mayank.upload.uploadfiles.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/file")
public class FileController {

    @Autowired
    private FileService fileService;

    @GetMapping
    public ModelAndView getIndex(){

        ModelAndView mav = new ModelAndView("index");

        List<FileDetails> fileDetails = fileService.getAllFiles();

        if (!fileDetails.isEmpty()) {

            try{

                List<Response> files = fileDetails.stream().filter(i->i.getName().equals(i.getName())).map(dbFile -> {

                    return new Response(dbFile.getName(), "", dbFile.getType(), dbFile.getData()!=null?dbFile.getData().length: 0,
                            dbFile.getId(), dbFile.getVersion(), dbFile.getDelete(), dbFile.getVersions().indexOf(",")==-1?new String[]{dbFile.getVersions()}:dbFile.getVersions().split(","));
                }).collect(Collectors.toList());

                mav.addObject("files", files);
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        return mav;
    }

    // upload a .txt file of max size upto 5MB
    @PostMapping(value = "/upload", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> uploadFile(MultipartFile file) {
        String message = "";
        if (file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1)
                .equalsIgnoreCase("txt")) {
            FileDetails fileDetails = null;
            try {
                fileDetails = fileService.saveFile(file);
            } catch (Exception e) {
                message = "Could not upload the file: " + file.getOriginalFilename() + "!";
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(message);
            }
            if (Objects.isNull(fileDetails)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("File with name: " + file.getOriginalFilename() + " already exist");
            }
            message = "Uploaded the file successfully: " + file.getOriginalFilename();
            return new ResponseEntity<>(message, HttpStatus.OK);

        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Only .txt files are allowed");
        }
    }

    //will fetch details of all files those are not removed
    @GetMapping(value = "/files", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Response>> getListFiles() {

        List<FileDetails> fileDetails = fileService.getAllFiles();

        if (!fileDetails.isEmpty()) {

            List<Response> files = fileDetails.stream().map(dbFile -> {
                return new Response(dbFile.getName(), "", dbFile.getType(), dbFile.getData()!=null? dbFile.getData().length:0,
                        dbFile.getId(), dbFile.getVersion(), dbFile.getDelete(), Objects.isNull(dbFile.getVersions())?new String[0]:dbFile.getVersions().split(","));
            }).collect(Collectors.toList());

            return ResponseEntity.status(HttpStatus.OK).body(files);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    //hit from browser we are able to download the file
    @GetMapping(value = "/files/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<byte[]> getFile(@PathVariable String name) {
        FileDetails files = fileService.getFile(name);
        if (Objects.isNull(files))
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + files.getName() + "\"")
                .body(files.getData());
    }

    // to change the content of the file, Doing it as a POC or task i am not storing the data of a file to system. instead i am converting it to byte and store it to db
    @PutMapping(value = "/updatefile/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> updateFile(@RequestParam("file") MultipartFile file, @PathVariable String name) {
        String message = "";
        try {

            if (file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1)
                    .equalsIgnoreCase("txt")) {
                FileDetails fileDetails = fileService.updateFile(file, name);
                if (Objects.isNull(fileDetails)) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body("Content of file : " + file.getOriginalFilename() + " is duplicate with existing one");
                }

                message = "File updated successfully: " + file.getOriginalFilename();
                // for json we can go with this
                return ResponseEntity.status(HttpStatus.OK).body(message);
                /*
                 * else to return updated file we can use return ResponseEntity.ok()
                 * .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + files.getName() + "\"")
                 * .body(fileDetails.getData())
                 */
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Only .txt files are allowed");
            }
        } catch (Exception e) {
            message = "Could not update the file: " + file.getOriginalFilename() + "!";
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(message);
        }
    }

    // will restore the
    @PutMapping(value = "/restoreversion/{name}/{version}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> restoreFileVersion(@PathVariable String name,
                                                     @PathVariable String version) {
        String message = "";
        try {

            FileDetails fileDetails = fileService.restoreFileVersion(name, version);
            if (Objects.isNull(fileDetails)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No file with name " + name + " is found");
            }

            message = "File " + name + "restore to version "+ version+ " successfully: ";
            // for json we can go with this
            return ResponseEntity.status(HttpStatus.OK).body(message);
            /*
             * else to return updated file we can use return ResponseEntity.ok()
             * .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + files.getName() + "\"")
             * .body(fileDetails.getData())
             */
        } catch (Exception e) {
            message = "Could not restore the file: " + name + "!";
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(message);
        }
    }

    @GetMapping(value = "/{name}/{version}")
    public ResponseEntity getFileContentByName(@PathVariable String name,@PathVariable String version){
        String data = fileService.getFileContentsByName(name, version);
        if(Objects.isNull(data)){
            return new ResponseEntity( "Data Not Found", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(data, HttpStatus.OK);
    }

    // to change the content of the file, Doing it as a POC or task i am not storing the data of a file to system. instead i am converting it to byte and store it to db
    @PutMapping(value = "/updatefiledata/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> updateFileData(@RequestBody Request requestDto, @PathVariable String name) {
        String message = "";
        try {
            FileDetails fileDetails = fileService.updateFile(requestDto, name);

            message = "File updated successfully: " + name;
            // for json we can go with this
            return ResponseEntity.status(HttpStatus.OK).body(message);
        } catch (Exception e) {
            message = "Could not update the file: " + name + "!";
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(message);
        }
    }

}