package com.example.plogrid.domain.trash.service.analysis;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.example.plogrid.domain.plogging.entity.Plogging;
import com.example.plogrid.domain.trash.dto.TrashRequestDTO;
import com.example.plogrid.global.s3.S3Uploader;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class TrashAnalysisRequestProducer {

	public static final String STREAM_KEY = "trash-analysis-requests";

	private final S3Uploader s3Uploader;
	private final StringRedisTemplate stringRedisTemplate;

	public void publish(TrashRequestDTO.WasteClassification request, Plogging plogging) {
		String imageUrl = s3Uploader.uploadTrashImage(request.getImage());

		Map<String, String> fields = Map.of(
			"ploggingId", String.valueOf(plogging.getId()),
			"imageUrl", imageUrl,
			"latitude", String.valueOf(request.getLatitude()),
			"longitude", String.valueOf(request.getLongitude()),
			"spectralValues", buildSpectralValues(request)
		);

		stringRedisTemplate.opsForStream().add(MapRecord.create(STREAM_KEY, fields));
	}

	private String buildSpectralValues(TrashRequestDTO.WasteClassification request) {
		return Stream.of(
			request.getA(), request.getB(), request.getC(), request.getD(),
			request.getE(), request.getF(), request.getG(), request.getH(),
			request.getI(), request.getJ(), request.getK(), request.getL(),
			request.getR(), request.getS(), request.getT(), request.getU(),
			request.getV(), request.getW()
		).map(String::valueOf).collect(Collectors.joining(","));
	}
}
