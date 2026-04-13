package com.tianji.learning.utils;

import lombok.Data;

import javax.validation.constraints.Max;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * 延迟任务封装类
 * <p>
 * 实现 {@link Delayed} 接口，用于配合 {@link java.util.concurrent.DelayQueue} 使用。
 * 该类的实例只有在指定的延迟时间到期后，才能从延迟队列中被取出执行。
 *
 * @param <D> 任务携带的数据类型
 */
@Data
public class DelayTask<D> implements Delayed {

    private D data;
    private long deadlineNanos;

    public DelayTask(D data, long delay, TimeUnit unit) {
        this.data = data;
        this.deadlineNanos = System.nanoTime() + unit.toNanos(delay);
    }
    @Override
    public long getDelay(TimeUnit unit) {
        return unit.convert(Math.max(0,deadlineNanos - System.nanoTime()), TimeUnit.NANOSECONDS);
    }

    @Override
    public int compareTo(Delayed o) {
        long l = getDelay(TimeUnit.NANOSECONDS) - o.getDelay(TimeUnit.NANOSECONDS);
        if (l > 0) {
            return 1;
        } else if (l < 0) {
            return -1;
        }else {
            return 0;
        }
    }
}
