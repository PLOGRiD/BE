package com.example.plogrid.global.s3;

import java.io.IOException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.example.plogrid.global.apiPayload.code.S3ErrorCode;
import com.example.plogrid.global.apiPayload.exception.GeneralException;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Component
@RequiredArgsConstructor
public class S3Uploader {

	private static final String CHAT_IMAGE_DIR = "chat-images";
	private static final String TRASH_IMAGE_DIR = "trash";

	private final S3Client s3Client;

	@Value("${cloud.aws.s3.bucket}")
	private String bucket;

	@Value("${cloud.aws.region}")
	private String region;

	public String uploadChatImage(MultipartFile file) {
		return upload(file, CHAT_IMAGE_DIR);
	}

	public String uploadTrashImage(MultipartFile file) {
		return upload(file, TRASH_IMAGE_DIR);
	}

	public void deleteImage(String imageUrl) {
		String key = extractKey(imageUrl);

		try {
			s3Client.deleteObject(
				DeleteObjectRequest.builder()
					.bucket(bucket)
					.key(key)
					.build()
			);
		} catch (S3Exception e) {
			throw new GeneralException(S3ErrorCode.S3_DELETE_FAILED);
		}
	}

	private String extractKey(String imageUrl) {
		String prefix = urlPrefix();
		if (imageUrl == null || !imageUrl.startsWith(prefix)) {
			throw new GeneralException(S3ErrorCode.S3_INVALID_URL);
		}
		return imageUrl.substring(prefix.length());
	}

	private String upload(MultipartFile file, String dirName) {
		if (file == null || file.isEmpty()) {
			throw new GeneralException(S3ErrorCode.S3_FILE_EMPTY);
		}

		String key = dirName + "/" + UUID.randomUUID() + extractExtension(file.getOriginalFilename());

		try {
			s3Client.putObject(
				PutObjectRequest.builder()
					.bucket(bucket)
					.key(key)
					.contentType(file.getContentType())
					.build(),
				RequestBody.fromInputStream(file.getInputStream(), file.getSize())
			);
		} catch (IOException e) {
			throw new GeneralException(S3ErrorCode.S3_UPLOAD_FAILED);
		}

		return urlPrefix() + key;
	}

	private String urlPrefix() {
		return "https://%s.s3.%s.amazonaws.com/".formatted(bucket, region);
	}

	private String extractExtension(String filename) {
		if (filename == null) {
			return "";
		}
		int dotIndex = filename.lastIndexOf('.');
		return dotIndex == -1 ? "" : filename.substring(dotIndex);
	}
}
