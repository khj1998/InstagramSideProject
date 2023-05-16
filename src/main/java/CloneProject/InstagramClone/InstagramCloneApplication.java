package CloneProject.InstagramClone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
@EnableJpaRepositories
public class InstagramCloneApplication {
	public static void main(String[] args) {
		SpringApplication.run(InstagramCloneApplication.class, args);
	}
}
