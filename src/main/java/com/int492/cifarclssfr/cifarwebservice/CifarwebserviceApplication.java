package com.int492.cifarclssfr.cifarwebservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class CifarwebserviceApplication extends SpringBootServletInitializer {
 
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(CifarwebserviceApplication.class);
    }
 
    public static void main(String[] args) throws Exception {
        SpringApplication.run(CifarwebserviceApplication.class, args);
    }
 
}

//@SpringBootApplication
//public class CifarwebserviceApplication {
//
//	public static void main(String[] args) {
//		SpringApplication.run(CifarwebserviceApplication.class, args);
//	}
//}
