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

package com.mijack.panserver.event;

import com.google.common.collect.Lists;
import com.mijack.panserver.model.Role;
import com.mijack.panserver.model.User;
import com.mijack.panserver.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * @author Mi&Jack
 */
@Component
public class ApplicationStartupListener implements ApplicationListener<ApplicationReadyEvent>, ApplicationContextAware {
    public static final Logger logger = LoggerFactory.getLogger(ApplicationStartupListener.class);
    @Autowired
    UserService userService;
    @Value("${application.root.username}")
    String rootName;
    @Value("${application.root.password}")
    String password;
    @Value("${application.root.email}")
    String email;
    private ApplicationContext applicationContext;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        logger.info("[onApplicationEvent]  " + event);
        if (userService.hasRootUser()) {
            logger.info("系统已经初始化完毕");
            applicationContext.publishEvent(new WebInitEvent(WebInitEvent.WebStatus.INITIALIZED));
            return;
        }
        logger.info("系统尚未初始化，开始初始化");
        userService.deleteAllUser();
        User root = userService.createUserWithRoles(rootName, email, password, Lists.newArrayList(Role.ROOT));
        userService.enableUser(root);
        logger.info("系统初始化完成");
        applicationContext.publishEvent(new WebInitEvent(WebInitEvent.WebStatus.INITIALIZED));
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}