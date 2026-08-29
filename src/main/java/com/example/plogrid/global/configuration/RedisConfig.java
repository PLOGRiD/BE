package com.example.plogrid.global.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

import com.example.plogrid.global.sse.SseRedisSubscriber;
import com.example.plogrid.global.sse.SseService;

@Configuration
public class RedisConfig {

	@Value("${spring.data.redis.host}")
	private String redisHost;

	@Value("${spring.data.redis.port}")
	private int redisPort;

	@Bean
	public RedisConnectionFactory redisConnectionFactory() {
		return new LettuceConnectionFactory(redisHost, redisPort);
	}

	@Bean
	public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory factory) {
		return new StringRedisTemplate(factory);
	}

	@Bean
	public RedisMessageListenerContainer redisMessageListenerContainer(
		RedisConnectionFactory connectionFactory, SseRedisSubscriber sseRedisSubscriber) {

		RedisMessageListenerContainer container = new RedisMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.addMessageListener(sseRedisSubscriber, sseTopic());
		return container;
	}

	@Bean
	public ChannelTopic sseTopic() {
		return new ChannelTopic(SseService.SSE_CHANNEL);
	}
}
