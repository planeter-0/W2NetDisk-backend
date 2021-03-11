package com.west2java.netdisk.controller;

import com.west2java.netdisk.result.ExceptionMsg;
import com.west2java.netdisk.result.ResponseData;
import com.west2java.netdisk.service.FileService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@Slf4j
@Tag(name = "admin-api", description = "后台管理员的各项服务")
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    FileService fileService;

    @Operation(summary = "分页获取未审核文件列表",
            parameters = {
                    @Parameter(name = "pageNumber", description = "页码", in = ParameterIn.QUERY)
            },
            responses = {@ApiResponse(responseCode = "200", description = "操作成功")})
    @GetMapping("/unreviewed")
    public ResponseData unreviewed(@RequestParam Integer pageNumber) {
        return new ResponseData(ExceptionMsg.SUCCESS, fileService.getUnreviewedInPage(pageNumber - 1));
    }


    @Operation(summary = "批量审核图片",
            parameters = {
                    @Parameter(name = "isPass", description = "审核是否通过"),
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "操作成功"),
                    @ApiResponse(responseCode = "999999", description = "操作失败")})
    @PostMapping("/review")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "整型文件id列表",required = true)
    public ResponseData review(@RequestBody List<Integer> fileIdList, @RequestParam Boolean isPass) {
        try {
            for (Integer fileId : fileIdList) {
                fileService.review(fileId, isPass);
            }
            return new

                    ResponseData(ExceptionMsg.SUCCESS, fileService.getFiles(fileIdList));
        } catch (Exception e) {
            log.warn("审核文件失败");
            e.printStackTrace();
            return new ResponseData(ExceptionMsg.FAILED);
        }

    }
}
