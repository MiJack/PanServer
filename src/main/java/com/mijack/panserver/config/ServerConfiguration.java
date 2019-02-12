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

import com.mijack.panserver.component.DisruptorService;
import com.mijack.panserver.component.StorageUnitEventHandler;
import com.mijack.panserver.model.StorageSettings;
import com.mijack.panserver.model.StorageUnit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Mi&Jack
 */
@Configuration
public class ServerConfiguration {

    @Value("${application.file-upload.chunk-size-limit}")
    private long chunkSizeLimit;
    @Value("${application.file-upload.gridfs-chunk-size}")
    private long gridfsChunkSize;

    @Bean
    public StorageSettings storageSettings(
            @Value("${application.file-upload.chunk-size-limit}") long chunkSizeLimit,
            @Value("${application.file-upload.gridfs-chunk-size}") long gridfsChunkSize) {
        return new StorageSettings(chunkSizeLimit, gridfsChunkSize);
    }

    @Bean
    DisruptorService<StorageUnit> storageMergeService(StorageUnitEventHandler eventHandler) {
        return new DisruptorService<>(eventHandler);
    }
}
