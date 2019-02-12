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

/**
 * @author Mi&Jack
 */
public interface MongoStorageService {
    /**
     * 将文件储存在MongoDB中
     *
     * @param fileName
     * @param fileLength
     * @param fileDigest
     * @param fileByte
     * @return
     */
    String saveFileEntity(String fileName, long fileLength, String fileDigest, byte[] fileByte);

    /**
     * 将块文件储存在MongoDB中
     *
     * @param uploadToken
     * @param chunkIndex
     * @param chunkCount
     * @param chunkLength
     * @param chunkDigest
     * @param byteArray
     * @return
     */
    String saveFileChunk(String uploadToken, long chunkIndex, long chunkCount, long chunkLength,
                         String chunkDigest, byte[] byteArray);

    /**
     * 返回对应服务的scheme，用于构建内部的url
     *
     * @return
     */
    String serviceSchema();
}
