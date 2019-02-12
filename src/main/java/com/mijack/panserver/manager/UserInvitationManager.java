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

import com.mijack.panserver.model.UserInvitation;

/**
 * @author Mi&Jack
 */
public interface UserInvitationManager {
    /**
     * 根据邮箱查找用户邀请
     *
     * @param email
     * @return
     */
    UserInvitation findInvitationByEmail(String email);

    /**
     * 保存用户邀请
     *
     * @param userInvitation
     */
    void insertInvitation(UserInvitation userInvitation);

    /**
     * 根据token查找对应的UserInvitation
     *
     * @param invitationToken
     * @return
     */
    UserInvitation findInvitationByToken(String invitationToken);

    /**
     * 更新UserInvitation
     *
     * @param userInvitation
     */
    void updateUserInvitation(UserInvitation userInvitation);
}
