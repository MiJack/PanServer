/*
 * Copyright 2019 Mi&Jack
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mijack.panserver.dao;

import com.mijack.panserver.model.User;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Many;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.type.JdbcType;
import org.springframework.stereotype.Repository;

import java.util.Set;

/**
 * @author Mi&Jack
 */
@Repository
public interface UserRepository {

//    id            BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
//    username      VARCHAR(255) NOT NULL DEFAULT '' COMMENT '用户名',
//    user_password VARCHAR(255) NOT NULL DEFAULT '' COMMENT '加密后的密码',
//    avatar_url    VARCHAR(255) NOT NULL DEFAULT '' COMMENT '头像对应的uri',
//    email         VARCHAR(255) NOT NULL DEFAULT '' COMMENT '邮箱',
//    user_status   TINYINT(1)            DEFAULT 0 NOT NULL COMMENT '是否启用，0为禁用',

    /**
     * 根据用户名查询用户信息
     *
     * @param username 用户名
     * @return
     */
    @Select("select id,username,user_password,avatar_url,email,user_status from user_info where username=#{username}")
    @Results({
            @Result(property = "id", column = "id", jdbcType = JdbcType.BIGINT, javaType = long.class),
            @Result(property = "password", column = "user_password"),
            @Result(property = "avatarUrl", column = "avatar_url"),
//            @Result(property = "enabled",column = "user_status"),
            @Result(property = "authorities", column = "id", javaType = Set.class,
                    many = @Many(select = "com.mijack.panserver.dao.UserRoleRepository.listUserRoles"))
    })
    User findUserByUsername(@Param("username") String username);

    /**
     * 根据邮箱查询用户信息
     *
     * @param email 邮箱
     * @return
     */
    @Select("select id,username,user_password,avatar_url,email,user_status from user_info where email=#{email}")
    @Results({
            @Result(property = "password", column = "user_password"),
            @Result(property = "avatarUrl", column = "avatar_url"),
//            @Result(property = "enabled",column = "user_status"),
            @Result(property = "authorities", column = "id", javaType = Set.class,
                    many = @Many(select = "com.mijack.panserver.dao.UserRoleRepository.listUserRoles"))
    })
    User findUserByEmail(@Param("email") String email);

    /**
     * 根据用户id查询用户信息
     *
     * @param userId 用户id
     * @return
     */
    @Select("select id,username,user_password,avatar_url,email,user_status from user_info where id=#{user_id}")
    @Results({
            @Result(property = "password", column = "user_password"),
            @Result(property = "avatarUrl", column = "avatar_url"),
//            @Result(property = "enabled",column = "user_status"),
            @Result(property = "authorities", column = "id", javaType = Set.class,
                    many = @Many(select = "com.mijack.panserver.dao.UserRoleRepository.listUserRoles"))
    })
    User findUserById(@Param("user_id") long userId);

    /**
     * 删除所有的用户
     */
    @Delete("delete from user_info")
    void deleteAllUser();

    /**
     * 创建新的用户
     *
     * @param user
     */
    @Insert("INSERT INTO user_info(username, user_password, avatar_url, email,user_status)" +
            " VALUES (#{user.username},#{user.password},#{user.avatarUrl},#{user.email},0)")
    @Options(useGeneratedKeys = true, keyColumn = "id", keyProperty = "id")
    void insertUser(@Param("user") User user);

    /**
     * 启用用户
     *
     * @param user
     */

    @Update(" UPDATE user_info " +
            " set  user_status  = 1 " +
            " where id = #{user.id} ")
    void enableUser(@Param("user") User user);

    /**
     * 删除用户
     *
     * @param user
     */
    @Delete("delete from user_info where id = #{user.id}")
    void deleteUser(@Param("user") User user);
}
