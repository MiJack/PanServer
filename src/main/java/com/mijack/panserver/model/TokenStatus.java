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

/**
 * @author Mi&Jack
 */
public enum TokenStatus implements IdentifierEnum {
    /**
     * token状态正常
     */
    OK(0, "token状态正常"),
    /**
     * token格式错误
     */
    BAD_FORMAT_TOKEN(1, "token格式错误"),
    /**
     * token已过期
     */
    EXPIRATION_TOKEN(2, "token已过期");

    TokenStatus(int id, String desc) {
        this.id = id;
        this.desc = desc;
    }

    private int id;
    private String desc;

    @Override
    public int id() {
        return id;
    }

    public boolean isValidate() {
        return this.equals(OK);
    }

    public String desc() {
        return desc;
    }
}
