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

import com.mijack.panserver.model.ChunkUploadInfo;
import com.mijack.panserver.model.StorageStatus;
import com.mijack.panserver.model.StorageUnit;

import java.util.List;

/**
 * @author Mi&Jack
 */
public interface ChunkStorageService {
    /**
     * 保存StorageUnit的Chunk
     *
     * @param chunk
     */
    void saveStorageUnitChunk(StorageUnit.Chunk chunk);

    /**
     * 更新Chunk的相关状态
     *
     * @param storageUnitId
     * @param chunkIndex
     * @param resourceInternalUri
     * @param storageStatus
     */
    void uploadChunkInfo(long storageUnitId, long chunkIndex, String resourceInternalUri, StorageStatus storageStatus);

    /**
     * 根据StorageUnitId找到所有的StorageUnit.Chunk
     *
     * @param storageUnitId
     * @return
     */
    List<StorageUnit.Chunk> findAllChunkInfoByStorageUnitId(long storageUnitId);

    /**
     * 获取当前storageUnitId对应的storageUnit的Chunk上传信息
     *
     * @param storageUnitId
     * @return
     */
    ChunkUploadInfo getChunkUploadInfoByStorageUnitId(long storageUnitId);

    /**
     * 判断storageUnitId对应的storageUnit的所有Chunk是否上传完毕
     *
     * @param storageUnitId
     * @return
     */
    boolean isAllChunkUploaded(long storageUnitId);

    /**
     * 获取当前storageUnitId对应的storageUnit的Chunk的ObjectId列表
     *
     * @param storageUnitId
     * @return
     */
    List<byte[]> findAllChunkObjectIdByStorageUnit(long storageUnitId);
}
