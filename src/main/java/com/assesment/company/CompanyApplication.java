package com.assesment.company;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;


@SpringBootApplication
@ComponentScan(basePackages = "com.assesment.company")
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class CompanyApplication extends SpringBootServletInitializer {
	public static void main(String[] args) {
		SpringApplication.run(CompanyApplication.class, args);
	}
}
