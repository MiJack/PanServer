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

package com.mijack.messagepattern;

/**
 * @author Mi&Jack
 */
public class SubMessage {
    private String content;
    private String name;
    private boolean isRawString;
    private int index = -1;

    public SubMessage(String content, String name, boolean isRawString, int index) {
        this.content = content;
        this.name = name;
        this.isRawString = isRawString;
        this.index = index;
    }

    public String getContent() {
        return content;
    }

    public String getName() {
        return name;
    }

    public boolean isRawString() {
        return isRawString;
    }

    public int getIndex() {
        return index;
    }

    @Override
    public String toString() {
        if (isRawString) {
            return "[" + content + "]";
        } else {
            boolean useName = name != null && name.length() > 0;
            boolean useIndex = index >= 0;
            return "{" + (useName ? ("name='" + name + '\'') : "") + (useIndex && useName ? "," : "")
                    + (useIndex ? ("index='" + index + '\'') : "") + '}';
        }
    }
}