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

import com.google.common.collect.Lists;
import com.mijack.panserver.model.Role;
import com.mijack.panserver.model.TokenStatus;
import com.mijack.panserver.service.TokenService;
import com.mijack.panserver.util.CollectionHelper;
import org.hashids.Hashids;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

/**
 * @author Mi&Jack
 */
@Component
public class TokenServiceImpl implements TokenService {
    public static final int INVITATION_TOKEN_ARGUMENTS_LENGTH = 3;
    Hashids hashids = new Hashids();

    @Override
    public String generateRestfulToken(long userId, long requestTimeMillis, long expireTimeMillis, Set<Role> authorities) {
        List<Long> list = Lists.newArrayList();
        list.add(userId);
        list.add(requestTimeMillis);
        list.add(expireTimeMillis);
        for (Role role : authorities) {
            list.add((long) role.id());
        }
        long[] array = new long[list.size()];
        return hashids.encode(array);
    }

    @Override
    public String generateChunkUploadToken(
            long userId, long storageUnitId, String fileName, long fileLength, long chunkCount, String fileDigest,
            long requestTimeMillis, long expireTimeMillis) {
        // todo 添加fileName、fileDigest到token的生成
        return hashids.encode(userId, storageUnitId, fileLength, chunkCount, requestTimeMillis, expireTimeMillis);
    }

    @Override
    public String generateInvitationToken(long invitationUserId, String email, long requestTimeMillis, long expireTimeMillis) {
        return hashids.encode(invitationUserId, requestTimeMillis, expireTimeMillis);
    }

    @Override
    public TokenStatus validateInvitationToken(String invitationToken, String email) {
        // todo 检查email和token的一致性
        long[] decode = hashids.decode(invitationToken);
        if (CollectionHelper.size(decode) != INVITATION_TOKEN_ARGUMENTS_LENGTH) {
            return TokenStatus.BAD_FORMAT_TOKEN;
        }
        long expireTimeMillis = decode[INVITATION_TOKEN_ARGUMENTS_LENGTH - 1];

        if (System.currentTimeMillis() > expireTimeMillis) {
            return TokenStatus.EXPIRATION_TOKEN;

        }
        return TokenStatus.OK;
    }

    @Override
    public long[] decodeToken(String token) {
        return hashids.decode(token);
    }

}
