package com.example.plogrid.domain.recruitment.service;

import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.plogrid.domain.recruitment.converter.RecruitmentConverter;
import com.example.plogrid.domain.recruitment.dto.RecruitmentResponseDTO;
import com.example.plogrid.domain.recruitment.entity.Recruitment;
import com.example.plogrid.domain.recruitment.repository.RecruitmentParticipantRepository;
import com.example.plogrid.domain.recruitment.repository.RecruitmentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecruitmentQueryService {

	private final RecruitmentRepository recruitmentRepository;
	private final RecruitmentParticipantRepository recruitmentParticipantRepository;

	public RecruitmentResponseDTO.RecruitmentListResponseDTO getRecruitmentList(Integer page, Integer size) {
		Page<Recruitment> recruitments = recruitmentRepository.findAllByOrderByCreatedAtDesc(
			PageRequest.of(page - 1, size));

		Map<Long, Integer> currentParticipantCounts = recruitments.getContent().stream()
			.collect(Collectors.toMap(Recruitment::getId,
				recruitment -> recruitmentParticipantRepository.countByRecruitmentId(recruitment.getId())));

		return RecruitmentConverter.toRecruitmentListResponseDTO(recruitments, currentParticipantCounts);
	}
}
