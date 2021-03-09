package com.west2java.netdisk.service;

import com.west2java.netdisk.dao.DirectoryDao;
import com.west2java.netdisk.dao.FileDao;
import com.west2java.netdisk.entity.File;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import com.west2java.netdisk.util.FileUtil;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

@Service
public class FileService {
    @Autowired
    FileDao fileDao;
    @Autowired
    DirectoryDao DirectoryDao;
    @Value("${filepath}")
    private String filepath;//文件存储的根路径
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");

//FileController
    /**
     * 文件上传 v
     *
     * @param file
     * @return
     */
    public File save(File file) {
        return fileDao.save(file);
    }


    /**
     * 文件重命名 v
     *
     * @param fileId
     * @param newName
     */
    public void rename(Integer fileId, String newName) {
        File entity = fileDao.getOne(fileId);
        String oldName = entity.getName();//旧文件名
        entity.setName(newName);
        File oldEntity = new File(entity.getId(),oldName,entity.getSize(), entity.getDirectoryId(), entity.getIsAvailable(), entity.getUserId(),entity.getUploadTime());
        File newEntity = new File(entity.getId(),newName,entity.getSize(), entity.getDirectoryId(), entity.getIsAvailable(), entity.getUserId(),entity.getUploadTime());
        //修改文件名
        java.io.File oldFile = FileUtil.transfer(oldEntity);
        java.io.File newFile = FileUtil.transfer(newEntity);
        boolean flag = oldFile.renameTo(newFile);
        if (flag) fileDao.save(entity);//保存，注意事务
    }

    /**
     * 根据fileId删除多文件 v
     *
     * @param fileIdList
     */
    public void delete(List<Integer> fileIdList) throws IOException {
        List<File> fileList = fileDao.findAllById(fileIdList);
        //删除文件
        for (File f:fileList) {
            FileUtils.forceDelete(FileUtil.transfer(f));
        }
        //表修改
        fileDao.deleteInBatch(fileList);
    }


    /**
     * 用户文件分页分目录查询 v
     *
     * @param userId
     * @param page
     * @param dirId
     * @return Page
     */
    public Page<File> getFilesInPageByUserId(Integer userId, int page, Integer dirId) {
        File fileExample = new File(dirId,userId);
        //定义example
        Example<File> example = Example.of(fileExample);
        //时间降序分页
        Sort sort = Sort.by(Sort.Direction.DESC, "uploadTime");
        Pageable pageable = PageRequest.of(page, 12, sort);
        return fileDao.findAll(example, pageable);
    }
//AdminController
    /**
     * 获取未审核文件列表 v
     *
     * @return
     */
    public Page<File> getUnreviewedInPage(int page) {
        File fileExample = new File();
        fileExample.setIsAvailable(false);
        //定义example
        Example<File> example = Example.of(fileExample);
        //时间降序分页
        Sort sort = Sort.by(Sort.Direction.DESC, "uploadTime");
        Pageable pageable = PageRequest.of(page, 12, sort);
        return fileDao.findAll(example, pageable);
    }
    /**
     * 审核，修改文件可用状态 v
     *
     * @return
     */
    public void review(Integer fileId, boolean isPass) {
        File f = fileDao.getOne(fileId);
        f.setIsAvailable(isPass);
        fileDao.save(f);
    }
//DirectoryController
    /**
     * 根据dirId删除多文件，供DirectoryController
     *
     * @param dirId
     * @return 文件实体列表
     */
    public void deleteByDirId(Integer dirId) throws IOException {
        List<File> fileList = fileDao.findByDirectoryId(dirId);
        //删除文件
        for (File f:fileList) {
            FileUtils.forceDelete(FileUtil.transfer(f));
        }
        //表修改
        fileDao.deleteInBatch(fileList);
    }
    /**
     * 某用户文件总大小
     *
     * @param userId
     * @return 总大小
     */
    public long getUsedSize(Integer userId){
        long total = 0;
        List<File> files =  fileDao.findByUserIdEquals(userId);
        for(File f:files){
            total += f.getSize();
        }
        return total;
    }
//工具
    //根据id查询文件
    public File getFile(Integer fileId) {
        return fileDao.getOne(fileId);
    }
    public List<File> getFiles(List<Integer> fileIdList){
        return fileDao.findAllById(fileIdList);
    }
}
