package com.shepherd.mallbase.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author fjzheng
 * @version 1.0
 * @date 2020/12/28 23:40
 */
@Data
public class FastDFSFile implements Serializable {
    //文件名字
    private String name;
    //文件内容
    private byte[] content;
    //文件扩展名
    private String ext;
    //文件MD5摘要值
    private String md5;
    //文件创建作者
    private String author;

    public FastDFSFile() {
    }

    public FastDFSFile(String name, byte[] content, String ext) {
        this.name = name;
        this.content = content;
        this.ext = ext;
    }

}
