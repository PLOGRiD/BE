package com.example.plogrid.domain.member.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.plogrid.domain.member.dto.MemberRequestDTO;
import com.example.plogrid.domain.member.dto.MemberResponseDTO;
import com.example.plogrid.domain.member.entity.Member;
import com.example.plogrid.domain.member.entity.MemberStatistics;
import com.example.plogrid.domain.member.repository.MemberRepository;
import com.example.plogrid.domain.member.repository.MemberStatisticsRepository;
import com.example.plogrid.global.apiPayload.code.MemberErrorCode;
import com.example.plogrid.global.apiPayload.exception.GeneralException;
import com.example.plogrid.global.security.jwt.JwtTokenProvider;
import com.example.plogrid.global.security.jwt.TokenRole;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberAuthService {

	private final MemberRepository memberRepository;
	private final MemberStatisticsRepository memberStatisticsRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtTokenProvider jwtTokenProvider;

	public void signUp(MemberRequestDTO.SignUp request) {
		if (!request.getPassword().equals(request.getPasswordConfirm())) {
			throw new GeneralException(MemberErrorCode.PASSWORD_MISMATCH);
		}
		if (memberRepository.existsByUsername(request.getUsername())) {
			throw new GeneralException(MemberErrorCode.DUPLICATE_USERNAME);
		}
		if (memberRepository.existsByEmail(request.getEmail())) {
			throw new GeneralException(MemberErrorCode.DUPLICATE_EMAIL);
		}
		Member member = memberRepository.save(Member.create(
			request.getUsername(),
			passwordEncoder.encode(request.getPassword()),
			request.getEmail(),
			request.getUsername()
		));
		memberStatisticsRepository.save(MemberStatistics.create(member));
	}

	public MemberResponseDTO.Token signIn(MemberRequestDTO.SignIn request) {
		Member member = memberRepository.findByUsername(request.getUsername())
			.orElseThrow(() -> new GeneralException(MemberErrorCode.MEMBER_NOT_FOUND));

		if (!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
			throw new GeneralException(MemberErrorCode.INVALID_PASSWORD);
		}

		String accessToken = jwtTokenProvider.createAccessToken(member.getId(), member.getUsername(), TokenRole.MEMBER);
		String refreshToken = jwtTokenProvider.createRefreshToken(member.getId(), member.getUsername(), TokenRole.MEMBER);
		jwtTokenProvider.saveRefreshToken(member.getId(), TokenRole.MEMBER, refreshToken);

		return MemberResponseDTO.Token.builder()
			.accessToken(accessToken)
			.refreshToken(refreshToken)
			.build();
	}

	public MemberResponseDTO.Token reissue(MemberRequestDTO.Reissue request) {
		String refreshToken = request.getRefreshToken();

		if (!jwtTokenProvider.validateToken(refreshToken)) {
			throw new GeneralException(MemberErrorCode.INVALID_REFRESH_TOKEN);
		}

		Long memberId = jwtTokenProvider.getMemberIdFromToken(refreshToken);
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new GeneralException(MemberErrorCode.MEMBER_NOT_FOUND));

		if (!refreshToken.equals(jwtTokenProvider.getRefreshToken(memberId, TokenRole.MEMBER))) {
			throw new GeneralException(MemberErrorCode.INVALID_REFRESH_TOKEN);
		}

		String newAccessToken = jwtTokenProvider.createAccessToken(member.getId(), member.getUsername(), TokenRole.MEMBER);
		String newRefreshToken = jwtTokenProvider.createRefreshToken(member.getId(), member.getUsername(), TokenRole.MEMBER);
		jwtTokenProvider.saveRefreshToken(member.getId(), TokenRole.MEMBER, newRefreshToken);

		return MemberResponseDTO.Token.builder()
			.accessToken(newAccessToken)
			.refreshToken(newRefreshToken)
			.build();
	}

	public void signOut(Long memberId, String accessToken) {
		jwtTokenProvider.deleteRefreshToken(memberId, TokenRole.MEMBER);
		jwtTokenProvider.blacklistToken(accessToken);
	}

	public void withdraw(Long memberId, String accessToken) {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new GeneralException(MemberErrorCode.MEMBER_NOT_FOUND));
		jwtTokenProvider.blacklistToken(accessToken);
		jwtTokenProvider.deleteRefreshToken(memberId, TokenRole.MEMBER);
		member.delete();
	}
}
