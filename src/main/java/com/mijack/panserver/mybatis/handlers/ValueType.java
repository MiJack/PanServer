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

package com.mijack.panserver.mybatis.handlers;

/**
 * 枚举的取值方式，按照id取、按照枚举名称取、按照特定字段取(暂不支持)
 *
 * @author Mi&Jack
 */
public enum ValueType {
    /**
     * 数据存储时，按照id取值
     */
    ID,
    /**
     * 数据存储时，按照枚举名称取值
     */
    NAME
    //, FIELD
}