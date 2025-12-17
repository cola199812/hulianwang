package com.outdoor.demo.mapper;

import com.outdoor.demo.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {
    @Select("SELECT * FROM user WHERE username = #{username}")
    User findByUsername(String username);

    @Select("SELECT * FROM user WHERE username = #{identifier} OR email = #{identifier}")
    User findByUsernameOrPhoneOrEmail(String identifier);

    @Insert("INSERT INTO user(username, password, email, create_time) VALUES(#{username}, #{password}, #{email}, #{createTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);

    @Select("SELECT * FROM user WHERE id = #{id}")
    User findById(Long id);

    @org.apache.ibatis.annotations.Update("UPDATE user SET password = #{password} WHERE email = #{email}")
    int updatePassword(@org.apache.ibatis.annotations.Param("email") String email, @org.apache.ibatis.annotations.Param("password") String password);
}

