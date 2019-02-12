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

package com.mijack.panserver.model;

import com.mijack.panserver.model.util.IdentifierEnum;
import lombok.Data;

import java.util.Date;

/**
 * @author Mi&Jack
 */
@Data
public class UserInvitation {
    private long id;
    private String email;
    private String token;
    private User operator;
    private Date inviteTime;
    private Date expireTime;
    private Status status;

    public enum Status implements IdentifierEnum {
        /**
         * 邀请生效中
         */
        ENABLE(1),
        /**
         * 邀请已失效
         */
        DISABLE(2),
        /**
         * 邀请已过期
         */
        EXPIRED(3);

        private int id;

        Status(int id) {
            this.id = id;
        }

        @Override
        public int id() {
            return id;
        }
    }
}
