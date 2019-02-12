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

import com.mijack.panserver.component.DisruptorService;
import com.mijack.panserver.model.ChunkUploadInfo;
import com.mijack.panserver.model.StorageResult;
import com.mijack.panserver.model.StorageStatus;
import com.mijack.panserver.model.StorageUnit;
import com.mijack.panserver.model.UploadToken;
import com.mijack.panserver.model.User;
import com.mijack.panserver.service.ChunkStorageService;
import com.mijack.panserver.service.MongoStorageService;
import com.mijack.panserver.service.StorageFacadeService;
import com.mijack.panserver.service.StorageUnitService;
import com.mijack.panserver.service.TokenService;
import com.mijack.panserver.service.UserService;
import com.mijack.panserver.util.Assert;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author Mi&Jack
 */
@Component
public class StorageFacadeServiceImpl implements StorageFacadeService, InitializingBean {
    @Value("${application.file-upload.chunk-size-limit}")
    private long chunkSizeLimit;
    @Value("${application.file-upload.gridfs-chunk-size}")
    private int gridFSChunkSize;

    @Autowired
    private StorageUnitService storageUnitDataService;
    @Autowired
    private MongoStorageService mongoStorageService;
    @Autowired
    private ChunkStorageService chunkStorageService;
    @Autowired
    private UserService userService;
    @Autowired
    private TokenService tokenService;
    @Autowired
    @Qualifier("storageMergeService")
    DisruptorService<StorageUnit> disruptorService;


    @Override
    public StorageResult saveFileEntity(long userId, String fileName, long fileLength, String contentType,
                                        String fileDigest, byte[] fileByte) {
        StorageUnit storageUnit = storageUnitDataService.findStorageUnit(userId, fileDigest, contentType);
        if (storageUnit != null) {
            // 文件已经存在
            return StorageResult.failure(StorageResult.RESULT_DIGEST_EXIST, fileDigest);
        }
        String objectId = mongoStorageService.saveFileEntity(fileName, fileLength, fileDigest, fileByte);
        String resourceInternalUri = mongoStorageService.serviceSchema() + "://" + objectId;

        storageUnit = storageUnitDataService.saveFileEntity(userId, fileName, fileLength, contentType,
                fileDigest, resourceInternalUri);
        return StorageResult.success(storageUnit);
    }

    @Override
    public StorageResult saveFileChunk(long userId, long storageUnitId, String uploadToken, long chunkIndex, long chunkCount, long chunkLength, String chunkDigest, byte[] fileByte) {
        // 超时检验
        long[] decodeParts = tokenService.decodeToken(uploadToken);
        if (decodeParts[decodeParts.length - 1] < System.currentTimeMillis()) {
            return StorageResult.failure(StorageResult.RESULT_UPDATE_TOKEN_EXPIRE);
        }
        String objectId = mongoStorageService.saveFileChunk(uploadToken, chunkIndex, chunkCount, chunkLength,
                chunkDigest, fileByte);
        String resourceInternalUri = mongoStorageService.serviceSchema() + "://" + objectId;
        chunkStorageService.uploadChunkInfo(storageUnitId, chunkIndex, resourceInternalUri, StorageStatus.STATUS_CHUNK_UPLOADED);
        ChunkUploadInfo chunkUploadInfo = chunkStorageService.getChunkUploadInfoByStorageUnitId(storageUnitId);

        boolean allChunkUploaded = chunkUploadInfo.isAllChunkUploaded();

        StorageResult.StorageCode storageCode = allChunkUploaded ?
                StorageResult.RESULT_CHUNK_UPLOAD_COMPLETE : StorageResult.RESULT_CHUNK_UPLOAD_OK;

        String msg = allChunkUploaded ?
                StorageResult.RESULT_CHUNK_UPLOAD_COMPLETE.formatMessage(storageUnitId, chunkIndex, chunkCount) :
                StorageResult.RESULT_CHUNK_UPLOAD_OK.formatMessage(storageUnitId, chunkIndex);
        if (allChunkUploaded) {
            storageUnitDataService.updateStorageUnitStatus(storageUnitId, StorageStatus.STATUS_CHUNK_MERGING);
            disruptorService.publish(chunkUploadInfo.getStorageUnit());
        }
        return StorageResult.success(storageCode.code(), msg, chunkUploadInfo);

    }

    @Override
    public UploadToken applyUploadToken(long userId, String fileName, long fileLength, String contentType, String fileDigest) {
        // Step 1： 申请元数据资源
        StorageUnit storageUnit = storageUnitDataService.applyChunkFileMetaData(userId, fileName, fileLength,
                contentType, fileDigest);
        long chunkCount = fileLength / chunkSizeLimit + (fileLength % chunkSizeLimit > 0 ? 1 : 0);
        // Step 2: 生成上传token
        long requestTimeMillis = System.currentTimeMillis();
        long expireTimeMillis = requestTimeMillis + TimeUnit.DAYS.toMillis(1);
        String token = tokenService.generateChunkUploadToken(userId, storageUnit.getId(), fileName, fileLength,
                chunkCount, fileDigest, requestTimeMillis, expireTimeMillis);
        // Step 3: 更新上传的token
        prepareForChunkUpload(userId, storageUnit.getId(), fileLength,
                chunkCount, token);
        UploadToken uploadToken = new UploadToken(storageUnit.getId(), fileName, fileLength, token);
        return uploadToken;
    }

    private void prepareForChunkUpload(long userId, long storageUnitId, long fileLength, long chunkCount, String updateToken) {
        // todo 优化参数传递过程
        User user = userService.findUserById(userId);
        StorageUnit storageUnit = storageUnitDataService.findStorageUnit(storageUnitId);
        for (int i = 0; i < chunkCount; i++) {
            StorageUnit.Chunk chunk = new StorageUnit.Chunk();
            chunk.setIndex(i);
            chunk.setUploader(user);
            long chunkLength = chunkSizeLimit;
            if (i == chunkCount - 1 && fileLength % chunkSizeLimit > 0) {
                chunkLength = fileLength % chunkSizeLimit;
            }
            chunk.setLength(chunkLength);
            chunk.setStorageUnit(storageUnit);
            chunk.setCount(chunkCount);
            chunk.setUpdateToken(updateToken);
            chunk.setStatus(StorageStatus.STATUS_INIT_CHUNK);

            Date date = new Date();
            chunk.setCreateTime(date);
            chunk.setUpdateTime(date);
            chunkStorageService.saveStorageUnitChunk(chunk);
        }
    }

    @Override
    public void afterPropertiesSet() {
        Assert.isTrue(chunkSizeLimit % gridFSChunkSize == 0)
                .orThrow("值chunkSizeLimit应可以被gridFSChunkSize整除");
    }
}
