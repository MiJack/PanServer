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

package com.mijack.panserver.component;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Mi&Jack
 */

public class DisruptorService<T> implements BeanNameAware, InitializingBean, ThreadFactory,
        EventFactory<DisruptorService.Element<T>> {

    private final Disruptor disruptor;
    private String name;
    private ElementEventHandler<T> elementEventHandler = new ElementEventHandler<>();

    public DisruptorService(EventHandler<T> handler) {
        // 阻塞策略
        BlockingWaitStrategy strategy = new BlockingWaitStrategy();

        // 指定RingBuffer的大小
        int bufferSize = 16;

        // 创建disruptor，采用单生产者模式
        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder()
                .setNameFormat("DisruptorService-" + name + "-%d").build();


        ExecutorService pool = new ThreadPoolExecutor(5, 200,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(1024), namedThreadFactory, new ThreadPoolExecutor.AbortPolicy());


        disruptor = new Disruptor(this, bufferSize, pool, ProducerType.MULTI, strategy);
        elementEventHandler.setEventHandler(handler);
        // 设置EventHandler
        disruptor.handleEventsWith(elementEventHandler);
    }

    public void publish(T ele) {
        RingBuffer<Element<T>> ringBuffer = disruptor.getRingBuffer();
        // 获取下一个可用位置的下标
        long sequence = ringBuffer.next();
        try {
            // 返回可用位置的元素
            Element<T> event = ringBuffer.get(sequence);
            // 设置该位置元素的值
            event.setElement(ele);
        } finally {
            ringBuffer.publish(sequence);
        }
    }

    @Override
    public void afterPropertiesSet() {
        disruptor.start();
    }

    @Override
    public Thread newThread(@NotNull Runnable r) {
        Thread thread = new Thread(r);
        String threadName = "DisruptorService[" + name + "-" + "]";
        thread.setName(threadName);
        return thread;
    }

    @Override
    public Element<T> newInstance() {
        return new Element<>();
    }

    @Override
    public void setBeanName(String name) {
        this.name = name;
    }

    public static class Element<T> {
        private T element;

        public T getElement() {
            return element;
        }

        public void setElement(T element) {
            this.element = element;
        }
    }

    public static class ElementEventHandler<T> implements EventHandler<Element<T>> {
        private EventHandler<T> eventHandler;

        public void setEventHandler(EventHandler<T> eventHandler) {
            this.eventHandler = eventHandler;
        }

        @Override
        public void onEvent(Element<T> event, long sequence, boolean endOfBatch) throws Exception {
            T element = event.getElement();
            if (eventHandler != null) {
                eventHandler.onEvent(element, sequence, endOfBatch);
            }
        }
    }
}
