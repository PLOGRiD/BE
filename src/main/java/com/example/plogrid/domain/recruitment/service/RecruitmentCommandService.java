package com.example.plogrid.domain.recruitment.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.plogrid.domain.member.entity.Member;
import com.example.plogrid.domain.member.repository.MemberRepository;
import com.example.plogrid.domain.recruitment.converter.RecruitmentConverter;
import com.example.plogrid.domain.recruitment.dto.RecruitmentResponseDTO;
import com.example.plogrid.domain.recruitment.entity.Recruitment;
import com.example.plogrid.domain.recruitment.entity.RecruitmentParticipant;
import com.example.plogrid.domain.recruitment.entity.enums.RecruitmentStatus;
import com.example.plogrid.domain.recruitment.repository.RecruitmentParticipantRepository;
import com.example.plogrid.domain.recruitment.repository.RecruitmentRepository;
import com.example.plogrid.global.apiPayload.code.MemberErrorCode;
import com.example.plogrid.global.apiPayload.code.RecruitmentErrorCode;
import com.example.plogrid.global.apiPayload.exception.GeneralException;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class RecruitmentCommandService {

	private final RecruitmentRepository recruitmentRepository;
	private final RecruitmentParticipantRepository recruitmentParticipantRepository;
	private final MemberRepository memberRepository;

	public RecruitmentResponseDTO.ParticipationToggleResponseDTO toggleParticipation(Long memberId,
		Long recruitmentId) {
		Recruitment recruitment = recruitmentRepository.findById(recruitmentId)
			.orElseThrow(() -> new GeneralException(RecruitmentErrorCode.RECRUITMENT_NOT_FOUND));

		if (recruitment.getStatus() == RecruitmentStatus.ENDED) {
			throw new GeneralException(RecruitmentErrorCode.RECRUITMENT_ENDED);
		}

		boolean isParticipating = recruitmentParticipantRepository.findByMemberIdAndRecruitmentId(memberId,
				recruitmentId)
			.map(participant -> {
				recruitmentParticipantRepository.delete(participant);
				recruitmentRepository.decreaseCurrentParticipants(recruitmentId);
				return false;
			})
			.orElseGet(() -> {
				if (recruitmentRepository.increaseCurrentParticipants(recruitmentId) == 0) {
					throw new GeneralException(RecruitmentErrorCode.RECRUITMENT_FULL);
				}

				Member member = memberRepository.findById(memberId)
					.orElseThrow(() -> new GeneralException(MemberErrorCode.MEMBER_NOT_FOUND));
				recruitmentParticipantRepository.save(RecruitmentParticipant.create(member, recruitment));
				return true;
			});

		int currentParticipants = recruitmentParticipantRepository.countByRecruitmentId(recruitmentId);

		return RecruitmentConverter.toParticipationToggleResponseDTO(isParticipating, currentParticipants,
			recruitment.getMaxParticipants());
	}
}
