package com.platzi.profesoresplatzi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

@SpringBootApplication (exclude = {DataSourceAutoConfiguration.class, JpaRepositoriesAutoConfiguration.class,
		HibernateJpaAutoConfiguration.class})
@EnableAutoConfiguration(exclude = {HibernateJpaAutoConfiguration.class})
public class ProfesoresPlatziApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProfesoresPlatziApplication.class, args);
	}

}
