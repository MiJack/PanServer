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

package com.mijack.panserver.manager.impl;

import com.mijack.panserver.dao.ChunkStorageRepository;
import com.mijack.panserver.manager.ChunkStorageManager;
import com.mijack.panserver.model.StorageStatus;
import com.mijack.panserver.model.StorageUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Mi&Jack
 */
@Component
public class ChunkStorageManagerImpl implements ChunkStorageManager {
    @Autowired
    ChunkStorageRepository chunkStorageRepository;

    @Override
    public void uploadChunkInfo(long mduId, long chunkIndex, String resourceUri, StorageStatus storageStatus) {
        chunkStorageRepository.uploadChunkInfo(mduId, chunkIndex, resourceUri, storageStatus);
    }

    @Override
    public void saveStorageUnitChunk(StorageUnit.Chunk chunk) {
        chunkStorageRepository.saveStorageUnitChunk(chunk);
    }

    @Override
    public List<StorageUnit.Chunk> findAllChunkInfoByStorageUnitId(long storageUnitId) {
        return chunkStorageRepository.findAllChunkInfoByStorageUnitId(storageUnitId);
    }
}
