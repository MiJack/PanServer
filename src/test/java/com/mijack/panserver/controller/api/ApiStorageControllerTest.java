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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mijack.panserver.model.StorageStatus;
import com.mijack.panserver.model.StorageStrategy;
import com.mijack.panserver.model.StorageUnit;
import com.mijack.panserver.model.UploadToken;
import com.mijack.panserver.service.TikaService;
import com.mijack.panserver.util.DigestHelper;
import com.mongodb.client.gridfs.GridFSBucket;
import okio.BufferedSource;
import okio.Okio;
import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@RunWith(SpringRunner.class)
@SpringBootTest()
@AutoConfigureMockMvc
public class ApiStorageControllerTest {
    public static final Logger logger = LoggerFactory.getLogger(ApiStorageControllerTest.class);
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private TikaService tikaService;
    @Autowired
    GridFSBucket fsBucket;
    ObjectMapper objectMapper = new ObjectMapper();


    @Before
    public void setUp() {
        fsBucket.drop();
    }
    @Test
    @WithUserDetails()
    public void uploadFileEntity() throws Exception {
        // 申请upload请求

        File file = new File("/Users/mijack/WorkSpaces/MiniPan/MiniPan-Server/api/src/main" +
                "/resources/static/imgs/gmail.jpeg");
        String fileDigest = "md5:" + DigestHelper.toDigest(new FileInputStream(file), "md5");
        MvcResult mvcResult = mockMvc.perform(multipart("/api/com.mijack.minipan.controller.storage/uploadFileEntity")
                .file(new MockMultipartFile("file-part", file.getName(), "image/jpeg", new FileInputStream(file)))
                .param("file-digest", fileDigest)
                .param("file-content-type", "image/jpeg")
                .param("file-length", String.valueOf(file.length()))
                .param("file-name", "timg.jpeg")

        ).andReturn();
        logger.error(mvcResult.getResponse().toString());
    }

    @Test
    @WithUserDetails()
    public void uploadFileChunk() throws Exception {

        String filePath = "/Users/mijack/图书/线性代数的几何意义.pdf";
        File file = new File(filePath);
        Assert.assertTrue(file.exists());
        String fileName = file.getName();
        long fileLength = file.length();
        String fileContentType = tikaService.extractMimeType(new FileInputStream(file));
        String fileDigest = "md5:" + DigestHelper.toDigest(new FileInputStream(file), "md5");

        String constantContent = mockMvc.perform(get("/api/constant")).andReturn().getResponse().getContentAsString();
        long chunkSizeLimit = objectMapper.readTree(constantContent).get("chunk-size-limit").asLong();

        // 申请Token
        String applyUpdateTokenContent = mockMvc.perform(
                post("/api/com.mijack.minipan.controller.storage/applyUploadToken")
                        .param("file-name", fileName)
                        .param("file-length", String.valueOf(fileLength))
                        .param("file-content-type", fileContentType)
                        .param("file-digest", fileDigest)
        ).andReturn().getResponse().getContentAsString();


        UploadToken map = objectMapper.readValue(applyUpdateTokenContent, UploadToken.class);
        String uploadToken = map.getUploadToken();
        long chunkCount = (fileLength / chunkSizeLimit) + (fileLength % chunkSizeLimit > 0 ? 1 : 0);
        long mduId = map.getMinDisplayUnitId();
        BufferedSource buffer = Okio.buffer(Okio.source(file));

        for (long chunkIndex = 0; chunkIndex < chunkCount; chunkIndex++) {

            long chunkLength = (chunkIndex == chunkCount - 1 && fileLength % chunkSizeLimit > 0) ?
                    (fileLength % chunkSizeLimit) : chunkSizeLimit;

            byte[] bytes = buffer.readByteArray(chunkLength);

            String chunkDigest = "md5:" + DigestHelper.toDigest(new ByteArrayInputStream(bytes), "md5");

            logger.info("chunkIndex:" + chunkIndex + "\tchunkDigest:" + chunkDigest);
            MvcResult result = mockMvc.perform(
                    multipart("/api/com.mijack.minipan.controller.storage/uploadChunk")
                            .file("chunk-part", bytes)
                            .param("upload-token", uploadToken)
                            .param("mdu-id", String.valueOf(mduId))
                            .param("chunk-index", String.valueOf(chunkIndex))
                            .param("chunk-count", String.valueOf(chunkCount))
                            .param("chunk-length", String.valueOf(chunkLength))
                            .param("chunk-digest", chunkDigest)
            ).andReturn();
            String contentAsString = result.getResponse().getContentAsString();

            logger.info("contentAsString:" + contentAsString);
        }
        logger.info("文件上传成功！！！");
        int i = 1;
        StorageUnit minDisplayUnit = null;
        do {
            MvcResult mvcResult = mockMvc.perform(get("/api/com.mijack.minipan.controller.storage/" + mduId + "/info/")).andReturn();
            String content = mvcResult.getResponse().getContentAsString();
            minDisplayUnit = objectMapper.readValue(content, StorageUnit.class);
            if (checkMduStatus(minDisplayUnit)) break;
            Thread.sleep(10 * 1000);
            i++;
        } while (i < 10);
        if (minDisplayUnit == null) {
            Assert.fail("文件合并超时");
        } else {
            logger.info("minDisplayUnit:" + minDisplayUnit);

            String storageUri = minDisplayUnit.getStorageUri();

            File file1 = new File("demo.pdf");
            fsBucket.downloadToStream(new ObjectId(storageUri.substring(StorageStrategy.MongoDB.schema().length() + 1)), new FileOutputStream(file1));

            logger.info("下载文件到" + file1.getAbsolutePath());
        }
    }

    private boolean checkMduStatus(StorageUnit minDisplayUnit) throws Exception {

        StorageStatus status = minDisplayUnit.getStatus();
        if (status.equals(StorageStatus.STATUS_CHUNK_MERGING)) {
            logger.info("大文件[mdu = " + minDisplayUnit.getId() + "]合并中");
        } else if (status.equals(StorageStatus.STATUS_INIT_CHUNK)) {
            logger.info("大文件[mdu = " + minDisplayUnit.getId() + "]等待上传");
            Assert.fail("状态错误：等待上传");
        } else if (status.equals(StorageStatus.STATUS_CHUNK_MERGED)) {
            logger.info("大文件[mdu = " + minDisplayUnit.getId() + "]上传成功");
            return true;
        } else {
            Assert.fail("状态错误:" + status);
        }
        return false;
    }

}