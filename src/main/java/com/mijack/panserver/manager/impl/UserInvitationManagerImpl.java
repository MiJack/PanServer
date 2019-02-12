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

import com.mijack.panserver.dao.UserInvitationRepository;
import com.mijack.panserver.manager.UserInvitationManager;
import com.mijack.panserver.model.UserInvitation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Mi&Jack
 */
@Component
public class UserInvitationManagerImpl implements UserInvitationManager {
    @Autowired
    UserInvitationRepository userInvitationRepository;

    @Override
    public UserInvitation findInvitationByEmail(String email) {
        return userInvitationRepository.findInvitationByEmail(email);
    }

    @Override
    public void insertInvitation(UserInvitation userInvitation) {
        userInvitationRepository.insertInvitation(userInvitation);
    }

    @Override
    public UserInvitation findInvitationByToken(String invitationToken) {
        return userInvitationRepository.findInvitationByToken(invitationToken);
    }

    @Override
    public void updateUserInvitation(UserInvitation userInvitation) {
        userInvitationRepository.updateUserInvitation(userInvitation.getId(), userInvitation);
    }
}
