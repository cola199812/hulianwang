package com.outdoor.demo.mapper;

import com.outdoor.demo.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

@Mapper
/**
 * 用户数据访问接口
 * 负责User表的数据库操作。
 */
public interface UserMapper {
    /**
     * 根据用户名查询用户
     * @param username 用户名
     * @return 用户对象
     */
    @Select("SELECT * FROM user WHERE username = #{username}")
    User findByUsername(String username);

    /**
     * 根据用户名或邮箱查询用户
     * @param identifier 用户名或邮箱
     * @return 用户对象
     */
    @Select("SELECT * FROM user WHERE username = #{identifier} OR email = #{identifier}")
    User findByUsernameOrPhoneOrEmail(String identifier);

    /**
     * 插入新用户
     * @param user 用户对象
     * @return 影响行数
     */
    @Insert("INSERT INTO user(username, password, email, create_time) VALUES(#{username}, #{password}, #{email}, #{createTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);

    /**
     * 根据ID查询用户
     * @param id 用户ID
     * @return 用户对象
     */
    @Select("SELECT * FROM user WHERE id = #{id}")
    User findById(Long id);

    /**
     * 更新用户密码
     * @param email 邮箱
     * @param password 新密码（已加密）
     * @return 影响行数
     */
    @org.apache.ibatis.annotations.Update("UPDATE user SET password = #{password} WHERE email = #{email}")
    int updatePassword(@org.apache.ibatis.annotations.Param("email") String email, @org.apache.ibatis.annotations.Param("password") String password);
}

