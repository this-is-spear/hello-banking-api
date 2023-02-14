package numble.bankingapi.member.domain;

import java.time.LocalDateTime;
import java.util.Objects;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Member {
	@EqualsAndHashCode.Include
	private Long userId;
	private String id;
	private String name;
	private String password;
	private LocalDateTime createdDate;

	@Builder
	public Member(String id, String name, String password, LocalDateTime createdDate) {
		requiredNotNullAndNotBlank(id);
		requiredNotNullAndNotBlank(name);
		requiredNotNullAndNotBlank(password);
		this.id = id;
		this.name = name;
		this.password = password;
		this.createdDate = createdDate == null ? LocalDateTime.now() : createdDate;
	}

	private void requiredNotNullAndNotBlank(String id) {
		if (Objects.isNull(id) || id.isBlank()) {
			throw new NullPointerException();
		}
	}
}
