package com.west2java.netdisk.dao;

import com.west2java.netdisk.entity.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FileDao extends JpaRepository<File,Integer> {
    // 查询用户上传的所有文件(参数  :paraname 或 ?paraindex)
    @Query(value = "SELECT * FROM file WHERE user_id = ?1",nativeQuery = true)
    List<File> getFilesByUserId(Integer userId);
    List<File> findByDirectoryId(Integer directoryId);
    List<File> findByIsAvailableEquals(boolean isAvailable);

    List<File> findByUserIdEquals(Integer userId);
}
