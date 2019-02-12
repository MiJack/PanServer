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

package com.mijack.panserver.controller.api;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.mijack.panserver.model.Role;
import com.mijack.panserver.model.TokenStatus;
import com.mijack.panserver.model.User;
import com.mijack.panserver.model.UserInvitation;
import com.mijack.panserver.service.InvitationService;
import com.mijack.panserver.service.MailService;
import com.mijack.panserver.service.TokenService;
import com.mijack.panserver.service.UserService;
import com.mijack.panserver.util.CollectionHelper;
import com.mijack.panserver.web.security.AuthenticationUtils;
import com.mijack.panserver.web.security.roles.PermitRoles;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

/**
 * @author Mi&Jack
 */
@Controller
public class ApiUserController {
    @Autowired
    UserService userService;
    @Autowired
    InvitationService invitationService;
    @Autowired
    MailService mailService;
    @Value("application.user-manager.default-password")
    String defaultPassword;
    @Autowired
    TokenService tokenService;

    @PostMapping("/api/user")
    @PermitRoles(roles = {Role.ADMIN, Role.ROOT})
    public ModelAndView addUser(@RequestParam("user-name") String userName, @RequestParam("user-email") String email) {
        ModelAndView mav = new ModelAndView();
        User user = userService.createUserWithRoles(userName, defaultPassword, email, Lists.newArrayList(Role.USER));
        userService.enableUser(AuthenticationUtils.currentUser(), user);

        mailService.sendTextMail(email, "用户创建成功通知", "新用户（id为" + user.getId()
                + "）创建成功，默认密码为" + defaultPassword);
        mav.setViewName("redirect:/user/home");
        return mav;
    }

    @DeleteMapping("/api/user")
    @PermitRoles(roles = {Role.ADMIN, Role.ROOT})
    public ModelAndView deleteUser(@RequestParam("user-names") List<String> userNames) {
        List<User> users = CollectionHelper.transform(userNames, new Function<String, User>() {
            @Nullable
            @Override
            public User apply(@Nullable String input) {
                return userService.findUserByName(input);
            }
        });

        for (User user : users) {
            if (user != null) {
                userService.deleteUser(user);
            }
        }
        ModelAndView mav = new ModelAndView();


        mav.setViewName("redirect:/user/home");
        return mav;
    }

    @PostMapping("/api/invitation")
    @ResponseBody
    public UserInvitation newUserInvitation(@RequestParam("user-email") String email) {
        return invitationService.inviteUser(AuthenticationUtils.currentUser(), email);

    }

    @PostMapping("/api/user/invitation")
    @ResponseBody
    public ResponseEntity activeUserInvitation(
            @RequestParam("user-email") String email,
            @RequestParam("invitation-token") String invitationToken,
            @RequestParam("user-name") String userName,
            @RequestParam("password") String password) {
        TokenStatus tokenStatus = tokenService.validateInvitationToken(invitationToken, email);
        if (!tokenStatus.isValidate()) {
            return new ResponseEntity(tokenStatus, HttpStatus.BAD_REQUEST);
        }
        User user = userService.createUserWithRoles(userName, password, email, Lists.newArrayList(Role.USER), invitationToken);

        return new ResponseEntity(user, HttpStatus.OK);
    }
}
