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

import com.google.common.collect.Lists;
import com.mijack.panserver.util.CollectionHelper;
import com.mongodb.BasicDBObject;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * @author Mi&Jack
 */
public class GridFsService {

    private MongoTemplate mongoTemplate;
    private String gridFsDatabase;

    public GridFsService(MongoTemplate mongoTemplate, String gridFsDatabase) {
        this.mongoTemplate = mongoTemplate;
        this.gridFsDatabase = gridFsDatabase;
    }

    public BasicDBObject findFile(ObjectId objectId) {
        return mongoTemplate.findOne(Query.query(Criteria.where("_id").is(objectId)),
                BasicDBObject.class, filesCollectionName());
    }

    public List<BasicDBObject> findChucks(ObjectId fileObjectId) {
        List<BasicDBObject> chunkObjects = mongoTemplate.find(Query.query(Criteria.where("files_id").is(fileObjectId)),
                BasicDBObject.class, chunksCollectionName());
        CollectionHelper.sort(chunkObjects, Comparator.comparingInt(o -> o.getInt("n", -1)));
        return chunkObjects;
    }

    public BasicDBObject newFile(String name) {
        BasicDBObject basicDBObject = new BasicDBObject();
        basicDBObject.put("_id", new ObjectId());
        basicDBObject.put("filename", name);
        basicDBObject.put("length", -1);
        basicDBObject.put("chunkSize", "chunkSize");
        basicDBObject.put("uploadDate", new Date());
        basicDBObject.put("md5", "unknown");
        mongoTemplate.insert(basicDBObject, filesCollectionName());
        return basicDBObject;
    }

    public String filesCollectionName() {
        return gridFsDatabase + ".files";
    }

    public String chunksCollectionName() {
        return gridFsDatabase + ".chunks";
    }

    public void updateDBObject(ObjectId objectId, Update update, String collectionName) {
        mongoTemplate.updateFirst(Query.query(Criteria.where("_id").is(objectId)), update, collectionName);
    }

    public void insert(BasicDBObject basicDBObject, String collectionName) {
        mongoTemplate.insert(basicDBObject,collectionName);
    }

    public List<Integer> getChunkSizeForObjectIds(List<ObjectId> objectIds) {
        List<Integer> list = Lists.newArrayList();
        for (ObjectId objectId : objectIds) {
            BasicDBObject basicDBObject = findFile(objectId);
            int chunkSize = basicDBObject.getInt("chunkSize", -1);
            list.add(chunkSize);
        }
        return list;
    }
}
