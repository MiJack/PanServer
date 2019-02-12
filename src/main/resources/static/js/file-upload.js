/*
 * Copyright 2019  Mi&Jack
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

$(document).ready(function () {
    var blobSlice = File.prototype.slice || File.prototype.mozSlice || File.prototype.webkitSlice,

        chunkSizeLimit = -1
    $.get('/api/constant', {}, function (data) {
        chunkSizeLimit = data['chunk-size-limit']
    });

    var calculateFileMd5 = function (file, ele) {
        var fileReader = new FileReader(),
            chunkSize = 2 * 1024 * 1024, // read in chunks of 2MB
            chunks = Math.ceil(file.size / chunkSize),
            currentChunk = 0;
        spark = new SparkMD5();
        fileReader.onload = function (e) {
            // console.log("read chunk nr", currentChunk + 1, "of", chunks);
            spark.appendBinary(e.target.result); // append binary string
            currentChunk++;

            if (currentChunk < chunks) {
                loadNext();
            } else {
                console.log("finished loading");
                let md5 = spark.end();
                ele.val('md5:' + md5)
                console.info("computed hash", md5); // compute hash
            }
        };

        var loadNext = function () {
            var start = currentChunk * chunkSize,
                end = start + chunkSize >= file.size ? file.size : start + chunkSize;
            fileReader.readAsBinaryString(blobSlice.call(file, start, end));
        };
        loadNext();
    };

    var uploadChunk = function (uploadToken, mduId, chunkIndex, chunkCount, chunkLength, chunkDigest, chunkPart) {
        let fd = new FormData();
        fd.append("upload-token", uploadToken);
        fd.append("mdu-id", mduId);
        fd.append("chunk-index", chunkIndex);
        fd.append("chunk-count", chunkCount);
        fd.append("chunk-length", chunkLength);
        fd.append("chunk-digest", 'md5:' + chunkDigest);
        fd.append("chunk-part", chunkPart);
        $.ajax({
            type: 'POST',
            headers: {
                "token": Cookies.get('restfulToken')
            },
            enctype: 'multipart/form-data',
            url: "/api/com.mijack.minipan.controller.storage/uploadChunk",
            processData: false,
            contentType: false,
            data: fd,
            success: function (data) {
                console.log(data);
            }
        });
    }

    var calculateFileChunkMd5 = function (file, chunkIndex, chunkCount, mduId, uploadToken, chunkStart, chunkEnd) {
        var fileReader = new FileReader(),
            spark = new SparkMD5(),
            fileChunk = blobSlice.call(file, chunkStart, chunkEnd);
        fileReader.onloadend = function (e) {
            if (e.target.readyState == FileReader.DONE) { // DONE == 2
                console.log(e)
                // console.log("read chunk nr", currentChunk + 1, "of", chunks);
                spark.appendBinary(e.target.result);
                // append binary string
                let md5 = spark.end();
                console.log({
                    "chunkIndex": chunkIndex,
                    "chunkStart": chunkStart,
                    "chunkEnd": chunkEnd,
                    "chunkLength": (chunkEnd - chunkStart)
                });
                uploadChunk(uploadToken, mduId, chunkIndex, chunkCount, chunkEnd - chunkStart, md5, fileChunk)
            }
        };

        fileReader.readAsBinaryString(fileChunk);
    };



    $("#single-file-input").change(function () {
            let files = this.files;
            if (files.length != 1) {
                return false;
            }
            let file = files[0];
            let fileSize = file.size;
            if (fileSize > 1024 * 1024) {
                $("#single-file-input").val("");
                alert('上传文件过大')
                return false;
            }
            console.log("typeof file:" + typeof file);
            console.log("file size:" + fileSize);
            calculateFileMd5(file, $("#single-file-digest"));
            $("#single-file-name").val(file.name);
            $("#single-file-length").val(file.size);
            $("#single-file-content-type").val(file.type);

        }
    );
    $("#simple-upload-submit").click(
        function () {
            let fd = new FormData();

            let file = $("#single-file-input")[0].files[0];
            let length = $("#single-file-length").val();

            fd.append('file-name', $("#single-file-name").val());
            fd.append('file-content-type', $("#single-file-content-type").val());
            fd.append('file-digest', $("#single-file-digest").val());
            fd.append('file-length', length);
            fd.append('file-part', file);
            $.ajax(
                {
                    url: "/api/com.mijack.minipan.controller.storage/uploadFileEntity",
                    type: 'POST',
                    headers: {
                        "token": Cookies.get('restfulToken')
                    },
                    processData: false,
                    contentType: false,
                    data: fd
                }).done(function () {
                alert("上传成功");
            });
            return false;
        }
    );

    $("#chunk-file-input").change(function () {
            let files = this.files;
            if (files.length != 1) {
                return false;
            }
            let file = files[0];
            let fileSize = file.size;

            console.log("file:" + file);
            console.log("file size:" + fileSize);
            calculateFileMd5(file, $("#chunk-file-digest"));
            $("#chunk-file-name").val(file.name);
            $("#chunk-file-length").val(file.size);
            $("#chunk-file-content-type").val(file.type);
        }
    );
    $("#chunk-upload-submit").click(function () {
        // 申请上传的token

        $.ajax(
            {
                url: "/api/com.mijack.minipan.controller.storage/applyUploadToken",
                type: 'POST',

                headers: {
                    "token": Cookies.get('restfulToken')
                },
                data: {
                'file-name': $("#chunk-file-name").val(),
                'file-length': $("#chunk-file-length").val(),
                'file-content-type': $("#chunk-file-content-type").val(),
                'file-digest': $("#chunk-file-digest").val()
            },
            function (data) {
                console.log(data);

                var upload = function (file, chunkIndex, chunkCount, mduId, uploadToken) {

                    let chunkStart = chunkIndex * chunkSizeLimit;
                    let chunkEnd = Math.min((chunkIndex + 1) * chunkSizeLimit, file.size);
                    calculateFileChunkMd5(file, chunkIndex, chunkCount, mduId, uploadToken, chunkStart, chunkEnd);
                };


                const uploadToken = data['uploadToken'];
                let mduId = data['minDisplayUnitId'];
                let file = $("#chunk-file-input")[0].files[0];
                const chunkCount = Math.ceil(file.size / chunkSizeLimit);

                console.log("minDisplayUnitId:" + mduId)
                console.log("uploadToken:" + uploadToken)
                console.log("chunkSizeLimit:" + chunkSizeLimit)
                console.log('chunkCount:' + chunkCount)
                for (let chunkIndex = 0; chunkIndex < chunkCount; chunkIndex++) {
                    upload(file, chunkIndex, chunkCount, mduId, uploadToken)
                }

            }
            });
        return false;
    });
});