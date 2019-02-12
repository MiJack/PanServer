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

package com.mijack.panserver.service.impl;

import com.google.common.collect.Sets;
import com.mijack.panserver.dao.UserRepository;
import com.mijack.panserver.dao.UserRoleRepository;
import com.mijack.panserver.exception.EmailRegisteredException;
import com.mijack.panserver.exception.UserNameRegisteredException;
import com.mijack.panserver.model.Role;
import com.mijack.panserver.model.TokenStatus;
import com.mijack.panserver.model.User;
import com.mijack.panserver.service.TokenService;
import com.mijack.panserver.service.UserService;
import com.mijack.panserver.util.Assert;
import com.mijack.panserver.util.CollectionHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

/**
 * @author Mi&Jack
 */
@Component
public class UserServiceImpl implements UserService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    TokenService tokenService;
    @Autowired
    UserRoleRepository userRoleRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Value("${application.img.default-avatar.baseUrl}${application.img.default-avatar.path}")
    String defaultAvatarUrl;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findUserByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("user [username = " + username + "] not found");
        }
        return user;
    }

    @Override
    public boolean hasRootUser() {
        List<User> users = userRoleRepository.listUserByRole(Role.ROOT);
        return !CollectionHelper.isEmpty(users);
    }

    @Override
    public void deleteAllUser() {
        // todo 添加事务异常处理
        userRepository.deleteAllUser();
        userRoleRepository.deleteAllUserRole();
    }

    @Override
    public User createUserWithRoles(String userName, String email, String password, List<Role> roles) {
        Assert.isNull(userRepository.findUserByEmail(email)).orThrow(EmailRegisteredException.class, email);
        Assert.isNull(userRepository.findUserByUsername(userName)).orThrow(UserNameRegisteredException.class, email);
        // todo 添加事务异常处理
        User user = new User(userName, email, defaultAvatarUrl, passwordEncoder.encode(password), Sets.newHashSet(roles));
        userRepository.insertUser(user);
        for (Role role : roles) {
            userRoleRepository.insertUserRole(user, role);
        }

        return user;
    }

    @Override
    public User createUserWithRoles(String userName, String password, String email, List<Role> roles, String invitationToken) {
        Assert.isNull(userRepository.findUserByUsername(userName)).orThrow(UserNameRegisteredException.class, email);
        Assert.isNull(userRepository.findUserByEmail(email)).orThrow(EmailRegisteredException.class, email);
        Assert.isEquals(tokenService.validateInvitationToken(invitationToken, email), TokenStatus.OK);
        // todo 添加事务异常处理
        User user = new User(userName, email, defaultAvatarUrl, passwordEncoder.encode(password), Sets.newHashSet(roles));
        userRepository.insertUser(user);
        for (Role role : roles) {
            userRoleRepository.insertUserRole(user, role);
        }
        userRepository.enableUser(user);
        return user;
    }

    /**
     * 删除用户
     *
     * @param user
     */
    @Override
    public void deleteUser(User user) {
        // todo 添加事务异常处理
        userRoleRepository.deleteUserRole(user);
        userRepository.deleteUser(user);
    }

    @Override
    public User findUserById(long userId) {
        return userRepository.findUserById(userId);
    }

    @Override
    public User findUserByName(String userName) {
        return userRepository.findUserByUsername(userName);
    }

    @Override
    public User findUserByMail(String email) {
        return userRepository.findUserByEmail(email);
    }

    @Override
    public void enableUser(User user) {
        Set<Role> roles = userRoleRepository.listUserRoles(user.getId());
        if (roles.contains(Role.ROOT)) {
            userRepository.enableUser(user);
        } else {
            // 非Root用户请用token激活
            throw new IllegalStateException("非Root用户请用token激活");
        }
    }

    @Override
    public void enableUser(User currentUser, User toEnableUser) {
        Set<Role> roles = userRoleRepository.listUserRoles(currentUser.getId());
        if (roles.contains(Role.ROOT) || roles.contains(Role.ADMIN)) {
            userRepository.enableUser(toEnableUser);
        } else {
            // 非Root用户请用token激活
            throw new IllegalStateException("非Root、ADMIN用户请用token激活");
        }
    }
}
