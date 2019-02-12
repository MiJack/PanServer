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

package com.mijack.panserver.util;

import java.io.IOException;
import java.io.InputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author Mi&Jack
 */

public class DigestHelper {
    public static DigestInputStream toDigestInputStream(InputStream inputStream, String digestAlgorithm) throws NoSuchAlgorithmException {
        MessageDigest algorithm = MessageDigest.getInstance(digestAlgorithm);
        DigestInputStream dis = new DigestInputStream(inputStream, algorithm);
        return dis;
    }

    public static String toDigest(InputStream inputStream, String digestAlgorithm) throws NoSuchAlgorithmException, IOException {
        MessageDigest algorithm = MessageDigest.getInstance(digestAlgorithm);
        DigestInputStream dis = new DigestInputStream(inputStream, algorithm);
        while (dis.read() != -1) {
        }
        return StringHelper.getMessageDigest(dis);
    }
}
