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

package com.mijack.panserver.util;

import com.mijack.panserver.model.util.IdentifierEnum;

/**
 * @author Mi&Jack
 */
public class EnumUtils {

    public static <T extends Enum<T>> T valueOf(int id, Class<T> clazz, int defaultId) {
        if (IdentifierEnum.class.isAssignableFrom(clazz)) {
            for (T t : clazz.getEnumConstants()) {
                if (((IdentifierEnum) t).id() == id) {
                    return t;
                }
            }
            for (T t : clazz.getEnumConstants()) {
                if (((IdentifierEnum) t).id() == defaultId) {
                    return t;
                }
            }
        }
        return null;
    }

    public static <T extends Enum<T>> T valueOf(String value, Class<T> clazz, String defaultValue) {
        for (T t : clazz.getEnumConstants()) {
            if (t.name().equals(value)) {
                return t;
            }
        }
        for (T t : clazz.getEnumConstants()) {
            if (t.name().equals(defaultValue)) {
                return t;
            }
        }
        return null;
    }

}
