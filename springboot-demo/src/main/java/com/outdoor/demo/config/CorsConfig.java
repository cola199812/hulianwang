package com.outdoor.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;

/**
 * 全局跨域和静态资源配置类
 * 解决前端跨域访问问题，并配置上传文件的访问路径。
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    /**
     * 配置跨域请求映射
     * 允许来自 localhost:5173/5174 的请求，支持 GET/POST/PUT/DELETE 等方法。
     * @param registry CORS注册表
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:5173", "http://localhost:5174")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowCredentials(true)
                .maxAge(3600)
                .allowedHeaders("*");
    }

    /**
     * 配置静态资源处理器
     * 将 /uploads/** 请求映射到本地文件系统的 uploads 目录。
     * @param registry 资源处理器注册表
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String uploadPath = System.getProperty("user.dir") + "/springboot-demo/uploads/";
        registry.addResourceHandler("/uploads/**").addResourceLocations("file:" + uploadPath);
    }
}
