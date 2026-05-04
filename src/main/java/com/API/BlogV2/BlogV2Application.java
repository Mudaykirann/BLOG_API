package com.API.BlogV2;

import com.API.BlogV2.Utils.ImageKitConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(ImageKitConfig.class)
public class BlogV2Application {

	public static void main(String[] args) {
		SpringApplication.run(BlogV2Application.class, args);
	}

}
