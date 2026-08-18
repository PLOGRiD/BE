package com.example.plogrid.domain.plogging.service;

import java.time.Duration;
import java.util.Map;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.example.plogrid.domain.plogging.dto.PloggingResponseDTO;
import com.example.plogrid.domain.plogging.entity.Plogging;
import com.example.plogrid.domain.plogging.entity.enums.PloggingStatus;
import com.example.plogrid.domain.plogging.repository.PloggingRepository;
import com.example.plogrid.global.apiPayload.code.PloggingErrorCode;
import com.example.plogrid.global.apiPayload.exception.GeneralException;
import com.example.plogrid.global.sse.SseService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PloggingLocationService {

	private static final String LOCATION_KEY_PREFIX = "plogging:location:";
	private static final String FIELD_LATITUDE = "latitude";
	private static final String FIELD_LONGITUDE = "longitude";
	private static final String FIELD_DISTANCE = "distance";
	private static final double EARTH_RADIUS_METERS = 6371000;
	private static final Duration LOCATION_TTL = Duration.ofHours(3);

	private final StringRedisTemplate redisTemplate;
	private final PloggingRepository ploggingRepository;
	private final SseService sseService;

	public void updateLocation(Long memberId, double latitude, double longitude) {
		Plogging plogging = ploggingRepository.findByMemberIdAndStatus(memberId, PloggingStatus.IN_PROGRESS)
			.orElseThrow(() -> new GeneralException(PloggingErrorCode.PLOGGING_NOT_IN_PROGRESS));

		String key = locationKey(plogging.getId());
		Map<Object, Object> cached = redisTemplate.opsForHash().entries(key);

		double distance = cached.isEmpty() ? 0.0 : Double.parseDouble((String) cached.get(FIELD_DISTANCE));
		if (!cached.isEmpty()) {
			double lastLatitude = Double.parseDouble((String) cached.get(FIELD_LATITUDE));
			double lastLongitude = Double.parseDouble((String) cached.get(FIELD_LONGITUDE));
			distance += calculateDistanceMeters(lastLatitude, lastLongitude, latitude, longitude);
		}

		redisTemplate.opsForHash().putAll(key, Map.of(
			FIELD_LATITUDE, String.valueOf(latitude),
			FIELD_LONGITUDE, String.valueOf(longitude),
			FIELD_DISTANCE, String.valueOf(distance)
		));
		redisTemplate.expire(key, LOCATION_TTL);

		sseService.send(memberId, "plogging-distance-updated",
			PloggingResponseDTO.DistanceUpdatedResponseDTO.builder()
				.distanceMeters(distance)
				.build());
	}

	public double getAccumulatedDistance(Long ploggingId) {
		Object distance = redisTemplate.opsForHash().get(locationKey(ploggingId), FIELD_DISTANCE);
		return distance == null ? 0.0 : Double.parseDouble((String) distance);
	}

	public void clear(Long ploggingId) {
		redisTemplate.delete(locationKey(ploggingId));
	}

	private String locationKey(Long ploggingId) {
		return LOCATION_KEY_PREFIX + ploggingId;
	}

	private double calculateDistanceMeters(double lat1, double lon1, double lat2, double lon2) {
		double dLat = Math.toRadians(lat2 - lat1);
		double dLon = Math.toRadians(lon2 - lon1);
		double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
			+ Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
			* Math.sin(dLon / 2) * Math.sin(dLon / 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		return EARTH_RADIUS_METERS * c;
	}
}
