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

import com.mijack.panserver.manager.ChunkStorageManager;
import com.mijack.panserver.model.ChunkUploadInfo;
import com.mijack.panserver.model.StorageStatus;
import com.mijack.panserver.model.StorageUnit;
import com.mijack.panserver.service.ChunkStorageService;
import com.mijack.panserver.service.StorageUnitService;
import com.mijack.panserver.util.Assert;
import com.mijack.panserver.util.CollectionHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Mi&Jack
 */
@Component
public class ChunkStorageServiceImpl implements ChunkStorageService {
    @Autowired
    private ChunkStorageManager chunkStorageManager;
    @Autowired
    StorageUnitService storageMetaDataService;

    @Override
    public void saveStorageUnitChunk(StorageUnit.Chunk chunk) {
        chunkStorageManager.saveStorageUnitChunk(chunk);
    }

    @Override
    public void uploadChunkInfo(long mduId, long chunkIndex, String resourceUri, StorageStatus storageStatus) {
        chunkStorageManager.uploadChunkInfo(mduId, chunkIndex, resourceUri, storageStatus);
    }

    @Override
    public List<StorageUnit.Chunk> findAllChunkInfoByStorageUnitId(long mduId) {
        return chunkStorageManager.findAllChunkInfoByStorageUnitId(mduId);
    }

    @Override
    public ChunkUploadInfo getChunkUploadInfoByStorageUnitId(long storageUnitId) {
        List<StorageUnit.Chunk> chunks = chunkStorageManager.findAllChunkInfoByStorageUnitId(storageUnitId);

        int chuckCount = chunks.size();
        long chunkUploadedCount = chunks.stream()
                .filter(chunk -> StorageStatus.STATUS_CHUNK_UPLOADED.equals(chunk.getStatus())).count();
        ChunkUploadInfo chunkUploadInfo = new ChunkUploadInfo();
        StorageUnit storageUnit = storageMetaDataService.findStorageUnit(storageUnitId);
        chunkUploadInfo.setStorageUnit(storageUnit);
        chunkUploadInfo.setChunkCount(chuckCount);
        chunkUploadInfo.setUploadedCount(chunkUploadedCount);
        chunkUploadInfo.setAllChunkUploaded(chunkUploadedCount == chuckCount);
        return chunkUploadInfo;
    }

    @Override
    public boolean isAllChunkUploaded(long mduId) {
        List<StorageUnit.Chunk> chunks = findAllChunkInfoByStorageUnitId(mduId);
        for (StorageUnit.Chunk chunk : chunks) {
            if (!chunk.getStatus().equals(StorageStatus.STATUS_CHUNK_UPLOADED)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public List<byte[]> findAllChunkObjectIdByStorageUnit(long storageUnitId) {
        List<StorageUnit.Chunk> chunks = findAllChunkInfoByStorageUnitId(storageUnitId);
        List<byte[]> objectIds = CollectionHelper.transform(chunks,
                chunk -> {
                    String uri = chunk.getUri();
                    int indexOf = uri.indexOf("://");
                    Assert.notEquals(indexOf, -1)
                            .orThrow("uri:" + uri + "不是一个合法的Uri，不符合格式\"schema://objectId\"");
                    String schema = uri.substring(0, indexOf);

                    Assert.equalsIgnoreCase(schema, "mongodb").orThrow("暂不支持MongoDB以外的资源存储方式");
                    return uri.substring(indexOf + 3).getBytes();
                });
        return objectIds;
    }

}
