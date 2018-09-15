

# Mybatis的学习-day01

## 1.MyBatis介绍

​	MyBatis本是apche的一个开源项目iBatis，2010年这个项目有apache software foundation 迁移到google code，并且改名MyBatis，2013年迁移到Github。

​	MyBatis是一个优秀的持久层框架，它对Jdbc操作的数据库的过程进行封装，使开发者只需要关注sql本身。

## 2.使用jdbc问题总结

- 数据库连接创建、释放频繁造成系统资源浪费，从而影响了系统的性能
- 使用Jdbc操作数据库，在很多时候都存在硬编码问题

##  3.MyBatis架构

![](MyBatis架构.bmp)

# 4.使用MyBatis步骤

- 书写sqlMapperConfig.xml（MyBatis的核心配置文件）文件，默认要放在classpath下

  ```xml
  <?xml version="1.0" encoding="UTF-8" ?>
  <!DOCTYPE configuration
  PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
  "http://mybatis.org/dtd/mybatis-3-config.dtd">
  <!-- MyBatis的核心配置文件 -->
  ```

- 书写xxMapper.xml（MyBatis中sql书写文件）文件，最好和xxMapper一个包中

  ```xml
  <?xml version="1.0" encoding="UTF-8" ?>
  <!DOCTYPE mapper
  PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
  "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
  <!--在此书写sql语句 -->
  
  ```

- 使用Mapper动态代理完成一系列增删改查操作，使用Mapper动态代理需要遵循的4个原则：
  - 1.**Mapper的接口名**必须和Mapper.xml中定义的**每个statement的id**相同
  - 2.**Mapper接口的方法名的输入参数类型**和mapper.xml中定义的**每个sql的parameteType的类型**相同
  - 3.**Mapper接口方法的输出参数类型**和mapper.xml中定义的**每个sql的resultType的类型**相同
  - 4.Mapper.xml中的**namespace**与**mapper接口的类路径**相同

## 5.MyBatis实战

### 5.1 根据id查找用户

- mapper接口的书写

  ```java
  public interface UserMapper {
  	/**
  	 * 根据用户id查找用户
  	 * @param id	用户id
  	 * @return		返回User对象
  	 */
  	public User selectUserById(Integer id);
  }
  ```

- mapper.xml中对应sql的书写

  ```xml
  <!-- 1.这里的namespace一定要和对应mapper接口的类路径相同，这样的目的为了区分不同类的同一个方法 -->
  <mapper namespace="cn.xiaozou.mapper.UserMapper">
  	<!--根据id查找用户-->
  	<select id="selectUserById" parameterType="Integer" resultType="cn.xiaozou.entity.User">
  		select * from user where id=#{id}
  	</select>
  </mapper>
  ```

- 测试类的书写

  ```java
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
  	
  ```

- xml写法总结

  ```xml
  <!-- 
  		1.xml属性详解：
  			1.id即当前语句的Id,一定要和Mapper的方法名一样
  			2.parameterType：输入参数类型,基本数据类型都配置了别名，所以可以简写，反之不可以
  			3.resultType:输出参数类型
  			4.#{}	相当于占位符?	这个占位符在执行的时候时会加""号的，可以防止sql注入
  			  ${}	也是占位符		这个占位符在执行的时候不会加""，不可以放在sql注入
  			  总结：建议多使用上者
  			5.parameterMap:作废，基本上不用
  			6.resultMap:在数据库中字段和实体类属性不一致时使用
  	 -->
  ```

### 5.2 增加用户

- mapper接口的书写

  ```java
  	/**
  	 * 增加用户
  	 * @param user	传入用户
  	 */
  public void insertUser(User user);
  ```

- xml写法

  ```xml
  <!-- 
  		2.xml详解：
  			1.keyProperty:实体属性
  			2.keyColumn:数据库对应字段
  			3.order:获取id的时机，一般来说如果时自增长就是AFTER，如果是uuid就是BEFORE
  			4.为了在没有提交事务之前就获取用户id可以下使用sql的LAST_INSERT_ID方法
  	 -->
  	<!-- 增加用户 -->
  	<insert id="insertUser" parameterType="cn.xiaozou.entity.User">
  		<!-- 为了执行增加用户操作后（还没提交事务时），就获取当前 用户的id -->
  		<selectKey keyProperty="id" order="AFTER" keyColumn="id"
  			resultType="Integer">
  			select LAST_INSERT_ID();
  		</selectKey>
  		insert into user(id,username,sex,birthday,address)
  		values(#{id},#{username},#{sex},#{birthday},#{address})
  	</insert>
  ```

### 5.3 根据用户名进行模糊查询

- xml写法

  ```xml
  
  	<!-- 根据用户名进行模糊查询 -->
  	<select id="selectUserByName" parameterType="String" 	 resultType="cn.xiaozou.entity.User">
  		select * from user where username like "%"#{username}"%"
  	</select>
  ```

### 5.4 其他模块

- 其他的操作基本上和上面一样

## 6.sqlConfigMapper.xml配置详解

- mappers（映射器）标签

  ```xml
  	<!-- 2.加载指定包下的所有mapper.xml文件 -->
  	<mappers>
  		<package name="cn.xiaozou.mapper" />
  	</mappers>
  ```

- properties

  ```xml
  	<!-- 3.引入外部的propeties -->
  	<properties resource="db.properties"/>
  ```

- typeAliases（类型别名）

  作用：只要定义了类型别名，那么我们写parameterType的时候就可以用类型别名，这样可以简化我们的书写，MyBatis自定义了基本类型的别名

  ```xml
  <!-- 4.给一个包定义别名 -->
  	<typeAliases>
  		<package name="cn.xiaozou.entity"/>
  	</typeAliases>
  ```

  注意：此种方法需要mapper接口类和对应的mapper.xml文件在同一个包中，并且名字前缀也要一样
