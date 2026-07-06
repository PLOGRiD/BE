package com.example.plogrid.domain.member.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.plogrid.domain.member.dto.MemberResponseDTO;
import com.example.plogrid.domain.member.entity.Member;
import com.example.plogrid.domain.member.repository.MemberRepository;
import com.example.plogrid.global.apiPayload.code.MemberErrorCode;
import com.example.plogrid.global.apiPayload.exception.GeneralException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberQueryService {

	private final MemberRepository memberRepository;

	public MemberResponseDTO.MemberProfileDTO getMyProfile(Long memberId) {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new GeneralException(MemberErrorCode.MEMBER_NOT_FOUND));

		return MemberResponseDTO.MemberProfileDTO.builder()
			.nickName(member.getNickname())
			.email(member.getEmail())
			.build();
	}
}
