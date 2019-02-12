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
public interface MailService {
    /**
     * 发送纯文本邮件
     *
     * @param toAddr  发送给谁
     * @param title   标题
     * @param content 内容
     */
    void sendTextMail(String toAddr, String title, String content);

    /**
     * 发送 html 邮件
     *
     * @param toAddr
     * @param title
     * @param content 内容（HTML）
     */
    void sendHtmlMail(String toAddr, String title, String content);

    /**
     * 发送待附件的邮件
     *
     * @param toAddr
     * @param title
     * @param content
     * @param filePath 附件地址
     */
    void sendAttachmentsMail(String toAddr, String title, String content, String filePath);

    /**
     * 发送文本中有静态资源（图片）的邮件
     *
     * @param toAddr
     * @param title
     * @param content
     * @param rscPath 资源路径
     * @param rscId   资源id (可能有多个图片)
     */
    void sendInlineResourceMail(String toAddr, String title, String content, String rscPath, String rscId);

}

