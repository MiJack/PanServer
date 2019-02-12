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

import java.lang.reflect.Constructor;

/**
 * @author Mi&Jack
 */

public class Assertion {

    private boolean condition;

    public Assertion(boolean condition) {
        this.condition = condition;
    }

    public void orThrow(String msg) {
        if (condition) {
            return;
        }
        throw new RuntimeException(msg);
    }

    public void orThrow(Class<? extends Throwable> throwableClazz, Object... args) {
        if (condition) {
            return;
        }
        try {
            if (args == null || args.length == 0) {
                throw new RuntimeException(throwableClazz.newInstance());
            }
            Class[] clazzes = new Class[args.length];
            for (int i = 0; i < args.length; i++) {
                clazzes[i] = args[i].getClass();
            }
            Constructor<? extends Throwable> constructor = throwableClazz.getConstructor(clazzes);
            Throwable throwable = constructor.newInstance(args);
            throw new RuntimeException(throwable);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}