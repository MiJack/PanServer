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

import com.mijack.panserver.exception.EmailRegisteredException;
import com.mijack.panserver.manager.UserInvitationManager;
import com.mijack.panserver.model.User;
import com.mijack.panserver.model.UserInvitation;
import com.mijack.panserver.service.InvitationService;
import com.mijack.panserver.service.MailService;
import com.mijack.panserver.service.TokenService;
import com.mijack.panserver.service.UserService;
import com.mijack.panserver.util.Assert;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;

/**
 * @author Mi&Jack
 */
@Component
public class InvitationServiceImpl implements InvitationService {
    @Value("${application.website.host}")
    String inviteHost;
    @Autowired
    UserService userService;
    @Autowired
    TokenService tokenService;
    @Autowired
    MailService mailService;
    @Autowired
    UserInvitationManager userInvitationManager;
    protected final Log logger = LogFactory.getLog(this.getClass());

    @Override
    public UserInvitation inviteUser(User user, String email) {
        Assert.isNull(userService.findUserByMail(email))
                .orThrow(EmailRegisteredException.class, email);

        UserInvitation userInvitation = userInvitationManager.findInvitationByEmail(email);
        if (userInvitation != null) {

            if (tokenService.validateInvitationToken(userInvitation.getToken(), email).isValidate()) {
                logger.info(String.format("邮件%s已在邀请过程中，且邀请未过期", email));
                return userInvitation;
            } else {
                logger.info(String.format("邮件%s已在邀请过程中，但邀请已过期", email));

                Calendar calendar = Calendar.getInstance();
                Date inviteTime = calendar.getTime();
                calendar.add(Calendar.DATE, 1);
                Date expireTime = calendar.getTime();
                userInvitation.setInviteTime(inviteTime);
                userInvitation.setExpireTime(expireTime);
                userInvitationManager.updateUserInvitation(userInvitation);
                return userInvitation;

            }


        }

        Calendar calendar = Calendar.getInstance();
        Date inviteTime = calendar.getTime();
        calendar.add(Calendar.DATE, 1);
        Date expireTime = calendar.getTime();

        String token = tokenService.generateInvitationToken(user.getId(), email, inviteTime.getTime(), expireTime.getTime());

        userInvitation = new UserInvitation();
        userInvitation.setEmail(email);
        userInvitation.setOperator(user);
        userInvitation.setInviteTime(inviteTime);
        userInvitation.setExpireTime(expireTime);
        userInvitation.setToken(token);
        userInvitation.setStatus(UserInvitation.Status.ENABLE);

        String url = generateInvitationUrl(token);

        mailService.sendHtmlMail(
                email, "邀请注册邮件",
                String.format("用户%1$s(%2$s)邀请您注册为用户，邀请链接为<a href=\"%3$s\">%3$s</a>.",
                        user.getUsername(), user.getEmail(), url)
        );
        userInvitationManager.insertInvitation(userInvitation);
        return userInvitation;
    }

    private String generateInvitationUrl(String token) {
        return inviteHost + "/invitation/" + token;
    }


    @Override
    public UserInvitation findInvitationByToken(String invitationToken) {
        UserInvitation invitation = userInvitationManager.findInvitationByToken(invitationToken);
        // todo 通过消息队列实现
        if (!UserInvitation.Status.DISABLE.equals(invitation.getStatus())
                && invitation.getExpireTime().getTime() < System.currentTimeMillis()) {
            invitation.setStatus(UserInvitation.Status.EXPIRED);
            userInvitationManager.updateUserInvitation(invitation);
        }
        return invitation;
    }
}


