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

package com.mijack.panserver.config;

import com.mijack.panserver.service.impl.GridFsService;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;

/**
 * @author Mi&Jack
 */
@Configuration
@EnableConfigurationProperties(MongoProperties.class)
public class WebMongoConfiguration {

    @Autowired
    MongoProperties mongoProperties;

    @Bean
    MongoClient mongoClient() {
        return new MongoClient(mongoProperties.getHost(), mongoProperties.getPort());
    }

    @Bean
    MongoDatabase mongoDatabase() {
        return mongoClient().getDatabase(mongoProperties.getDatabase());
    }

    @Bean
    public GridFSBucket gridFSBucket(MongoDatabase mongoDatabase) {
        return GridFSBuckets.create(mongoDatabase, mongoProperties.getGridFsDatabase());
    }

    @Bean
    public GridFsService gridFsService() {
        GridFsService gridFsService = new GridFsService(mongoTemplate(), mongoProperties.getGridFsDatabase());
        return gridFsService;
    }

    @Bean
    public MongoTemplate mongoTemplate() {
        return new MongoTemplate(new SimpleMongoDbFactory(mongoClient(), mongoProperties.getDatabase()));
    }

}
