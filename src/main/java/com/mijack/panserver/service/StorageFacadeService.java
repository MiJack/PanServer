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

import com.mijack.panserver.model.StorageResult;
import com.mijack.panserver.model.UploadToken;

/**
 * @author Mi&Jack
 */
public interface StorageFacadeService {
    /**
     * 保存完整的文件到存储介质中
     *
     * @param userId      用户名
     * @param fileName    文件名
     * @param fileLength  文件长度
     * @param contentType 文件类型
     * @param fileDigest  文件签名
     * @param fileByte    文件数据
     * @return
     */
    StorageResult saveFileEntity(long userId, String fileName, long fileLength, String contentType, String fileDigest, byte[] fileByte);

    /**
     * 保存文件的一部分数据到存储介质中
     *
     * @param userId        用户名
     * @param storageUnitId storageUnit Id
     * @param uploadToken   上传的token
     * @param chunkIndex    chunk 序号
     * @param chunkCount    chunk 总数
     * @param chunkLength   chunk 大小
     * @param chunkDigest   chunk 签名
     * @param fileByte      文件数据
     * @return
     */
    StorageResult saveFileChunk(long userId, long storageUnitId, String uploadToken, long chunkIndex, long chunkCount, long chunkLength, String chunkDigest, byte[] fileByte);

    /**
     * 申请上传文件所需的token
     *
     * @param userId
     * @param fileName
     * @param fileLength
     * @param contentType
     * @param fileDigest
     * @return
     */
    UploadToken applyUploadToken(long userId, String fileName, long fileLength, String contentType, String fileDigest);
}
