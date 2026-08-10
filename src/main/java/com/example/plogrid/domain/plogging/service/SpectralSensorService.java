package com.example.plogrid.domain.plogging.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.plogrid.domain.plogging.dto.SpectralRequestDTO;
import com.example.plogrid.domain.plogging.dto.SpectralResponseDTO;
import com.example.plogrid.domain.trash.dto.TrashRequestDTO;

@Service
public class SpectralSensorService {

	private final RestTemplate restTemplate = new RestTemplate();

	@Value("${spectral.url}")
	private String spectralUrl;

	public SpectralResponseDTO predict(TrashRequestDTO.WasteClassification request) {

		List<Double> values = List.of(
			request.getA(), request.getB(), request.getC(), request.getD(),
			request.getE(), request.getF(), request.getG(), request.getH(),
			request.getI(), request.getJ(), request.getK(), request.getL(),
			request.getR(), request.getS(), request.getT(), request.getU(),
			request.getV(), request.getW()
		);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<SpectralRequestDTO> requestEntity = new HttpEntity<>(new SpectralRequestDTO(values), headers);

		return restTemplate.postForObject(spectralUrl, requestEntity, SpectralResponseDTO.class);
	}
}
