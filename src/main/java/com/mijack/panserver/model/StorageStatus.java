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
public enum StorageStatus implements IdentifierEnum {
    /**
     * 初始化
     */
    STATUS_INIT(001, "初始化"),
    /**
     * 初始化，等待分块上传
     */
    STATUS_INIT_CHUNK(002, "初始化，等待分块上传"),

    /**
     * 上传中
     */
    STATUS_UPLOADING(101, "上传中"),
    /**
     * 分块上传完成，等待合并
     */
    STATUS_CHUNK_UPLOADED(102, "分块上传完成，等待合并"),
    /**
     * 分块上传数据块合并中
     */
    STATUS_CHUNK_MERGING(103, "分块上传数据块合并中"),

    /**
     * 上传完成
     */
    STATUS_UPLOADED(200, "上传完成"),
    /**
     * 分块上传数据块合并完成，上传结束
     */
    STATUS_CHUNK_MERGED(201, "分块上传数据块合并完成，上传结束"),
    /**
     * 分块上传数据块合并异常
     */
    STATUS_CHUNK_MERGED_FAILED(301, "分块上传数据块合并异常"),
    ;

    /**
     * 状态代码
     */
    private final int code;

    /**
     * 状态代码描述
     */
    private final String description;

    StorageStatus(int code, String description) {
        this.code = code;
        this.description = description;
    }

    @Override
    public int id() {
        return code;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }}
