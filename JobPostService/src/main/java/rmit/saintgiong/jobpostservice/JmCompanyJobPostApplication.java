package rmit.saintgiong.jobpostservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class JmCompanyJobPostApplication {

	public static void main(String[] args) {
		SpringApplication.run(JmCompanyJobPostApplication.class, args);
	}

}
