package com.example.plogrid.domain.member.entity;

import java.time.LocalDateTime;

import com.example.plogrid.domain.common.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class Member extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true)
	private String username;

	@Column(nullable = false)
	private String password;

	@Column(nullable = false, unique = true)
	private String email;

	@Column(nullable = false)
	private String nickname;

	@Column
	private LocalDateTime deletedAt;

	public static Member create(String username, String password, String email, String nickname) {
		return Member.builder()
			.username(username)
			.password(password)
			.email(email)
			.nickname(nickname)
			.build();
	}

	public void delete() {
		this.username = "deleted_" + this.id;
		this.email = "deleted_" + this.id + "@deleted.invalid";
		this.password = "DELETED";
		this.nickname = "알 수 없음";
		this.deletedAt = LocalDateTime.now();
	}
}