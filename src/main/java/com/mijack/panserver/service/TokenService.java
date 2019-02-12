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
import com.mijack.panserver.model.TokenStatus;

import java.util.Set;

/**
 * @author Mi&Jack
 */
public interface TokenService {

    /**
     * 创建restful token
     * date
     *
     * @param userId            用户Id
     * @param requestTimeMillis 请求的时间点点
     * @param expireTimeMillis  token过期时间点
     * @param authorities       对应的权限
     * @return 生成的权限
     */
    String generateRestfulToken(long userId, long requestTimeMillis, long expireTimeMillis, Set<Role> authorities);

    /**
     * 将token进行解密
     *
     * @param token
     * @return
     */
    long[] decodeToken(String token);

    /**
     * 为分块上传的文件创建token
     *
     * @param userId
     * @param storageUnitId
     * @param fileName
     * @param fileLength
     * @param chunkCount
     * @param fileDigest
     * @param requestTimeMillis
     * @param expireTimeMillis
     * @return
     */
    String generateChunkUploadToken(long userId, long storageUnitId, String fileName, long fileLength, long chunkCount, String fileDigest, long requestTimeMillis, long expireTimeMillis);

    /**
     * 生成邀请注册的token
     *
     * @param invitationUserId
     * @param email
     * @param requestTimeMillis
     * @param expireTimeMillis
     * @return
     */
    String generateInvitationToken(long invitationUserId, String email, long requestTimeMillis, long expireTimeMillis);

    /**
     * 检查invitationToken状态
     *
     * @param invitationToken
     * @param email
     * @return
     */
    TokenStatus validateInvitationToken(String invitationToken, String email);
}
