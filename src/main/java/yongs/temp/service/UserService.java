package yongs.temp.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    
    public List<User> getUsers() throws Exception {
    	logger.debug("yongs-user|UserService|getUsers()");
    	List<User> users = mapper.getUsers();	
    	// 각 user에 대한 권한을 넣어준다.
    	for(User user: users) {
    		List<String> roles = roleMapper.getRoles(user.getEmail());
			user.setRoles(roles);
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
    	List<String> roles = null;
    	if(user != null) {
    		roles = roleMapper.getRoles(email);
        	user.setRoles(roles);
    	}
        return user;
    }
    
    public void insertUser(User user) throws Exception {
    	logger.debug("yongs-user|UserService|insertUser()");
    	// password 암호화
    	user.setPassword(CryptoUtil.sha256(user.getPassword()));
    	roleMapper.insertRole(user.getEmail(), "USER");
        mapper.insertUser(user);
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
    }
}