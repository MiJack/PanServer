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

import com.mijack.panserver.manager.AuthManager;
import com.mijack.panserver.model.Role;
import com.mijack.panserver.model.User;
import com.mijack.panserver.service.AuthService;
import com.mijack.panserver.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author Mi&Jack
 */
@Component
public class AuthServiceImpl implements AuthService {
    /**
     * 默认restful的token有效期为7天
     */
    public static final long TOKEN_TIME_PERIOD = TimeUnit.DAYS.toMillis(7);

    @Autowired
    AuthManager authManager;

    @Autowired
    TokenService tokenService;

    @Override
    public User findUserByToken(String token) {
        if (token == null) {
            return null;
        }
        return authManager.findUserByToken(token);
    }

    @Override
    public String createRestfulToken(long userId, long requestTimeMillis, Set<Role> authorities) {
        long expireTimeMillis = requestTimeMillis + TOKEN_TIME_PERIOD;
        String restfulToken = tokenService.generateRestfulToken(userId, requestTimeMillis, expireTimeMillis, authorities);
        authManager.saveRestfulToken(userId, requestTimeMillis, expireTimeMillis, restfulToken);
        return restfulToken;
    }

}
