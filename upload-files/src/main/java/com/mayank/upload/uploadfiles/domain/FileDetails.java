package com.mayank.upload.uploadfiles.domain;

import javax.persistence.*;

@Entity
@Table(name = "filesdetails")
public class FileDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private double version;

    private String name;

    private String type;

    @Lob
    private byte[] data;

    private Boolean isDelete;

    private String startTime;

    private String endTime;

    @Transient
    private String versions;

    public FileDetails() {
    }

    public FileDetails(double version, String name, String type, byte[] data, Boolean isDelete, String startTime,
                       String endTime,String versions, long id) {
        this.version = version;
        this.name = name;
        this.type = type;
        this.data = data;
        this.isDelete = isDelete;
        this.startTime = startTime;
        this.endTime = endTime;
        this.versions = versions;
        this.id = id;
    }

    public FileDetails(double version, String name, String type, byte[] data, Boolean isDelete, String startTime,
                       String endTime) {
        this.version = version;
        this.name = name;
        this.type = type;
        this.data = data;
        this.isDelete = isDelete;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getVersions() {
        return versions;
    }

    public void setVersions(String versions) {
        this.versions = versions;
    }

    public double getVersion() {
        return version;
    }

    public void setVersion(double version) {
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public Boolean getDelete() {
        return isDelete;
    }

    public void setDelete(Boolean delete) {
        isDelete = delete;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public Long getId() {
        return id;
    }
}