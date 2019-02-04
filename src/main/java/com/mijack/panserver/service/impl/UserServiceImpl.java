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
import com.mijack.panserver.model.Role;
import com.mijack.panserver.model.User;
import com.mijack.panserver.service.UserService;
import com.mijack.panserver.util.CollectionHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Mi&Jack
 */
@Component
public class UserServiceImpl implements UserService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    UserRoleRepository userRoleRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Value("${application.img.default-avatar.baseUrl}${application.img.default-avatar.path}")
    String DEFAULT_AVATAR_URL;

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
    public User createUserWithRoles(String rootName, String email, String password, List<Role> roles) {
        User user = new User(rootName, email, DEFAULT_AVATAR_URL, passwordEncoder.encode(password), Sets.newHashSet(roles));
        userRepository.insertUser(user);
        for (Role role : roles) {
            userRoleRepository.insertUserRole(user, role);
        }
        return user;
    }

    @Override
    public User findUserById(long userId) {
        return userRepository.findUserById(userId);
    }
}
