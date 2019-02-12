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

import com.mijack.panserver.exception.UnsupportedMimeTypeFoundException;
import com.mijack.panserver.service.TikaService;
import org.apache.tika.Tika;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Mi&Jack
 */
@Component
public class TikaServiceImpl implements TikaService {
    private Tika tika = new Tika();

    @Override
    public String extractMimeType(InputStream inputStream) {
        try {
            String detect = tika.detect(inputStream);
            return detect;
        } catch (IOException e) {
            e.printStackTrace();
            throw new UnsupportedMimeTypeFoundException();
        }
    }
}
