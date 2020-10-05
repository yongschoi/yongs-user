package yongs.temp.controller;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;
import yongs.temp.service.UserService;
import yongs.temp.vo.User;

@RestController
@RequestMapping("/user")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);	

    @Autowired
    UserService service;
    
    @GetMapping("/all")
    @ApiOperation(value="사용자목록", notes="전체 사용자 리스트를 리턴") // Swagger annotation
    public List<User> getUsers() throws Exception{
    	logger.debug("yongs-user|UserController|getUsers()");    	
        return service.getUsers();
    }   
    @PostMapping("/role/update")
    public void roleUpdate(@RequestBody User user) throws Exception{
    	logger.debug("yongs-user|UserController|roleUpdate()");
        service.updateRoles(user);    
    }
    // Admin.vue에서 비번 체크
    @PostMapping("/certificate")
    public ResponseEntity<String> certificate(@RequestBody User user, HttpServletResponse res) throws Exception{
    	logger.debug("yongs-user|LoginController|certificate()");
    	User loginUser = service.getUser(user.getEmail());
    	HttpStatus status = null;
    	if( loginUser == null || !service.comparePassword(user)) {
    		status = HttpStatus.NOT_ACCEPTABLE;
    		return new ResponseEntity<String>(status);
    	} else {
    		status = HttpStatus.OK;
    		return new ResponseEntity<String>(status);
    	}	
    }  
    @PostMapping("/delete")
    public void deleteUser(@RequestBody User user) throws Exception{
    	logger.debug("yongs-user|UserController|deleteUser()");
        service.deleteUser(user.getEmail());
    }
}