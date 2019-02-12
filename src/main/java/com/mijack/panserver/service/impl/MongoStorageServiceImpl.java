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

import com.google.common.collect.ImmutableMap;
import com.mijack.panserver.model.StorageStrategy;
import com.mijack.panserver.service.MongoStorageService;
import com.mijack.panserver.util.Assert;
import com.mijack.panserver.util.DigestHelper;
import com.mijack.panserver.util.StringHelper;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.model.GridFSUploadOptions;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.security.DigestInputStream;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.UUID;

/**
 * @author Mi&Jack
 */
@Component
public class MongoStorageServiceImpl implements MongoStorageService {
    Map<String, String> digestMap = new ImmutableMap.Builder<String, String>()
            .put("md5", "MD5").put("md-5", "MD5").put("MD5", "MD5").put("MD-5", "MD5")
            .build();

    @Autowired
    GridFSBucket gridFSBucket;

    @Value("${application.file-upload.gridfs-chunk-size}")
    private int gridFSChunkSize;

    @Override
    public String saveFileEntity(String fileName, long fileLength, String fileDigest, byte[] byteArray) {
        try {
            Assert.isTrue(fileLength == byteArray.length)
                    .orThrow("文件长度与实际不符，" + fileLength + " != " + byteArray.length);

            int indexOf = fileDigest.indexOf(":");
            Assert.isGreatThan(indexOf, -1).orThrow("文件摘要（" + fileDigest + "）不符合格式'摘要算法名:摘要值'");

            String digestAlgorithm = digestMap.get(fileDigest.substring(0, indexOf));
            DigestInputStream dis = DigestHelper.toDigestInputStream(new ByteArrayInputStream(byteArray), digestAlgorithm);
            ObjectId objectId = gridFSBucket.uploadFromStream(UUID.randomUUID().toString(), dis, defaultUploadOptions());
            String messageDigest = StringHelper.getMessageDigest(dis);
            String uploadDigest = fileDigest.substring(indexOf + 1);
            Assert.equalsIgnoreCase(messageDigest, uploadDigest)
                    .orThrow("文件签名校验不通过，" + messageDigest + " != " + uploadDigest);
            return objectId.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private GridFSUploadOptions defaultUploadOptions() {
        return new GridFSUploadOptions().chunkSizeBytes(gridFSChunkSize);
    }

    @Override
    public String saveFileChunk(String uploadToken, long chunkIndex, long chunkCount, long chunkLength,
                                String chunkDigest, byte[] byteArray) {
        try {
            int indexOf = chunkDigest.indexOf(":");
            Assert.isGreatThan(indexOf, -1).orThrow(
                    "bucket 摘要（" + chunkDigest + "）不符合格式'摘要算法名:摘要值'");
            String digestAlgorithm = digestMap.get(chunkDigest.substring(0, indexOf));
            DigestInputStream dis = DigestHelper.toDigestInputStream(new ByteArrayInputStream(byteArray), digestAlgorithm);
            ObjectId objectId = gridFSBucket.uploadFromStream(UUID.randomUUID().toString(), dis, defaultUploadOptions());

            String messageDigest = StringHelper.getMessageDigest(dis);
            String uploadDigest = chunkDigest.substring(indexOf + 1);
            Assert.equalsIgnoreCase(messageDigest, uploadDigest)
                    .orThrow("文件签名校验不通过，" + messageDigest + " != " + uploadDigest);

            return objectId.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public String serviceSchema() {
        return StorageStrategy.MongoDB.schema();
    }
}
