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

import java.util.Collection;

/**
 * @author Mi&Jack
 */

public class Assert {

    public static Assertion notNull(Object o) {
        return newAssertion(o != null);
    }

    private static Assertion newAssertion(boolean condition) {
        return new Assertion(condition);
    }

    public static Assertion isNull(Object o) {
        return newAssertion(o == null);
    }


    public static <T> Assertion contains(Collection<T> collection, T t) {
        return newAssertion(collection.contains(t));
    }

    public static Assertion isTrue(boolean condition) {
        return newAssertion(condition);
    }

    public static Assertion isFalse(boolean condition) {
        return newAssertion(!condition);
    }

    public static Assertion isLessThan(long source, long target) {
        return newAssertion(source <= target);
    }

    public static Assertion isGreatThan(long source, long target) {
        return newAssertion(source >= target);
    }

    public static <T> Assertion isEquals(T source, T target) {
        return newAssertion(source.equals(target));
    }

    public static <T> Assertion notEquals(T source, T target) {
        return newAssertion(!source.equals(target));
    }

    public static Assertion equalsIgnoreCase(String source, String target) {
        return newAssertion(source.equalsIgnoreCase(target));
    }

    public static Assertion notEqualsIgnoreCase(String source, String target) {
        return newAssertion(source.equalsIgnoreCase(target));
    }

    public static Assertion notEmpty(Collection<?> collection) {
        return newAssertion(!collection.isEmpty());
    }

    public static Assertion isEmpty(Collection<?> collection) {
        return newAssertion(collection.isEmpty());
    }
}
