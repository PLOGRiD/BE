package com.example.plogrid.domain.trash.service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.example.plogrid.domain.plogging.dto.PredictResponseDTO;
import com.example.plogrid.domain.trash.dto.TrashRequestDTO;

@Service
public class WastePredictionService {

	private final RestTemplate restTemplate = new RestTemplate();

	@Value("${predict.url}")
	private String predictUrl;

	public PredictResponseDTO predict(TrashRequestDTO.WasteClassification request) throws IOException {

		MultipartFile file = request.getImage();

		HttpHeaders fileHeaders = new HttpHeaders();
		String contentType = file.getContentType();
		fileHeaders.setContentType(
			contentType != null ? MediaType.parseMediaType(contentType) : MediaType.IMAGE_PNG
		);

		ByteArrayResource resource = new ByteArrayResource(file.getBytes()) {
			@Override
			public String getFilename() {
				return file.getOriginalFilename();
			}
		};

		MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
		body.add("file", new HttpEntity<>(resource, fileHeaders));
		body.add("nir", buildNir(request));

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);
		headers.setAccept(List.of(MediaType.APPLICATION_JSON));

		HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

		return restTemplate.postForObject(predictUrl, requestEntity, PredictResponseDTO.class);
	}

	private String buildNir(TrashRequestDTO.WasteClassification request) {
		return Stream.of(
			request.getA(), request.getB(), request.getC(), request.getD(),
			request.getE(), request.getF(), request.getG(), request.getH(),
			request.getI(), request.getJ(), request.getK(), request.getL(),
			request.getR(), request.getS(), request.getT(), request.getU(),
			request.getV(), request.getW()
		).map(String::valueOf).collect(Collectors.joining(","));
	}
}
