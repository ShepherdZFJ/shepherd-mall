package com.shepherd.mallbase.api.controller;

import com.shepherd.mall.annotation.ResponseResultBody;
import com.shepherd.mallbase.api.service.FileService;
import com.shepherd.mallbase.dto.FastDFSFile;
import com.shepherd.mallbase.util.FastDFSClient;
import io.swagger.annotations.Api;
import org.apache.ibatis.annotations.Delete;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * @author fjzheng
 * @version 1.0
 * @date 2020/12/28 23:43
 */
@RestController
//@ResponseResultBody
@RequestMapping("/api/mall/base/file")
@Api("文件相关接口")

@CrossOrigin//支持跨域

//跨域:
//不同的域名A 访问 域名B 的数据就是跨域
// 端口不同 也是跨域  loalhost:18081----->localhost:18082
// 协议不同 也是跨域  http://www.jd.com  --->  https://www.jd.com
// 域名不同 也是跨域  http://www.jd.com  ---> http://www.taobao.com
//协议一直,端口一致,域名一致就不是跨域  http://www.jd.com:80 --->http://www.jd.com:80 不是跨域
public class FileController {
    @Resource
    private FileService fileService;

    /**
     * 返回 图片的全路径
     *
     * @param file 页面的文件对象
     * @return
     */
    @PostMapping()
    public String upload(@RequestParam(value = "file") MultipartFile file) {
        try {
            //1. 创建图片文件对象(封装)
            //2. 调用工具类实现图片上传

            //String substring = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1);

            FastDFSFile fastdfsfile = new FastDFSFile(
                    file.getOriginalFilename(),//原来的文件名  1234.jpg
                    file.getBytes(),//文件本身的字节数组
                    StringUtils.getFilenameExtension(file.getOriginalFilename())
            );
            String[] upload = FastDFSClient.upload(fastdfsfile);

            //  upload[0] group1
            //  upload[1] M00/00/00/wKjThF1aW9CAOUJGAAClQrJOYvs424.jpg
            //3. 拼接图片的全路径返回

            // http://192.168.211.132:8080/group1/M00/00/00/wKjThF1aW9CAOUJGAAClQrJOYvs424.jpg

            // http://192.168.211.132:8080  +
            return FastDFSClient.getTrackerUrl()+"/"+upload[0]+"/"+upload[1];
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @PostMapping("/oss")
    public String uploadOSS(@RequestParam(value = "file") MultipartFile file) {
        String url = fileService.uploadFileToOSS(file);
        return url;
    }

    @DeleteMapping("/oss/{fileName}")
    public void deleteOSSFile(@PathVariable("fileName") String fileName) {
        fileService.deleteOSSFile(fileName);
    }
}
