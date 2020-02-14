package yongs.temp.controller;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import yongs.temp.service.UserService;
import yongs.temp.vo.User;

@RestController
@RequestMapping("/any")
public class AnyController {
    private static final Logger logger = LoggerFactory.getLogger(AnyController.class);	

    @Autowired
    UserService service;
    
    @Autowired
    private RestTemplate restTemplate;
    // eureka서비스에 등록을 하고 
    // URL을 http://localhost:XXXX/~ 혹은 http://127.0.0.1:XXXX/~ 로 하면  
    // no instances available ERROR 발생함
    private static String JWT_URI = "http://yongs-jwt/jwt/create";
 
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody User user, HttpServletResponse res) throws Exception{
    	logger.debug("yongs-user|AnyController|login()");
    	User loginUser = service.getUser(user.getEmail());
    	HttpStatus status = null;
    	// 사용자가 없거나 비밀번호가 틀리면 NOT_ACCEPTABLE
    	if( loginUser == null || !service.comparePassword(user)) {
    		status = HttpStatus.NOT_ACCEPTABLE;
    		return new ResponseEntity<String>(status);
    	} else {
    		status = HttpStatus.OK;
    		HttpEntity<User> entity = new HttpEntity<User>(loginUser);
 
    		// yongs-jwt 인증 서버 API 호출하여 access-token 리턴
    		ResponseEntity<String> response = restTemplate.exchange(
    				JWT_URI, 
    				HttpMethod.POST, 
    				entity, 
    				String.class);    		   		
    		return new ResponseEntity<String>(response.getBody(), status);
    	}	
    }
    
    // RegistrationForm.vue에서 email 중복 체크
    @GetMapping("/{email}")
    public ResponseEntity<String> getUser(@PathVariable("email") String email) throws Exception{
    	logger.debug("yongs-user|AnyController|getUser({})", email);
    	User user = service.getUser(email);
        HttpStatus status = user != null ? HttpStatus.OK : HttpStatus.NOT_FOUND;
        return new ResponseEntity<String>(user !=null ? user.getEmail() : "", status);
    }  
    
    @PostMapping("/create") /* Postman 프로그램으로 실행 */
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@RequestBody User user) throws Exception{
    	logger.debug("yongs-user|AnyController|create()");
        service.insertUser(user);
    }
}