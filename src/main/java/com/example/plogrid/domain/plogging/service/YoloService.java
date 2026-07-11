package com.example.plogrid.domain.plogging.service;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.example.plogrid.domain.plogging.dto.YoloResponseDTO;

@Service
public class YoloService {

	private final RestTemplate restTemplate = new RestTemplate();

	@Value("${yolo.url}")
	private String yoloUrl;

	public YoloResponseDTO predict(MultipartFile file) throws IOException {

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

		HttpEntity<ByteArrayResource> fileEntity = new HttpEntity<>(resource, fileHeaders);

		MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
		body.add("file", fileEntity);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);
		headers.setAccept(List.of(MediaType.APPLICATION_JSON));

		HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

		ResponseEntity<YoloResponseDTO> response = restTemplate.exchange(
			yoloUrl,
			HttpMethod.POST,
			requestEntity,
			YoloResponseDTO.class
		);

		return response.getBody();
	}
}
