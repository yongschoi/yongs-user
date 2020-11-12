package yongs.temp.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;

import yongs.temp.db.mapper.RoleMapper;
import yongs.temp.db.mapper.UserMapper;
import yongs.temp.util.CryptoUtil;
import yongs.temp.vo.User;

@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);	
 
    @Autowired
    UserMapper mapper;
    @Autowired
    RoleMapper roleMapper;
    @Autowired
    MinioService minio;
    
    public List<User> getUsers() throws Exception {
    	logger.debug("yongs-user|UserService|getUsers()");
    	List<User> users = mapper.getUsers();	
    	// 각 user에 대한 권한을 넣어준다.
    	for(User user: users) {
    		List<String> roles = roleMapper.getRoles(user.getEmail());
			user.setRoles(roles); 
			// 사용자 Photo 정보 셋팅
	    	user.setPhoto(minio.getObjectUrl("user", user.getEmail()));
    	}   
    	/* 
    	users.forEach(user -> {  
    		try {
    			List<String> roles = roleMapper.getRoles(user.getEmail());
    			user.setRoles(roles);
    		} catch (Exception e) {
    			throw new RuntimeException(e);
    		}
    	});
    	*/
  
        return users;
    }
 
    public User getUser(String email) throws Exception {
    	logger.debug("yongs-user|UserService|getUser({})", email);
    	User user = mapper.getUser(email);
    	// 사용자 Role 정보 셋팅
    	List<String> roles = null;
    	if(user != null) {
    		roles = roleMapper.getRoles(email);
        	user.setRoles(roles);
    	}
    	// 사용자 Photo 정보 셋팅
    	user.setPhoto(minio.getObjectUrl("user", email));
        return user;
    }
    
    public void insertUser(MultipartFile file, String userStr) throws Exception {
    	logger.debug("yongs-user|UserService|insertUser()");
    	User user = null;
    	ObjectMapper objMapper = new ObjectMapper();
		try {
			user = objMapper.readValue(userStr, User.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	// password 암호화
    	user.setPassword(CryptoUtil.sha256(user.getPassword()));
    	roleMapper.insertRole(user.getEmail(), "USER");
    	mapper.insertUser(user);  
        // minio에 photo 이미지 저장
    	minio.putObject("user", user.getEmail(), file);
    }
    
    public boolean comparePassword(User user) throws Exception {
    	logger.debug("yongs-user|UserService|comparePassword()");
    	String encrypted = mapper.getUser(user.getEmail()).getPassword();
    	String raw = user.getPassword();

    	return encrypted.equals(CryptoUtil.sha256(raw));
    }
     
    public void updateRoles(User user) throws Exception {
    	logger.debug("yongs-user|UserService|updateRoles()");    	
    	
    	// 1. 해당 user의 모든 role을 삭제한다.
    	roleMapper.deleteRole(user.getEmail());
    	// 2. 해당 user의 role을 모두 insert한다.
    	for(String role: user.getRoles()) {
    		roleMapper.insertRole(user.getEmail(), role);
    	}
    	/*
    	user.getRoles().forEach(role -> {
    		try {
    			roleMapper.insertRole(user.getEmail(), role);
    		} catch (Exception e) {
    			throw new RuntimeException();
    		}		
    	});
    	*/
    }
    
    public void deleteUser(String email) throws Exception {
    	logger.debug("yongs-user|UserService|deleteUser()");   
    	// 사용자 삭제하고
    	mapper.deleteUser(email);
    	// 해당 사용자의 role 삭제
    	roleMapper.deleteRole(email);
        // minio에 photo 이미지 삭제
    	minio.removeObject("user", email);
    }
}