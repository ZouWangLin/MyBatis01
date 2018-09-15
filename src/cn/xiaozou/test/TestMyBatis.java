package cn.xiaozou.test;

import java.io.InputStream;
import java.util.Date;
import java.util.List;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Before;
import org.junit.Test;

import cn.xiaozou.entity.User;
import cn.xiaozou.mapper.UserMapper;

/**
 * 测试使用MyBatis实现增删改查操作
 * 
 * @author 26282
 *
 */
public class TestMyBatis {
	SqlSessionFactory sqlSessionFactory = null;
	// 每个测试运行之前都会运行这个方法
	@Before
	public void before() {
		try {
			// 1.加载配置文件，默认加载的是classpath下的文件
			InputStream inputStream = 
					Resources.getResourceAsStream("sqlMapperConfig.xml");
			// 2.利用sqlSessionBuilder构建sessionFactory对象
			SqlSessionFactoryBuilder sqlSessionFactoryBuilder = 
					new SqlSessionFactoryBuilder();
			sqlSessionFactory = 
					sqlSessionFactoryBuilder.build(inputStream);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("创建sqlSessionFacotry异常！");
		}
	}

	/**
	 * 测试根据id查找
	 */
	@Test
	public void testSelectById() {
		// 1.创建sqlSession
		SqlSession sqlSession = sqlSessionFactory.openSession();
		// 2.利用sqlSession获取对应的Mapper对象
		UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
		// 3.利用获取到的Mapper对象，执行一系列操作
		User user = userMapper.selectUserById(1);
		// 4.验证结果
		System.out.println(user);
	}
	
	/**
	 * 测试增加用户
	 */
	@Test
	public void testInsertUser(){
		//1.创建sqlSession
		SqlSession sqlSession = sqlSessionFactory.openSession();
		//2.获取Mapper对象
		UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
		//3.模拟数据
		User user=new User();
		user.setUsername("小明");
		user.setSex("男");
		user.setBirthday(new Date());
		user.setAddress("江西");
		//4.执行一系列操作
		userMapper.insertUser(user);
		//5.查看结果，我们可以发现这个user中有id属性
		System.out.println(user);
		//6.提交事务
		sqlSession.commit();
	}
	
	/**
	 * 测试更新用户
	 */
	@Test
	public void testUpdateUser(){
		//1.获取session
		SqlSession sqlSession = sqlSessionFactory.openSession();
		//2.获取对应的mapper
		UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
		User user=new User();
		user.setId(27);
		user.setUsername("xiaoming");
		user.setSex("女");
		//3.执行方法
		userMapper.updateUser(user);
		//4.提交事务
		sqlSession.commit();
	}
	
	/**
	 * 测试删除
	 */
	@Test
	public void testDeleteUser(){
		//1.获取sqlSession
		SqlSession sqlSession = sqlSessionFactory.openSession();
		//2.获取mapper
		UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
		//3.执行mapper对应的方法
		userMapper.deleteUser(27);
		//4.提交事务
		sqlSession.commit();
	}
	
	/**
	 * 测试模糊查询
	 */
	@Test
	public void testLikeUser(){
		//1.获取sqlSession
		SqlSession sqlSession= sqlSessionFactory.openSession();
		//2.获取mapper
		UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
		//3.执行mapper对应的方法
		List<User> users = userMapper.selectUserByName("五");
		//4.查看结果
		for (User user : users) {
			System.out.println(user);
		}
		
	}

}
