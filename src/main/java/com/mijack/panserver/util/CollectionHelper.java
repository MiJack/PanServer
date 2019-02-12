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

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * @author Mi&Jack
 */
public class CollectionHelper {
    public static <T> boolean isEmpty(Collection<T> collection) {
        return collection == null ? true : collection.isEmpty();
    }

    public static <T, R> List<R> transform(List<T> source, Function<T, R> function) {
        List<R> list = Lists.newArrayList();
        for (T item : source) {
            list.add(function.apply(item));
        }
        return list;
    }

    public static <T, R> Map<T, R> transformToMap(List<T> list, Function<T, R> function) {
        Map<T, R> map = Maps.newHashMap();
        for (T item : list) {
            map.put(item, function.apply(item));
        }
        return map;
    }


    public static <T> void sort(List<T> list, Comparator<T> comparator) {
        if (isEmpty(list)) {
            return;
        }
        list.sort((o1, o2) -> {
            if (o1 == null && o2 == null) {
                return 0;
            }
            if (o1 == null && o2 != null) {
                return -1;
            }
            if (o1 != null && o2 == null) {
                return 1;
            }
            return comparator.compare(o1, o2);
        });
    }

    public static int size(Collection<?> collection) {
        return collection == null ? 0 : collection.size();
    }

    public static int size(long[] array) {
        return array == null ? 0 : array.length;
    }
}
