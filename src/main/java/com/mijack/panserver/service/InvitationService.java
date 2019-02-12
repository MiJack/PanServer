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

import com.mijack.panserver.model.User;
import com.mijack.panserver.model.UserInvitation;

/**
 * @author Mi&Jack
 */
public interface InvitationService {
    /**
     * 邀请用户
     *
     * @param user  邀请者
     * @param email 被邀请的邮箱
     * @return
     */
    UserInvitation inviteUser(User user, String email);

    /**
     * 根据邀请token找到对应的邀请信息
     *
     * @param invitationToken
     * @return
     */
    UserInvitation findInvitationByToken(String invitationToken);
}


