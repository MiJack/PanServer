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

package com.mijack.panserver.event;

import org.springframework.context.ApplicationEvent;

/**
 * @author Mi&Jack
 */
public class WebInitEvent extends ApplicationEvent {
    /**
     * Create a new ApplicationEvent.
     */
    public WebInitEvent(WebStatus webStatus) {
        super(webStatus);
    }

    public WebStatus getWebStatus() {
        return (WebStatus) getSource();
    }

    public enum WebStatus {
        /**
         * 网站未初始化完毕
         */
        UNINITIALIZED,
        /**
         * 网站初始化完毕
         */
        INITIALIZED,
        /**
         * 网站初始化失败
         */
        INITIALIZATION_FAILED;
    }
}
