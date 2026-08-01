package com.API.BlogV2;

import com.API.BlogV2.Utils.ImageKitConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableConfigurationProperties(ImageKitConfig.class)
@EnableCaching
@EnableAsync
public class BlogV2Application {

	public static void main(String[] args) {
		SpringApplication.run(BlogV2Application.class, args);
	}

}
