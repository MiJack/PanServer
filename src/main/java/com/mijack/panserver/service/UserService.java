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

package com.mijack.panserver.service;

import com.mijack.panserver.model.Role;
import com.mijack.panserver.model.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

/**
 * @author Mi&Jack
 */
public interface UserService extends UserDetailsService {
    /**
     * 判断是否存在Root用户
     *
     * @return
     */
    boolean hasRootUser();

    /**
     * 删除所有的用户
     */
    void deleteAllUser();

    /**
     * 创建相应权限的用户，默认关闭状态
     *
     * @param userName 用户名
     * @param password 明文密码
     * @param email    用户邮箱
     * @param roles    对应的权限
     * @return
     */
    User createUserWithRoles(String userName, String password, String email, List<Role> roles);

    /**
     * 根据用户id查找User
     *
     *
     * @param userId User Id
     * @return
     */
    User findUserById(long userId);

    /**
     * 根据用户名查找User
     *
     * @param userName
     * @return
     */
    User findUserByName(String userName);

    /**
     * 根据用户邮箱查找User
     *
     * @param email
     * @return
     */
    User findUserByMail(String email);

    /**
     * 启用用户
     *
     * @param user
     */
    void enableUser(User user);

    /**
     * 创建相应权限的用户，默认开启状态
     *
     * @param userName
     * @param password
     * @param email
     * @param roles
     * @param invitationToken 邀请
     */
    User createUserWithRoles(String userName, String password, String email, List<Role> roles, String invitationToken);

    /**
     * 删除用户
     *
     * @param user
     */
    void deleteUser(User user);

    /**
     * 启用用户
     *
     * @param currentUser
     * @param toEnableUser
     */
    void enableUser(User currentUser, User toEnableUser);
}
