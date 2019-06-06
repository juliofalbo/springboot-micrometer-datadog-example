package com.julio.test;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.julio.test.components.SomeController;

@SpringBootApplication(scanBasePackageClasses = SomeController.class)
@EnableScheduling
public class DatadogApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder(DatadogApplication.class).profiles("datadog").run(args);
    }
}
