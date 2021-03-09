package com.west2java.netdisk.service;

import com.west2java.netdisk.dao.DirectoryDao;
import com.west2java.netdisk.entity.Directory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import com.west2java.netdisk.util.TreeBuilder;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.List;

@Service
public class DirectoryService {
    @Autowired
    DirectoryDao directoryDao;
    @Autowired
    FileService fileService;//耦合了。。

    /**
     * 新建文件夹 v
     *
     * @param dir
     * @return
     */
    public Directory create(Directory dir) {
        return directoryDao.save(dir);
    }

    /**
     * 删除文件夹及其下文件(不含子孙文件夹
     *
     * @param dirId
     */
    public void delete(Integer dirId) throws IOException {
        Directory dir = directoryDao.getOne(dirId);
        //删除文件夹
        directoryDao.delete(dir);
        //删除其下文件
        fileService.deleteByDirId(dir.getId());
    }

    public void delete(Directory dir) throws IOException {
        //删除文件夹
        directoryDao.delete(dir);
        //删除其下文件
        fileService.deleteByDirId(dir.getId());
    }

    /**
     * 重命名文件夹
     *
     * @param newName
     * @param id
     */
    @Transactional
    public void rename(String newName, Integer id) {
        directoryDao.updateNameById(newName, id);
    }

    /**
     * 获取完整目录树(json)
     *
     * @param userId
     * @return
     */
    public String directoryTree(Integer userId) {
        //用ExampleMatcher查询
        Directory dir = new Directory();
        //构造查询条件
        dir.setUserId(userId);
        Example<Directory> example = Example.of(dir);
        List<Directory> list = directoryDao.findAll(example);
        TreeBuilder treeBuilder = new TreeBuilder(list);
        String json = treeBuilder.buildJSONTree();
        return json;
    }

    /**
     * 获取树形结构列表
     *
     * @param rootId
     * @return
     */
    public Directory getDescendant(Integer rootId) {
        Directory rootNode = directoryDao.getOne(rootId);
        buildChildNodes(rootNode);
        return rootNode;
    }

    // 递归子节点
    public void buildChildNodes(Directory node) {
        List<Directory> children = getChildNodes(node);
        if (!children.isEmpty()) {
            for (Directory child : children) {
                buildChildNodes(child);//递归寻找子节点直至子节点为空
            }
            node.setChildren(children);
        }
    }
    public Directory getRootDirByUserId(Integer userId){
        List<Directory> dirsList = directoryDao.findByNameAndUserId("root",userId);
        Directory rootDir=null;
        for (Directory dir:dirsList){
             rootDir = dir;
        }
        return  rootDir;
    }
    // 获取父节点下所有的子节点
    public List<Directory> getChildNodes(Directory pnode) {
        return directoryDao.findByParentIdEquals(pnode.getId());
    }

    public Directory getOne(Integer dirId) {
        return directoryDao.getOne(dirId);
    }

    public List<Directory> findByUserIdEquals(Integer userId) {
        return directoryDao.findByUserIdEquals(userId);
    }


}
