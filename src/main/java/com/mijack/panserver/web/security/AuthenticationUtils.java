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

package com.mijack.panserver.web.security;

import com.google.common.collect.Sets;
import com.mijack.panserver.model.Role;
import com.mijack.panserver.model.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Mi&Jack
 */
public class AuthenticationUtils {

    public static Set<Role> currentRoles() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return new HashSet(authentication.getAuthorities());
        }
        return Sets.newHashSet();
    }

    public static boolean isUser() {
        return hasRole(Role.USER);
    }

    private static boolean hasRole(Role role) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated() && authentication.getAuthorities().contains(role);
    }

    public static boolean isAdmin() {
        return hasRole(Role.ADMIN);
    }

    public static boolean hasAnyRole(Role... roles) {
        for (Role role : roles) {
            if (hasRole(role)) {
                return true;
            }
        }
        return false;
    }

    public static User currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getDetails() instanceof User) {
            return ((User) authentication.getDetails());
        }
        if (authentication.getPrincipal() instanceof User) {
            return ((User) authentication.getPrincipal());
        }
        return null;
    }
}
