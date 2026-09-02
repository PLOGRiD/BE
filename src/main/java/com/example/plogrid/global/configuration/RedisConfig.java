package com.example.plogrid.global.configuration;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.RedisSystemException;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;

import com.example.plogrid.domain.trash.service.analysis.TrashAnalysisResultConsumer;

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

	@Bean(initMethod = "start", destroyMethod = "stop")
	public StreamMessageListenerContainer<String, MapRecord<String, String, String>> trashAnalysisResultStreamContainer(
		RedisConnectionFactory connectionFactory, StringRedisTemplate stringRedisTemplate,
		TrashAnalysisResultConsumer trashAnalysisResultConsumer) {

		createConsumerGroupIfNotExists(stringRedisTemplate);

		StreamMessageListenerContainer.StreamMessageListenerContainerOptions<String, MapRecord<String, String, String>> options =
			StreamMessageListenerContainer.StreamMessageListenerContainerOptions.builder()
				.pollTimeout(Duration.ofSeconds(2))
				.build();

		StreamMessageListenerContainer<String, MapRecord<String, String, String>> container =
			StreamMessageListenerContainer.create(connectionFactory, options);

		container.receive(
			Consumer.from(TrashAnalysisResultConsumer.GROUP, trashAnalysisResultConsumer.getConsumerName()),
			StreamOffset.create(TrashAnalysisResultConsumer.STREAM_KEY, ReadOffset.lastConsumed()),
			trashAnalysisResultConsumer
		);

		return container;
	}

	private void createConsumerGroupIfNotExists(StringRedisTemplate stringRedisTemplate) {
		try {
			stringRedisTemplate.opsForStream()
				.createGroup(TrashAnalysisResultConsumer.STREAM_KEY, ReadOffset.from("0"), TrashAnalysisResultConsumer.GROUP);
		} catch (RedisSystemException e) {
			boolean alreadyExists = e.getCause() != null && e.getCause().getMessage() != null
				&& e.getCause().getMessage().contains("BUSYGROUP");
			if (!alreadyExists) {
				throw e;
			}
		}
	}
}
