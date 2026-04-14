package com.m2m.management.configuration;


import com.alibaba.fastjson.JSONObject;
import com.m2m.management.constant.SystemEnvConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

/**
 * @date ：Created in 6/24/19 10:12 AM
 * @description：config postgres
 */
@Configuration
@Slf4j
public class HibernateConfiguration {


    @Value("${spring.datasource.url}")
    String JdbcPostgresURI;

    @Value("${spring.datasource.password}")
    String PostgreSQLPasssword;

    @Value("${spring.datasource.username}")
    String PostgreSQLUser ;

    @Autowired
    Environment env;

    @Bean
    public DataSource dataSource(){
        System.out.println("******初始化数据库****");
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        String PostgreSQLDatabase=null;
        try{
            String ServiceInstName = "postgresql";
            if(System.getenv(SystemEnvConstant.SERVICES_key) != null){
                JSONObject vcapServices = new JSONObject().parseObject(System.getenv(SystemEnvConstant.SERVICES_key));
                PostgreSQLDatabase = vcapServices.getJSONArray(ServiceInstName).getJSONObject(0).getJSONObject("credentials").getString("database");
                String PostgreSQLHost = vcapServices.getJSONArray(ServiceInstName).getJSONObject(0).getJSONObject("credentials").getString("host");

                int PostgreSQLPort = vcapServices.getJSONArray(ServiceInstName).getJSONObject(0).getJSONObject("credentials").getIntValue("port");
                PostgreSQLUser = vcapServices.getJSONArray(ServiceInstName).getJSONObject(0).getJSONObject("credentials").getString("username");
                PostgreSQLPasssword = vcapServices.getJSONArray(ServiceInstName).getJSONObject(0).getJSONObject("credentials").getString("password");
                JdbcPostgresURI = "jdbc:postgresql://" +  PostgreSQLHost + ":" + PostgreSQLPort + "/" + PostgreSQLDatabase;
            }
            System.out.println("postgresUrl:"+JdbcPostgresURI+"#user:"+PostgreSQLUser);
            dataSource.setUrl(JdbcPostgresURI);
            dataSource.setUsername(PostgreSQLUser);
            dataSource.setPassword(PostgreSQLPasssword);
            dataSource.setSchema("classpath:sql/schema.sql");
            dataSource.setDriverClassName("org.postgresql.Driver");
        }catch(Exception e){
            log.error("HibernateConfiguration-dataSource-"+e.getMessage());
        }
        return dataSource;
    }
}
