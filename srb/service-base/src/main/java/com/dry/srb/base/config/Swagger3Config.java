package com.dry.srb.base.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.function.Predicate;

@Configuration
@EnableOpenApi
public class Swagger3Config {

    /**
     * 接口文档的格式配置对象
     * 第一组
     */
    @Bean
    public Docket adminApiConfig(){
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("admin") //分组
                .apiInfo(adminApiInfo())
                .select()
                // 只显示admin路径下的页面
                .paths(PathSelectors.ant("/admin/**/"))
                .build();
    }

    private ApiInfo adminApiInfo(){
        return new ApiInfoBuilder()
                .title("尚融宝后台管理系统-API文档")
                .description("本文档描述了尚融宝后台管理系统接口")
                .version("1.0")
                .contact(new Contact("Dry", "www.dry.com", "347189447@gmail.com"))
                .build();
    }

    /**
     * 第二组，网页客户端api
     */
    @Bean
    public Docket apiConfig(){
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("api")
                .apiInfo(apiInfo())
                .select()
                .paths(PathSelectors.ant("/api/**/"))
                .build();
    }

    private ApiInfo apiInfo(){
        return new ApiInfoBuilder()
                .title("尚融宝前台客户系统-API文档")
                .description("本文档描述了尚融宝前台客户系统接口")
                .version("1.0")
                .contact(new Contact("Dry", "www.dry.com", "347189447@gmail.com"))
                .build();
    }
}
