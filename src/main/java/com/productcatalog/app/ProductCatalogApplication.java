package com.productcatalog.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = { "com.productcatalog.app" })
@EnableJpaRepositories
public class ProductCatalogApplication {

	public static void main(String[] args) {

		SpringApplication.run(ProductCatalogApplication.class, args);
	}

}
