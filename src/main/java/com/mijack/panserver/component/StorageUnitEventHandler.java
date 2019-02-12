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

package com.mijack.panserver.component;

import com.google.common.base.Function;
import com.lmax.disruptor.EventHandler;
import com.mijack.panserver.event.ApplicationStartupListener;
import com.mijack.panserver.model.StorageStatus;
import com.mijack.panserver.model.StorageStrategy;
import com.mijack.panserver.model.StorageUnit;
import com.mijack.panserver.service.ChunkStorageService;
import com.mijack.panserver.service.ConstantService;
import com.mijack.panserver.service.StorageUnitService;
import com.mijack.panserver.service.impl.GridFsService;
import com.mijack.panserver.util.Assert;
import com.mijack.panserver.util.CollectionHelper;
import com.mongodb.BasicDBObject;
import com.mongodb.client.gridfs.GridFSBucket;
import okio.Buffer;
import okio.ByteString;
import okio.Okio;
import org.bson.types.ObjectId;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.io.EOFException;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Mi&Jack
 */
@Component
public class StorageUnitEventHandler implements EventHandler<StorageUnit> {
    public static final Logger logger = LoggerFactory.getLogger(StorageUnitEventHandler.class);

    @Autowired
    ChunkStorageService chunkStorageService;
    @Autowired
    StorageUnitService storageMetaDataService;
    @Autowired
    GridFSBucket gridFSBucket;
    @Autowired
    GridFsService gridFsService;
    @Autowired
    ConstantService constantService;

    @Override
    public void onEvent(StorageUnit unit, long sequence, boolean endOfBatch) throws Exception {

        // 获取所有的chunk

        logger.info("合并MinDisplayUnit[" + unit.getId() + "]");
        long unitId = unit.getId();
        if (!chunkStorageService.isAllChunkUploaded(unitId)) {
            logger.debug("mudId为{0}的块未上传完全");
            storageMetaDataService.updateStorageUnitStatus(unitId, StorageStatus.STATUS_CHUNK_MERGED_FAILED);
            return;
        }

        List<ObjectId> objectIds = CollectionHelper.transform(chunkStorageService.findAllChunkObjectIdByStorageUnit(unitId),
                new Function<byte[], ObjectId>() {
                    @Nullable
                    @Override
                    public ObjectId apply(byte @Nullable [] input) {
                        return new ObjectId(input);
                    }
                });
        // todo 从数据的拷贝转为修改

        Buffer buffer = new Buffer();

        BasicDBObject newFile = gridFsService.newFile(unit.getName());
        ObjectId newFileObjectId = newFile.getObjectId("_id");

        String chunksCollectionName = gridFsService.chunksCollectionName();

        int index = 0;
        long length = 0;

        DigestOutputStream digestOutputStream = new DigestOutputStream(Okio.buffer(Okio.blackhole()).outputStream(),
                MessageDigest.getInstance("md5"));
        List<Integer> chunkSizeList = gridFsService.getChunkSizeForObjectIds(objectIds);
        chunkSizeList = chunkSizeList.stream().distinct().filter(integer -> integer != -1)
                .sorted().collect(Collectors.toList());

        int chunkSize = chunkSizeList.size() == 1 ? chunkSizeList.get(0) : (int) constantService.getStorageSettings().getGridFSChunkSize();

        for (ObjectId objectId : objectIds) {
            BasicDBObject basicDBObject = gridFsService.findFile(objectId);
            if (chunkSize == -1) {
                chunkSize = basicDBObject.getInt("chunkSize", -1);
            }

            List<BasicDBObject> chucks = gridFsService.findChucks(objectId);
            for (BasicDBObject chuck : chucks) {
                byte[] data = (byte[]) chuck.get("data");
                buffer.write(data);
                digestOutputStream.write(data);
                while (buffer.size() >= chunkSize) {
                    addNewChunk(buffer, newFileObjectId, chunksCollectionName, index, chunkSize);
                    index++;
                    length += data.length;
                }
            }
        }
        if (buffer.size() > 0) {
            byte[] bytes = buffer.readByteArray();
            BasicDBObject dbObject = new BasicDBObject().append("n", index).append("data", bytes)
                    .append("files_id", newFileObjectId).append("_id", new ObjectId());
            gridFsService.insert(dbObject, chunksCollectionName);
            logger.info("chunk info: index = " + index + " , length = " + bytes.length);
            index++;
            length += bytes.length;
        }

        String md5 = ByteString.of(digestOutputStream.getMessageDigest().digest()).hex();
        Update update = new Update().set("md5", md5).set("length", length).set("chunkSize", chunkSize);
        gridFsService.updateDBObject(newFileObjectId, update, gridFsService.filesCollectionName());

        String fileDigest = unit.getFileDigest();
        String targetMd5 = fileDigest.substring("md5:".length());
        Assert.equalsIgnoreCase(targetMd5, md5).orThrow("签名校验不通过");
        logger.info("合并多个文件chunk完毕");
        // 更新元数据状态
        storageMetaDataService.updateStorageUnitStatus(unitId, StorageStatus.STATUS_CHUNK_MERGED);

        String storageUri = StorageStrategy.MongoDB.name() + ":" + newFileObjectId.toHexString();
        storageMetaDataService.updateStorageUnitStorageUri(unitId, storageUri);
        // todo 删除原有的数据

    }

    void addNewChunk(Buffer buffer, ObjectId objectId, String collectionName, int index, int size) throws EOFException {
        byte[] bytes = buffer.readByteArray(size);
        BasicDBObject dbObject = new BasicDBObject().append("n", index).append("data", bytes)
                .append("files_id", objectId).append("_id", new ObjectId());
        gridFsService.insert(dbObject, collectionName);
        logger.info("chunk info: index = " + index + " , length = " + bytes.length);
    }
}
