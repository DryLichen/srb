package com.dry.srb.core;

import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.Collections;
import java.util.Properties;

public class CodeGenerator {

    /**
     * 获取 Druid DataSource
     */
    private static DataSource dataSource;
    static {
        Properties properties = new Properties();
        try {
            properties.load(CodeGenerator.class.getClassLoader().getResourceAsStream("CodeGenerator.properties"));
            dataSource = DruidDataSourceFactory.createDataSource(properties);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 数据源配置对象
     */
    private static final DataSourceConfig.Builder DATA_SOURCE_CONFIG =
            new DataSourceConfig.Builder(dataSource);

    @Test
    public void genCode(){
        String projectPath = System.getProperty("user.dir");
                // 1、数据源配置
        FastAutoGenerator.create(DATA_SOURCE_CONFIG)
                // 2、全局配置
                .globalConfig(builder -> {
                    builder.author("dry") // 设置作者
                            .enableSwagger() // 开启 swagger 模式
                            .fileOverride() // 覆盖已生成文件
                            .outputDir(projectPath + "/src/main/java") // 指定输出目录
                            .disableOpenDir(); //生成后是否打开资源管理器
                })
                // 3、包配置
                .packageConfig(builder -> {
                    builder.parent("com.dry.srb.core") // 设置父包名
                            .entity("pojo.entity");
                })
                // 4、策略配置
                .strategyConfig(builder -> {
                    builder.entityBuilder()
                            .enableLombok()
                            .logicDeleteColumnName("is_deleted")
                            .logicDeletePropertyName("deleted")
                            .enableRemoveIsPrefix()
                            .idType(IdType.AUTO) //主键策略
                            .controllerBuilder()
                            .enableRestStyle() //生成rest风格的controller，restController注解
                            .serviceBuilder()
                            .formatServiceFileName("%sService") //去掉Service接口的首字母I
                            .formatServiceImplFileName("%sServiceImpl");
                })
                .execute();

    }
}
