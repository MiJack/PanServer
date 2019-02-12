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

package com.mijack.panserver.service.impl;

import com.mijack.panserver.model.StorageStatus;
import com.mijack.panserver.model.StorageUnit;
import com.mijack.panserver.service.StorageUnitService;
import org.springframework.stereotype.Component;

/**
 * @author Mi&Jack
 */
@Component
public class StorageUnitServiceImpl implements StorageUnitService {
    @Override
    public StorageUnit findStorageUnit(long storageUnitId) {
        return null;
    }

    /**
     * 查找特定用户下的对应类型特定签名的文件
     *
     * @param userId
     * @param fileDigest
     * @param contentType
     * @return
     */
    @Override
    public StorageUnit findStorageUnit(long userId, String fileDigest, String contentType) {
        return null;
    }

    /**
     * 保存storage的相关信息
     * todo 将参数改为POJO
     *
     * @param userId
     * @param fileName
     * @param fileLength
     * @param contentType
     * @param fileDigest
     * @param resourceInternalUri
     * @return
     */
    @Override
    public StorageUnit saveFileEntity(long userId, String fileName, long fileLength, String contentType, String fileDigest, String resourceInternalUri) {
        return null;
    }

    /**
     * 更新StorageUnit的Status
     *
     * @param storageUnitId
     * @param statusChunkMerging
     */
    @Override
    public void updateStorageUnitStatus(long storageUnitId, StorageStatus statusChunkMerging) {

    }

    /**
     * 更新StorageUnit的Uri
     *
     * @param storageUnitId
     * @param storageUri
     */
    @Override
    public void updateStorageUnitStorageUri(long storageUnitId, String storageUri) {

    }

    /**
     * 为分块上传的文件申请StorageUnit
     *
     * @param userId
     * @param fileName
     * @param fileLength
     * @param contentType
     * @param fileDigest
     * @return
     */
    @Override
    public StorageUnit applyChunkFileMetaData(long userId, String fileName, long fileLength, String contentType, String fileDigest) {
        return null;
    }
}
