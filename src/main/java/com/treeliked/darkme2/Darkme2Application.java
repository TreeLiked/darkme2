package com.treeliked.darkme2;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 应用入口
 *
 * @author lqs2
 */
@SpringBootApplication
@EnableScheduling
public class Darkme2Application {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(Darkme2Application.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.run(args);
    }
}
