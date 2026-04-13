package com.tianji.learning.utils;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.concurrent.DelayQueue;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class DelayTaskTest {

    @Test
    void testDelayTask() throws InterruptedException {
        // 1.初始化延迟队列
        DelayQueue<DelayTask<String>> queue = new DelayQueue<>();
        // 2.向队列中添加延迟执行的任务
        log.info("开始初始化延迟任务。。。。");
        // 修复：匹配现有构造函数 (data, delay, unit)
        queue.add(new DelayTask<>("延迟任务 3", 3, TimeUnit.SECONDS));
        queue.add(new DelayTask<>("延迟任务 1", 1, TimeUnit.SECONDS));
        queue.add(new DelayTask<>("延迟任务 2", 2, TimeUnit.SECONDS));

        // 3.尝试执行任务 (修复：避免无限阻塞)
        // 共添加了 3 个任务，循环 3 次即可
        for (int i = 0; i < 3; i++) {
            DelayTask<String> task = queue.take();
            log.info("延迟任务执行完毕：{}", task.getData());
        }
    }
}
