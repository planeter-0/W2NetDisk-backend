package com.west2java.netdisk.controller;

import com.alibaba.fastjson.JSONObject;
import com.west2java.netdisk.result.ExceptionMsg;
import com.west2java.netdisk.result.ResponseData;
import com.west2java.netdisk.service.DirectoryService;
import com.west2java.netdisk.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.west2java.netdisk.util.FileUtil;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@Slf4j
@Tag(name = "file-api", description = "文件相关的api")
public class FileController {
    @Autowired
    FileService fileService;
    @Autowired
    DirectoryService directoryService;
    @Value("${filepath}")
    String filepath;//文件存储的根路径
    @Value("${disksize}")
    Long disksize;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");

    /**
     * 多文件上传 v-v
     *
     * @param files
     * @return
     * @throws IOException
     */
    @Operation(summary = "上传多个文件",
            parameters = {
                    @Parameter(name = "dirId", description = "目录id", in = ParameterIn.QUERY),
                    @Parameter(name = "userId", description = "用户id", in = ParameterIn.QUERY),
                    @Parameter(name = "files", description = "文件(表单)")
            },
            responses = {@ApiResponse(responseCode = "200", description = "操作成功")})
    @PostMapping(value = "/upload")
    public ResponseData doUploadFile(@RequestParam MultipartFile[] files, @RequestParam Integer dirId, @RequestParam Integer userId) throws IOException {
        if(fileService.getUsedSize(userId)<disksize) {
            ArrayList<com.west2java.netdisk.entity.File> list = new ArrayList<>();
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    //文件信息
                    String originalFilename = file.getOriginalFilename();
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.MILLISECOND, 0);//将毫秒数设为0
                    Date uploadDate = calendar.getTime();
                    String date = sdf.format(uploadDate);
                    Long size = file.getSize();
                    //文件上传，将文件流拷贝到目标文件对象中，加时间戳
                    FileUtils.copyInputStreamToFile(file.getInputStream(),
                            new File(filepath, date + originalFilename));
                    //数据库操作
                    com.west2java.netdisk.entity.File entity;
                    entity = new com.west2java.netdisk.entity.File(originalFilename, size, dirId, userId, uploadDate);
                    list.add(fileService.save(entity));
                } else {
                    log.warn("空文件");
                    return new ResponseData(ExceptionMsg.FileEmpty,file);
                }
            }
            log.info("上传文件成功");
            return new ResponseData(ExceptionMsg.SUCCESS, list);
        }
        return new ResponseData(ExceptionMsg.LimitSize);
    }

    /**
     * 文件下载,需要文件id v-v
     */
    @Operation(summary = "下载文件",
            parameters = {
                    @Parameter(name = "fileId", description = "文件id", in = ParameterIn.PATH),
            },
            responses = {@ApiResponse(responseCode = "200", description = "操作成功")})
    @GetMapping("/download/{fileId}")
    public void downLoad(HttpServletResponse response, @PathVariable Integer fileId) throws UnsupportedEncodingException {
        com.west2java.netdisk.entity.File entity = fileService.getFile(fileId);
        File file = FileUtil.transfer(entity);
        log.info("待下载文件路径"+file.toPath());
        //检查文件是否存在以及是否审核通过
        if (file.exists() && entity.getIsAvailable()) {
            //设置下载完毕不打开文件
            response.setContentType("application/force-download");
            //设置文件名
            response.setHeader("Content-Disposition", "attachment;filename=" + new String(entity.getName().getBytes("utf-8"), "ISO8859-1"));
            //输出流
            OutputStream out = null;
            try {
                out = response.getOutputStream();
                out.write(FileUtils.readFileToByteArray(file));
                out.flush();
//                return new ResponseData(ExceptionMsg.SUCCESS);
            } catch (IOException e) {
                log.warn("文件io失败");
                e.printStackTrace();
            } finally {
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    log.warn("输出流为null");
                }
            }
        }
        log.info("文件不存在");
    }

    /**
     * 文件预览,需要文件id v-v
     */
    @Operation(summary = "预览文件",
            parameters = {
                    @Parameter(name = "fileId", description = "文件id", in = ParameterIn.PATH),
            },
            responses = {@ApiResponse(responseCode = "200", description = "操作成功")})
    @GetMapping("/view/{fileId}")
    public void view(HttpServletResponse response, @PathVariable Integer fileId) throws UnsupportedEncodingException {
        com.west2java.netdisk.entity.File entity = fileService.getFile(fileId);
        File file = FileUtil.transfer(entity);
        //检查文件是否存在以及是否审核通过
        if (file.exists()) {
            //设置下载完毕不打开文件
            response.setContentType(MediaType.IMAGE_JPEG_VALUE);
            //设置文件名
//            response.setHeader("Content-Disposition", "attachment;filename=" + new String(entity.getName().getBytes("utf-8"), "ISO8859-1"));
            //输出流
            OutputStream out = null;
            try {
                out = response.getOutputStream();
                out.write(FileUtils.readFileToByteArray(file));
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        log.warn("预览文件失败");
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * 文件重命名,需要文件id v-v
     */
    @Operation(summary = "重命名文件",
            parameters = {
                    @Parameter(name = "fileId", description = "文件id"),
                    @Parameter(name = "newName", description = "新名"),
            },
            responses = {@ApiResponse(responseCode = "200", description = "操作成功")})
    @PutMapping("/rename")
    public ResponseData rename(@RequestBody String jsonStr) {
        JSONObject json = JSONObject.parseObject(jsonStr);
        Integer fileId = json.getInteger("fileId");
        String newName = json.getString("newName");
        fileService.rename(fileId, newName);
        return new ResponseData(ExceptionMsg.SUCCESS, fileService.getFile(fileId));
    }

    /**
     * 多文件删除,需要文件id整形列表[] v-v
     */
    @Operation(summary = "删除多个文件",
            responses = {@ApiResponse(responseCode = "200", description = "操作成功")})
    @PostMapping("/delete")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "整型文件id列表",required = true)
    public ResponseData delete(@RequestBody List<Integer> fileIdList) throws IOException {
        fileService.delete(fileIdList);
        return new ResponseData(ExceptionMsg.SUCCESS, fileIdList);
    }

    /**
     * 某用户某目录分页文件，参数pageNumber v-v
     * 时间降序，每页12
     */
    @Operation(summary = "分页查询文件",
            description = "获取某用户某目录文件页",
            parameters = {
                    @Parameter(name = "userId", description = "用户id",in = ParameterIn.QUERY),
                    @Parameter(name = "dirId", description = "目录id",in = ParameterIn.QUERY),
                    @Parameter(name = "pageNumber", description = "页码",in = ParameterIn.QUERY)
            },
            responses = {@ApiResponse(responseCode = "200", description = "操作成功")})
    @GetMapping("/page")
    public ResponseData getFiles(@RequestParam Integer userId,@RequestParam Integer dirId,@RequestParam Integer pageNumber) {
        return new ResponseData(ExceptionMsg.SUCCESS, fileService.getFilesInPageByUserId(userId, pageNumber - 1, dirId));
    }

    /**
     * 某用户文件总大小
     */
    @Operation(summary = "获取某用户网盘使用大小",
            parameters = {
                    @Parameter(name = "userId", description = "用户id",in = ParameterIn.QUERY),
            },
            responses = {@ApiResponse(responseCode = "200", description = "操作成功")})
    @GetMapping("/used")
    public ResponseData getUsedSize(@RequestParam Integer userId) {
        return new ResponseData(ExceptionMsg.SUCCESS, fileService.getUsedSize(userId));
    }
}
