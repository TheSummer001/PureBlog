package com.github.toran.module.infra.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.github.toran.common.exception.BizException;
import com.github.toran.module.infra.service.FileStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.InputStream;

/**
 * 本地文件存储实现
 *
 * @author toran
 */
@Slf4j
@Service("localFileStorageService")
public class LocalFileStorageServiceImpl implements FileStorageService {

    // TODO: 从配置中读取本地存储路径
    private static final String LOCAL_STORAGE_PATH = "D:/upload/";
    private static final String FILE_URL_PREFIX = "http://localhost:8080/files/";

    @Override
    public String upload(String path, String fileName, InputStream inputStream) {
        try {
            String fullPath = LOCAL_STORAGE_PATH + path;
            File dir = new File(fullPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            File file = new File(fullPath + fileName);
            FileUtil.writeFromStream(inputStream, file);

            log.info("本地文件上传成功：{}", file.getAbsolutePath());
            return FILE_URL_PREFIX + path + fileName;
        } catch (Exception e) {
            log.error("本地文件上传失败：", e);
            throw new BizException("文件上传失败：" + e.getMessage());
        }
    }

    @Override
    public String upload(String path, String fileName, byte[] bytes) {
        try {
            String fullPath = LOCAL_STORAGE_PATH + path;
            File dir = new File(fullPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            File file = new File(fullPath + fileName);
            FileUtil.writeBytes(bytes, file);

            log.info("本地文件上传成功：{}", file.getAbsolutePath());
            return FILE_URL_PREFIX + path + fileName;
        } catch (Exception e) {
            log.error("本地文件上传失败：", e);
            throw new BizException("文件上传失败：" + e.getMessage());
        }
    }

    @Override
    public boolean delete(String fileUrl) {
        try {
            if (StrUtil.isBlank(fileUrl) || !fileUrl.startsWith(FILE_URL_PREFIX)) {
                return false;
            }

            String relativePath = fileUrl.substring(FILE_URL_PREFIX.length());
            String fullPath = LOCAL_STORAGE_PATH + relativePath;
            File file = new File(fullPath);

            if (file.exists() && file.isFile()) {
                boolean deleted = file.delete();
                log.info("本地文件删除{}：{}", deleted ? "成功" : "失败", fullPath);
                return deleted;
            }

            return false;
        } catch (Exception e) {
            log.error("本地文件删除失败：", e);
            return false;
        }
    }

    @Override
    public String getFileUrl(String path, String fileName) {
        return FILE_URL_PREFIX + path + fileName;
    }

    @Override
    public String getStorageType() {
        return "local";
    }
}
