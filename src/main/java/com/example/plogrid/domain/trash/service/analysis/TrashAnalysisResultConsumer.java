package com.example.plogrid.domain.trash.service.analysis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class TrashAnalysisResultConsumer implements StreamListener<String, MapRecord<String, String, String>> {

	public static final String STREAM_KEY = "trash-analysis-results";
	public static final String GROUP = "trash-result-workers";

	private final TrashAnalysisResultService trashAnalysisResultService;
	private final StringRedisTemplate stringRedisTemplate;

	@Getter
	@Value("${trash.analysis.consumer-name}")
	private String consumerName;

	@Override
	public void onMessage(MapRecord<String, String, String> record) {
		try {
			trashAnalysisResultService.save(record.getValue());
			stringRedisTemplate.opsForStream().acknowledge(STREAM_KEY, GROUP, record.getId());
		} catch (Exception e) {
			log.error("쓰레기 분석 결과 처리 실패 - recordId: {}, fields: {} (ACK 보류, PEL에서 재처리 필요)",
				record.getId(), record.getValue(), e);
		}
	}
}
