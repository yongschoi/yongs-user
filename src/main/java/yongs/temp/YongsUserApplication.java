package yongs.temp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class YongsUserApplication {

	public static void main(String[] args) {
		SpringApplication.run(YongsUserApplication.class, args);
	}
}
