package yongs.temp.db.mapper;

import java.util.List;

import yongs.temp.vo.User;

public interface UserMapper {
	public List<User> getUsers() throws Exception;
	public User getUser(String email) throws Exception;
	public void insertUser(User user) throws Exception;
	public void deleteUser(String email) throws Exception;
}