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

import com.mijack.panserver.model.Role;
import com.mijack.panserver.model.User;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

/**
 * @author Mi&Jack
 */
@Repository
public interface UserRoleRepository {

    /**
     * 根据用户权限枚举所有的用户列表
     * <p>
     * todo 接口优化：listUserByRoles(@Param("roles") List<Role> roles);
     *
     * @param role
     * @return
     */
    @Select("select user_id, role_name from user_role where role_name = #{role} and role_status = 1")
    List<User> listUserByRole(@Param("role") Role role);


    /**
     * 根据用户ID枚举所有的用户角色
     *
     * @param userId
     * @return
     */
    @Select("select role_name from user_role where user_id=#{userId} and role_status = 1")
    Set<Role> listUserRoles(@Param("userId") long userId);

    /**
     * 删除所有的用户权限
     */
    @Delete("delete from user_role")
    void deleteAllUserRole();

    /**
     * 给用户添加用户权限
     * <p>
     * todo 接口优化：void insertUserRoles(User user, List<Role> roles);
     *
     * @param user
     * @param role
     */
    @Insert("INSERT INTO user_role(user_id, role_name, role_status)" +
            " VALUES (#{user.id},#{role.name},1)")
    void insertUserRole(@Param("user") User user, @Param("role") Role role);
}
