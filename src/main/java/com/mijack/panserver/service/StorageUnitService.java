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

package com.mijack.panserver.service;

import com.mijack.panserver.model.StorageStatus;
import com.mijack.panserver.model.StorageUnit;

/**
 * @author Mi&Jack
 */
public interface StorageUnitService {
    /**
     * 根据storageUnitId 查找storageUnit
     *
     * @param storageUnitId
     * @return
     */
    StorageUnit findStorageUnit(long storageUnitId);

    /**
     * 查找特定用户下的对应类型特定签名的文件
     *
     * @param userId
     * @param fileDigest
     * @param contentType
     * @return
     */
    StorageUnit findStorageUnit(long userId, String fileDigest, String contentType);

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
    StorageUnit saveFileEntity(long userId, String fileName, long fileLength, String contentType, String fileDigest, String resourceInternalUri);

    /**
     * 更新StorageUnit的Status
     *
     * @param storageUnitId
     * @param statusChunkMerging
     */
    void updateStorageUnitStatus(long storageUnitId, StorageStatus statusChunkMerging);

    /**
     * 更新StorageUnit的Uri
     *
     * @param storageUnitId
     * @param storageUri
     */
    void updateStorageUnitStorageUri(long storageUnitId, String storageUri);


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
    StorageUnit applyChunkFileMetaData(long userId, String fileName, long fileLength, String contentType, String fileDigest);
}
