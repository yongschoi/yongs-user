package yongs.temp.db.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

public interface RoleMapper {
	public List<String> getRoles(String email) throws Exception;
	public void insertRole(@Param("email") String email, @Param("role") String role) throws Exception;
	public void deleteRole(String email) throws Exception;
}