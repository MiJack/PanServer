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

package com.mijack.panserver.manager.impl;

import com.mijack.panserver.dao.AuthTokenRepository;
import com.mijack.panserver.dao.UserRepository;
import com.mijack.panserver.manager.AuthManager;
import com.mijack.panserver.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;

/**
 * @author Mi&Jack
 */
@Component
public class AuthManagerImpl implements AuthManager {
    @Autowired
    AuthTokenRepository authTokenRepository;
    @Autowired
    UserRepository userRepository;

    @Override
    public User findUserByToken(String token) {
        return userRepository.findUserById(authTokenRepository.findUserByToken(token));
    }

    @Override
    public void saveRestfulToken(long userId, long requestTimeMillis, long expireTimeMillis, String restfulToken) {
        authTokenRepository.saveRestfulToken(userId, new Timestamp(requestTimeMillis), new Timestamp(expireTimeMillis), restfulToken);
    }

}
