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

package com.mijack.panserver.manager;

import com.mijack.panserver.model.StorageStatus;
import com.mijack.panserver.model.StorageUnit;

import java.util.List;

/**
 * @author Mi&Jack
 */
public interface ChunkStorageManager {
    /**
     * 保存StorageUnit.Chunk
     *
     * @param chunk 
     */
    void saveStorageUnitChunk(StorageUnit.Chunk chunk);

    /**
     * 上传Chun信息
     *
     * @param storageUnitId 
     * @param chunkIndex    
     * @param resourceUri   
     * @param storageStatus 
     */
    void uploadChunkInfo(long storageUnitId, long chunkIndex, String resourceUri, StorageStatus storageStatus);

    /**
     * 查找和storageUnitId相关的Chunk信息
     *
     * @param storageUnitId 
     * @return 
     */
    List<StorageUnit.Chunk> findAllChunkInfoByStorageUnitId(long storageUnitId);
}
