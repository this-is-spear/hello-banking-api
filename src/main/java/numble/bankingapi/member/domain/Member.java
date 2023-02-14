package numble.bankingapi.member.domain;

import java.time.LocalDateTime;
import java.util.Objects;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import numble.bankingapi.common.BaseEntity;

@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class Member extends BaseEntity {
	@EqualsAndHashCode.Include
	private Long userId;
	private String email;
	private String name;
	private String password;

	@Builder
	public Member(String email, String name, String password) {
		requiredNotNullAndNotBlank(email);
		requiredNotNullAndNotBlank(name);
		requiredNotNullAndNotBlank(password);
		this.email = email;
		this.name = name;
		this.password = password;
	}

	private void requiredNotNullAndNotBlank(String field) {
		if (Objects.isNull(field) || field.isBlank()) {
			throw new NullPointerException();
		}
	}
}
