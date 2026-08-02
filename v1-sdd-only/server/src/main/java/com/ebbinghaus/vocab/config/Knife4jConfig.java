package com.ebbinghaus.vocab.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Knife4jConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("艾宾浩斯单词背诵系统 API")
                        .description("基于艾宾浩斯遗忘曲线的自适应词汇学习系统。"
                                + "包含用户认证、词库管理、学习流程、复习算法、统计等功能。")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Dev Team")));
    }
}
