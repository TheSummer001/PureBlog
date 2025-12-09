package com.github.toran.framework.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Knife4j (Swagger) 配置
 *
 * @author toran
 */
@Configuration
public class Knife4jConfig {

        @Bean
        public OpenAPI customOpenAPI() {
                return new OpenAPI()
                                .info(new Info()
                                                .title("Personal Blog API 文档")
                                                .description("个人博客系统 RESTful API 接口文档")
                                                .version("1.0.0")
                                                .contact(new Contact()
                                                                .name("toran")
                                                                .email("toran@example.com")
                                                                .url("https://github.com/toran"))
                                                .license(new License()
                                                                .name("Apache 2.0")
                                                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")));
        }

        /**
         * 配置 API 分组，确保接口能被正确扫描和显示
         *
         * @return GroupedOpenApi Bean
         */
        @Bean
        public GroupedOpenApi personalBlogApi() {
                return GroupedOpenApi.builder()
                                .group("personal-blog")
                                .packagesToScan("com.github.toran.module")
                                .pathsToMatch("/api/**")
                                .build();
        }
}