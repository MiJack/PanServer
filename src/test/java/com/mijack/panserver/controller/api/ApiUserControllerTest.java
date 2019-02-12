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

import com.mijack.panserver.BaseTest;
import com.mijack.panserver.model.User;
import com.mijack.panserver.model.UserInvitation;
import com.mijack.panserver.service.UserService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.anonymous;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.ResultMatcher.matchAll;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Mi&Jack
 */

@RunWith(SpringRunner.class)
@SpringBootTest()
@AutoConfigureMockMvc
public class ApiUserControllerTest extends BaseTest {
    public static final Logger logger = LoggerFactory.getLogger(ApiUserControllerTest.class);
    public static final String USER_TEST_1 = "test-1";
    public static final String USER_EMAIL_1 = "893380824@qq.com";
    public static final String USER_TEST_2 = "test-2";
    public static final String USER_TEST_3 = "test-3";
    private static final String USER_TEST_2_EMAIL = "mijack.yuan@qq.com";
    private static final String USER_TEST_2_PASSWORD = "password";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserService userService;


    @Before
    public void cleanDatabase() throws Exception {
        mockMvc.perform(delete("/api/user").param("user-names", USER_TEST_1, USER_TEST_2, USER_TEST_3))
                .andExpect(matchAll(status().is3xxRedirection()));

        Assert.assertNull(userService.findUserByName(USER_TEST_1));
        Assert.assertNull(userService.findUserByName(USER_TEST_2));
        Assert.assertNull(userService.findUserByName(USER_TEST_3));
    }

    @Test
    public void testAdminAddUser() throws Exception {
        mockMvc.perform(post("/api/user")
                .with(user(userService.loadUserByUsername("root")))
                .param("user-name", USER_TEST_1)
                .param("user-email", USER_EMAIL_1)

        ).andExpect(matchAll(status().is3xxRedirection()));
        User user = userService.findUserByName(USER_TEST_1);
        Assert.assertNotNull(user);
        Assert.assertTrue(user.isEnabled());
    }

    @Test
    public void testAdminInviteUser() throws Exception {
        User user = userService.findUserByMail(USER_TEST_2_EMAIL);
        Assert.assertNull(user);


        MvcResult mvcResult = mockMvc.perform(post("/api/invitation")
                .with(user(userService.loadUserByUsername("root")))
                .param("user-email", USER_TEST_2_EMAIL))
                .andExpect(matchAll(status().isOk()))
                .andReturn();
        UserInvitation userInvitation = resultTo(mvcResult, UserInvitation.class);


        mockMvc.perform(post("/api/user/invitation")
                .with(anonymous())
                .param("user-email", USER_TEST_2_EMAIL)
                .param("invitation-token", userInvitation.getToken())
                .param("user-name", USER_TEST_2)
                .param("password", USER_TEST_2_PASSWORD))
                .andExpect(matchAll(status().isOk()))
        ;

        mockMvc.perform(post("/login")
                .with(anonymous())
                .param("user-name", USER_TEST_2)
                .param("password", USER_TEST_2_PASSWORD))
                .andExpect(matchAll(status().is3xxRedirection()))
        ;


    }

}