package com.west2java.netdisk.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "file")
@Schema(name="File", title="文件模型")
@JsonIgnoreProperties(value={"hibernateLazyInitializer","handler","fieldHandler"})
public class File {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "size", nullable = false)
    private Long size;
    @Column(name = "directory_id", nullable = false)
    private Integer directoryId;
    @Column(name = "is_available")
    private Boolean isAvailable;
    @Column(name = "user_id",nullable = false)
    private Integer userId;
    @Column(name = "upload_time",nullable = false)
    private Date uploadTime;
    public File(String name, Long size, Integer directoryId, Integer userId) {
        this.name = name;
        this.size = size;
        this.directoryId = directoryId;
        this.isAvailable = false;//默认值
        this.userId = userId;
        this.uploadTime = new Date();//默认值
    }
    //新建文件用
    public File(String name, Long size, Integer directoryId,Integer userId,Date uploadTime) {
        this.name = name;
        this.size = size;
        this.directoryId = directoryId;
        this.isAvailable = Boolean.FALSE;//默认值
        this.userId = userId;
        this.uploadTime = uploadTime;//默认值
    }
    //文件example用
    public File(Integer directoryId, Integer userId) {
        this.directoryId = directoryId;
        this.userId = userId;
    }
}
