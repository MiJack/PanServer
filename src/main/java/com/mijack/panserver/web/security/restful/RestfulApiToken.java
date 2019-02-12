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

package com.mijack.panserver.web.security.restful;

import com.google.common.collect.Sets;
import com.mijack.panserver.model.User;
import com.mijack.panserver.util.CollectionHelper;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

/**
 * @author Mi&Jack
 */

public class RestfulApiToken extends AbstractAuthenticationToken {
    private String token;
    private User user;

    public RestfulApiToken() {
        super(Collections.emptyList());
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return user;
    }

    public String getToken() {
        return token;
    }

    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        if (user == null) {
            return Sets.newHashSet();
        }
        if (CollectionHelper.size(user.getAuthorities()) == 0) {
            return Sets.newHashSet();
        }
        return new HashSet<>(user.getAuthorities());
    }

    public void setToken(String token) {
        this.token = token;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
