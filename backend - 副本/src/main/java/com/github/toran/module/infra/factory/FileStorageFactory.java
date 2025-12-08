package com.github.toran.module.infra.factory;

import com.github.toran.module.infra.service.FileStorageService;
import com.github.toran.module.system.service.ISysConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 文件存储工厂
 * 根据系统配置动态选择存储实现（Local、OSS、MinIO 等）
 *
 * @author toran
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FileStorageFactory {

    private final ISysConfigService sysConfigService;
    private final Map<String, FileStorageService> fileStorageServiceMap;

    /**
     * 获取文件存储服务实例
     * 根据系统配置动态选择实现类
     *
     * @return 文件存储服务
     */
    public FileStorageService getFileStorageService() {
        // 从系统配置读取存储类型
        String storageType = sysConfigService.getConfigValue("oss.storage.type", "local");
        
        log.debug("当前存储类型：{}", storageType);

        // 根据类型获取对应的 Bean
        // Bean 名称约定：{storageType}FileStorageService
        String beanName = storageType + "FileStorageService";
        FileStorageService fileStorageService = fileStorageServiceMap.get(beanName);

        if (fileStorageService == null) {
            log.warn("未找到存储类型 {} 的实现，使用默认本地存储", storageType);
            return fileStorageServiceMap.get("localFileStorageService");
        }

        return fileStorageService;
    }

    /**
     * 根据存储类型获取文件存储服务
     *
     * @param storageType 存储类型
     * @return 文件存储服务
     */
    public FileStorageService getFileStorageService(String storageType) {
        String beanName = storageType + "FileStorageService";
        FileStorageService fileStorageService = fileStorageServiceMap.get(beanName);

        if (fileStorageService == null) {
            log.warn("未找到存储类型 {} 的实现，使用默认本地存储", storageType);
            return fileStorageServiceMap.get("localFileStorageService");
        }

        return fileStorageService;
    }
}
