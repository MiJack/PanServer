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

package com.mijack.panserver.controller.api;

import com.mijack.panserver.model.Role;
import com.mijack.panserver.model.StorageResult;
import com.mijack.panserver.model.StorageUnit;
import com.mijack.panserver.model.UploadToken;
import com.mijack.panserver.model.User;
import com.mijack.panserver.service.ConstantService;
import com.mijack.panserver.service.StorageFacadeService;
import com.mijack.panserver.service.StorageUnitService;
import com.mijack.panserver.service.TokenService;
import com.mijack.panserver.util.Assert;
import com.mijack.panserver.util.OkioWrapper;
import com.mijack.panserver.web.security.AuthenticationUtils;
import com.mijack.panserver.web.security.HasAnyRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @author Mi&Jack
 */

@RestController
@RequestMapping("/api/storage")
public class ApiStorageController {

    Logger logger = LoggerFactory.getLogger(ApiStorageController.class);

    @Autowired
    ConstantService constantService;

    @Autowired
    StorageFacadeService storageFacadeService;

    @Autowired
    StorageUnitService storageUnitService;

    @Autowired
    TokenService tokenService;

    @GetMapping("/{storageUnitId}/info/")
    @ResponseBody
    public StorageUnit storageUnitInfo(@PathVariable("storageUnitId") long storageUnitId) {
        StorageUnit storageUnit = storageUnitService.findStorageUnit(storageUnitId);
        return storageUnit;
    }

    @PostMapping("/uploadFileEntity")
    @ResponseBody
    @HasAnyRole(Role.USER)
    public StorageResult uploadFileEntity(
            @RequestParam("file-name") String fileName,
            @RequestParam("file-length") long fileLength,
            @RequestParam("file-content-type") String contentType,
            @RequestParam("file-digest") String fileDigest,
            @RequestParam("file-part") MultipartFile filePart) throws IOException {
        logger.info("[uploading file]:name: {} , length: {}, digest: {}",
                fileName, fileLength, fileDigest);
        User user = AuthenticationUtils.currentUser();
        Assert.isLessThan(fileLength, constantService.getStorageSettings().getChunkSizeLimit())
                .orThrow(MaxUploadSizeExceededException.class, constantService.getStorageSettings().getChunkSizeLimit());

        Assert.isEquals(fileLength, filePart.getSize())
                .orThrow("文件大小与长度不符");
        return storageFacadeService.saveFileEntity(user.getId(), fileName, fileLength, contentType, fileDigest,
                OkioWrapper.toByteArray(filePart.getInputStream()));
    }

    @PostMapping("/uploadChunk")
    @ResponseBody
    public StorageResult uploadFileChunk(@RequestParam("upload-token") String uploadToken,
                                         @RequestParam("mdu-id") long storageUnitId,
                                         @RequestParam("chunk-index") long chunkIndex,
                                         @RequestParam("chunk-count") long chunkCount,
                                         @RequestParam("chunk-length") long chunkLength,
                                         @RequestParam("chunk-digest") String chunkDigest,
                                         @RequestParam("chunk-part") MultipartFile chunkPart) throws IOException {
        User user = AuthenticationUtils.currentUser();

        long[] decodeToken = tokenService.decodeToken(uploadToken);

        Assert.isLessThan(chunkLength, constantService.getStorageSettings().getChunkSizeLimit())
                .orThrow(MaxUploadSizeExceededException.class, constantService.getStorageSettings().getChunkSizeLimit());
        Assert.isEquals(user.getId(), decodeToken[0])
                .orThrow("该token不是该用户的token");

        Assert.isTrue(user.hasRole(Role.USER))
                .orThrow("当前用户无法上传文件");

        Assert.isEquals(chunkLength, chunkPart.getSize())
                .orThrow("文件大小与长度不符");

        return storageFacadeService.saveFileChunk(user.getId(), storageUnitId, uploadToken, chunkIndex, chunkCount,
                chunkLength, chunkDigest, OkioWrapper.toByteArray(chunkPart.getInputStream()));
    }

    @PostMapping("/applyUploadToken")
    @ResponseBody
    public UploadToken applyUploadToken(@RequestParam("file-name") String fileName,
                                        @RequestParam("file-length") long fileLength,
                                        @RequestParam("file-content-type") String contentType,
                                        @RequestParam("file-digest") String fileDigest) {
        User user = AuthenticationUtils.currentUser();
        Assert.isTrue(user.hasRole(Role.USER))
                .orThrow("当前用户无法上传文件");

        logger.info("[uploading file]  name: {} , length: {}, digest: {}", fileName, fileLength, fileDigest);
        UploadToken uploadToken = storageFacadeService.applyUploadToken(user.getId(), fileName, fileLength, contentType,
                fileDigest);
        return uploadToken;
    }
}