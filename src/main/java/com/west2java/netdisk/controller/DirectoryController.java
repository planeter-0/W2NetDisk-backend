package com.west2java.netdisk.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.west2java.netdisk.entity.Directory;
import com.west2java.netdisk.result.ExceptionMsg;
import com.west2java.netdisk.result.ResponseData;
import com.west2java.netdisk.service.DirectoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.west2java.netdisk.util.ListBuilder;

import java.io.IOException;
import java.util.List;
@Tag(name = "directory-api", description = "目录相关的api")
@RestController
@Slf4j
@RequestMapping("/dir")
public class DirectoryController {
    @Autowired
    private DirectoryService directoryService;
    /**
     * 新建 v-v
     */
    @Operation(summary = "新建目录",
            parameters = {
                    @Parameter(name = "parentId", description = "父目录id"),
                    @Parameter(name = "name", description = "目录名"),
                    @Parameter(name = "userId", description = "所属用户id")
            },
            responses = {@ApiResponse(responseCode = "200", description = "操作成功")})
    @PostMapping("/create")
    public ResponseData create(@RequestBody Directory dir) {
        return new ResponseData(ExceptionMsg.SUCCESS,directoryService.create(dir));
    }

    /**
     * 文件夹删除 v-v
     */
    @Operation(summary = "删除目录",
            description= "删除该目录，该目录下的所有文件，后代文件夹以及后代文件夹的所有文件",
            parameters = {
                    @Parameter(name = "dirId", description = "目录id",in = ParameterIn.QUERY),
            },
            responses = {@ApiResponse(responseCode = "200", description = "操作成功")})
    @DeleteMapping("/delete")
    public ResponseData delete(@RequestParam Integer dirId) throws IOException {
        Directory tree = directoryService.getDescendant(dirId);
        ListBuilder listBuilder = new ListBuilder();
        listBuilder.toList(tree);
        List<Directory> descendant = listBuilder.list;
        //删除子孙
        if (!descendant.isEmpty()) {
            for (Directory dir : descendant) {
                directoryService.delete(dir);
            }
        }
        //删除自己
        directoryService.delete(dirId);
        return new ResponseData(ExceptionMsg.SUCCESS);
    }

    /**
     * 重命名 v-v
     */
    @Operation(summary = "重命名目录",
            parameters = {
                    @Parameter(name = "dirId", description = "目录id",in = ParameterIn.QUERY),
                    @Parameter(name = "newName", description = "新名",in = ParameterIn.QUERY),
            },
            responses = {@ApiResponse(responseCode = "200", description = "操作成功")})
    @PutMapping("/rename")
    public ResponseData rename(@RequestParam Integer dirId,@RequestParam String newName) {
        directoryService.rename(newName,dirId);
        Directory newDir =  directoryService.getOne(dirId);
        return new ResponseData(ExceptionMsg.SUCCESS,newDir);
    }
//    /**
//     * 目录树获取 v-v
//     */
//    @Operation(summary = "获取目录树",
//            description= "获取某用户的目录树，根节点组成的树形json列表",
//            parameters = {
//                    @Parameter(name = "userId", description = "用户id"),
//            },
//            responses = {@ApiResponse(responseCode = "200", description = "操作成功")})
//    @GetMapping("/tree")
//    public ResponseData getTree(@RequestBody String jsonStr){
//        JSONObject json = JSONObject.parseObject(jsonStr);
//        Integer userId = json.getInteger("userId");
//        List<Directory> nodes = directoryService.findByUserIdEquals(userId);
//        TreeBuilder treeBuilder = new TreeBuilder(nodes);
//        String tree = treeBuilder.buildJSONTree();
//        JSONArray jsonTree =  JSONArray.parseArray(tree);
//        return new ResponseData(ExceptionMsg.SUCCESS,jsonTree);
//    }
    /**
     * 目录树获取
     */
    @Operation(summary = "获取目录树",
            description= "以某目录为根节点，获取由其子节点组成的树形json列表",
            parameters = {
                    @Parameter(name = "dirId", description = "目录id"),
            },
            responses = {@ApiResponse(responseCode = "200", description = "操作成功")})
    @GetMapping("/tree")
    public ResponseData getChildTree(@RequestParam Integer dirId){
        List<Directory> childTree = directoryService.getDescendant(dirId).getChildren();
        String tree = JSONArray.toJSONString(childTree);
        JSONArray jsonTree =  JSONArray.parseArray(tree);
        return new ResponseData(ExceptionMsg.SUCCESS,jsonTree);
    }
}
