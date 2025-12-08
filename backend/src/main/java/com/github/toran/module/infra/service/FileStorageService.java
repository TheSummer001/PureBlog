package com.github.toran.module.infra.service;

import java.io.InputStream;

public interface FileStorageService {

    public String upload(String path, String fileName, InputStream inputStream);

    public String upload(String path, String fileName, byte[] bytes);

    public boolean delete(String fileUrl);

    public String getFileUrl(String path, String fileName);

    public String getStorageType();

}
