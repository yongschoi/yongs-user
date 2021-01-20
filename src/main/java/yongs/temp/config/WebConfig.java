package yongs.temp.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class WebConfig {
	@LoadBalanced // @LoadBalanced가 없으면 eureka/zipkin call 이 안됨 
    @Bean  
    public RestTemplate restTemplate() {	
		/* Connection Pool 방식을 사용하지 않으려면 */
		return new RestTemplate();
		
		/* Connection Pool 방식을 사용	
    	HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(); 
    	factory.setReadTimeout(5000); // 읽기시간초과, ms 
    	factory.setConnectTimeout(3000); // 연결시간초과, ms 
    	HttpClient httpClient = HttpClientBuilder.create() 
    			.setMaxConnTotal(10) // 최대 오픈되는 커넥션 수
    			.setMaxConnPerRoute(5) // IP,포트 1쌍에 대해 수행 할 연결 수를 제한
    			.build(); 
    	
    	factory.setHttpClient(httpClient); // 동기실행에 사용될 HttpClient 세팅

        return new RestTemplate(factory);
        */
    }
}
