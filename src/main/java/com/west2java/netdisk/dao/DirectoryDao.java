package com.west2java.netdisk.dao;

import com.west2java.netdisk.entity.Directory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface DirectoryDao extends JpaRepository<Directory,Integer> {
    //原生sql更新
    @Modifying//声明执行的SQL语句是更新（增删改）操作
    @Transactional//提供事务支持,默认情况下JPA的每个操作都是事务的
    @Query("UPDATE directory SET name = :name WHERE id = :id")
    void updateNameById(String name, Integer id);
    //约定方法名
    List<Directory> findByParentIdEquals(Integer dirId);
    List<Directory> findByUserIdEquals(Integer dirId);
    List<Directory> findByNameAndUserId(String name, Integer userId);
}
