package com.saqib.school;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO;

@SpringBootApplication
@EnableMethodSecurity
@EnableSpringDataWebSupport(pageSerializationMode = VIA_DTO)
public class SchoolApplication {

  public static void main(String[] args) {
    SpringApplication.run(SchoolApplication.class, args);
  }

  @Bean
  CommandLineRunner runner(PasswordEncoder passwordEncoder) {
    return args -> {
      IO.println("password: " + passwordEncoder.encode("Admin123"));
    };
  }

}
