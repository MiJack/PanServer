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

import lombok.Data;

import java.util.Date;

/**
 * @author Mi&Jack
 */

@Data
public class StorageUnit {
    private long id;
    private String name;
    private String mineType;
    private String storageUri;
    private long length;
    private User uploader;
    private String fileDigest;
    private String coverUri;
    private Date createTime;
    private Date updateTime;
    private StorageStatus status;

    @Data
    public static class Chunk {
        private long id;
        private StorageUnit storageUnit;
        private String uri;
        private User uploader;
        private long length;
        private long index;
        private long count;
        private String updateToken;
        private StorageStatus status;
        private Date createTime;
        private Date updateTime;
    }
}
