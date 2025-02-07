package com.example.travelshooting.config;

import com.example.travelshooting.common.Const;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        // 기본 쓰레드 개수로 처리하다가 작업량을 넘으면 큐에서 대기하고, 큐 크기를 초과하면 쓰레드를 최대로 생성
        executor.setCorePoolSize(Const.THREAD_CORE_POOL_SIZE); // 쓰레드풀에서 사용할 쓰레드의 기본 개수
        executor.setMaxPoolSize(Const.THREAD_MAX_POOL_SIZE);
        executor.setQueueCapacity(Const.THREAD_QUEUE_CAPACITY); // 이벤트 대기 큐 크기
        executor.setThreadNamePrefix(Const.THREAD_NAME_PREFIX);
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();

        return executor;
    }
}
