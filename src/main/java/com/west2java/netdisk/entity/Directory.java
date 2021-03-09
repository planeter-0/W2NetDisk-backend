package com.west2java.netdisk.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "directory")
@Schema(name="Directory", title="目录模型")
@JsonIgnoreProperties(value={"hibernateLazyInitializer","handler","fieldHandler"})
public class Directory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name="parent_id")
    private Integer parentId;
    private String name;
    @Column(name="user_id")
    private Integer userId;
    @Transient
    private List<Directory> children;
    public Directory(Integer id,Integer parentId, String name) {
        this.id = id;
        this.parentId = parentId;
        this.name = name;
    }
    public Directory(String name,Integer parentId,  Integer userId) {
        this.name = name;
        this.parentId = parentId;
        this.userId = userId;
    }
    //新建文件夹
    public Directory(String name,Integer parentId) {
        this.name = name;
        this.parentId = parentId;
    }
    //新建根文件夹
    public Directory(String name,  int userId) {
        this.parentId = null;
        this.name = name;
        this.userId = userId;
    }
}
