package cn.xiaozou.mapper;

import java.util.List;

import cn.xiaozou.entity.User;

public interface UserMapper {
	/**
	 * 根据用户id查找用户
	 * @param id	用户id
	 * @return		返回User对象
	 */
	public User selectUserById(Integer id);
	/**
	 * 增加用户
	 * @param user	传入User对象
	 */
	public void insertUser(User user);
	/**
	 * 根据id删除用户
	 * @param id	传入用户id
	 */
	public void deleteUser(Integer id);
	/**
	 * 更新用户
	 * @param user	传入User对象
	 */
	public void updateUser(User user);
	/**
	 * 根据用户名进行模糊查询
	 * @param username	传入用户名
	 * @return			返回User对象
	 */
	public List<User> selectUserByName(String username);

}
