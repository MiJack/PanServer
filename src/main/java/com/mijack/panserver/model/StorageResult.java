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

import com.mijack.messagepattern.MessagePattern;
import lombok.Getter;

/**
 * @author Mi&Jack
 */
@Getter
public class StorageResult<T> {
    public static final StorageCode RESULT_OK =
            new StorageCode(200, "文件保存成功");
    public static final StorageCode RESULT_CHUNK_UPLOAD_OK =
            new StorageCode(201, "分块上传文件[{0:mudId}]的第{1:index}块已经上传成功");
    public static final StorageCode RESULT_CHUNK_UPLOAD_COMPLETE =
            new StorageCode(202, "分块上传文件[{0:mudId}]的第{1:index}块已经上传成功，{2:count}块数据块上传完成");
    public static final StorageCode RESULT_DIGEST_EXIST =
            new StorageCode(301, "该摘要[{0:digest}]对应的文件已经存在");
    public static final StorageCode RESULT_UPDATE_TOKEN_EXPIRE = new StorageCode(-2,
            "分块上传token[0:token]已过期");

    private final int code;
    private final String msg;
    private T object;
    private final boolean success;

    public StorageResult(int code, String msg, T object, boolean success) {
        this.code = code;
        this.msg = msg;
        this.object = object;
        this.success = success;
    }

    public static StorageResult failure(StorageCode code, Object... args) {
        return new StorageResult(code.code, code.formatMessage(args), null, false);
    }

    public static StorageResult success(StorageCode code, Object o) {
        return new StorageResult(code.code, code.messagePattern.format(), o, true);
    }

    public static StorageResult success(int code,String msg, Object o) {
        return new StorageResult(code, msg, o, true);
    }

    public static <T> StorageResult<T> success(T object) {
        return new StorageResult<>(RESULT_OK.code, RESULT_OK.formatMessage(), object, true);
    }

    public static class StorageCode {
        private final int code;
        private final MessagePattern messagePattern;

        public int code() {
            return code;
        }

        public StorageCode(int code, String pattern) {
            this.code = code;
            this.messagePattern = new MessagePattern(pattern);
        }

        public String formatMessage(Object... args) {
            return messagePattern.format(args);
        }
    }
}
