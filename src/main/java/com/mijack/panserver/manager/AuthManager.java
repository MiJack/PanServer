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

package com.mijack.panserver.manager;

import com.mijack.panserver.model.User;

/**
 * @author Mi&Jack
 */
public interface AuthManager {
    /**
     * 根据restful token 查找对应的用户
     *
     * @param token
     * @return
     */
    User findUserByToken(String token);

    /**
     * 保存restful token
     * @param userId
     * @param requestTimeMillis
     * @param expireTimeMillis
     * @param restfulToken
     */
    void saveRestfulToken(long userId, long requestTimeMillis, long expireTimeMillis, String restfulToken);

}
