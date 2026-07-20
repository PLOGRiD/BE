package com.example.plogrid.domain.device.service;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.plogrid.domain.device.dto.DeviceRequestDTO;
import com.example.plogrid.domain.device.dto.DeviceResponseDTO;
import com.example.plogrid.domain.device.entity.CollectionDevice;
import com.example.plogrid.domain.device.entity.MemberCollectionDevice;
import com.example.plogrid.domain.device.repository.MemberDeviceRepository;
import com.example.plogrid.global.apiPayload.code.DeviceErrorCode;
import com.example.plogrid.global.apiPayload.exception.GeneralException;
import com.example.plogrid.global.security.jwt.JwtTokenProvider;
import com.example.plogrid.global.security.jwt.TokenRole;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class DeviceQueryService {

	public final MemberDeviceRepository memberDeviceRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtTokenProvider jwtTokenProvider;

	public DeviceResponseDTO.TokenDTO issueToken(DeviceRequestDTO.TokenRequest request) {

		Optional<MemberCollectionDevice> memberCollectionDevice = memberDeviceRepository.findByCollectionDeviceId(request.getDeviceId());

		if(memberCollectionDevice.isEmpty()) {
			throw new GeneralException(DeviceErrorCode.DEVICE_NOT_LINKED);
		}

		CollectionDevice collectionDevice = memberCollectionDevice.get().getCollectionDevice();

		if (!passwordEncoder.matches(request.getSecretKey(), collectionDevice.getSecretKey())) {
			throw new GeneralException(DeviceErrorCode.INVALID_DEVICE_CREDENTIALS);
		}

		String subject = String.valueOf(collectionDevice.getId());
		String accessToken = jwtTokenProvider.createAccessToken(collectionDevice.getId(), subject, TokenRole.DEVICE);
		String refreshToken = jwtTokenProvider.createRefreshToken(collectionDevice.getId(), subject, TokenRole.DEVICE);
		jwtTokenProvider.saveRefreshToken(collectionDevice.getId(), TokenRole.DEVICE, refreshToken);

		return DeviceResponseDTO.TokenDTO.builder()
			.deviceToken(accessToken)
			.refreshToken(refreshToken)
			.build();
	}

	public DeviceResponseDTO.TokenDTO reissue(DeviceRequestDTO.Reissue request) {
		String refreshToken = request.getRefreshToken();

		if (!jwtTokenProvider.validateToken(refreshToken)) {
			throw new GeneralException(DeviceErrorCode.INVALID_REFRESH_TOKEN);
		}

		Long deviceId = jwtTokenProvider.getMemberIdFromToken(refreshToken);

		if (!refreshToken.equals(jwtTokenProvider.getRefreshToken(deviceId, TokenRole.DEVICE))) {
			throw new GeneralException(DeviceErrorCode.INVALID_REFRESH_TOKEN);
		}

		String subject = String.valueOf(deviceId);
		String newAccessToken = jwtTokenProvider.createAccessToken(deviceId, subject, TokenRole.DEVICE);
		String newRefreshToken = jwtTokenProvider.createRefreshToken(deviceId, subject, TokenRole.DEVICE);
		jwtTokenProvider.saveRefreshToken(deviceId, TokenRole.DEVICE, newRefreshToken);

		return DeviceResponseDTO.TokenDTO.builder()
			.deviceToken(newAccessToken)
			.refreshToken(newRefreshToken)
			.build();
	}
}