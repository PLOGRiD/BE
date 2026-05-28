package com.example.plogrid.domain.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class MemberRequestDTO {

	@Schema(name = "회원가입 요청")
	@Getter
	@NoArgsConstructor
	public static class SignUp {

		@Schema(description = "아이디 (영문/숫자 6~10자, 띄어쓰기 불가)", example = "john123")
		@NotBlank(message = "아이디를 입력해주세요.")
		@Pattern(regexp = "^[a-zA-Z0-9]{6,10}$", message = "아이디는 띄어쓰기 없이 영문/숫자 6~10자여야 합니다.")
		private String username;

		@Schema(description = "비밀번호 (8~15자, 영문 + 숫자 또는 특수문자 조합)", example = "pass1234!")
		@NotBlank(message = "비밀번호를 입력해주세요.")
		@Pattern(
			regexp = "^(?=.*[A-Za-z])(?=.*[0-9!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?])[A-Za-z0-9!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]{8,15}$",
			message = "비밀번호는 8~15자의 영문, 숫자 또는 특수문자 조합이어야 합니다."
		)
		private String password;

		@Schema(description = "이메일", example = "john@example.com")
		@NotBlank(message = "이메일을 입력해주세요.")
		@Email(message = "올바른 이메일 형식이 아닙니다.")
		private String email;

		@Schema(description = "닉네임", example = "달리는존")
		@NotBlank(message = "닉네임을 입력해주세요.")
		private String nickname;
	}

	@Schema(name = "로그인 요청")
	@Getter
	@NoArgsConstructor
	public static class SignIn {

		@Schema(description = "아이디", example = "john123")
		@NotBlank(message = "아이디를 입력해주세요.")
		private String username;

		@Schema(description = "비밀번호", example = "pass1234!")
		@NotBlank(message = "비밀번호를 입력해주세요.")
		private String password;
	}

	@Schema(name = "토큰 재발급 요청")
	@Getter
	@NoArgsConstructor
	public static class Reissue {

		@Schema(description = "로그인 시 발급받은 Refresh Token", example = "eyJhbGciOiJIUzI1NiJ9...")
		@NotBlank(message = "Refresh Token을 입력해주세요.")
		private String refreshToken;
	}
}
