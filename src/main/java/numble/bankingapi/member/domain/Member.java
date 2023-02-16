package numble.bankingapi.member.domain;

import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import numble.bankingapi.common.BaseEntity;
import numble.bankingapi.member.exception.InvalidFormatException;

@Getter
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {

	// https://www.baeldung.com/java-email-validation-regex 를 참고했습니다.
	private static final Pattern EMAIL_FORMAT = Pattern.compile(
		"^(?=.{1,64}@)[A-Za-z\\d_-]+(\\.[A-Za-z\\d_-]+)*@"
			+ "[^-][A-Za-z\\d-]+(\\.[A-Za-z\\d-]+)*(\\.[A-Za-z]{2,})$"
	);

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@EqualsAndHashCode.Include
	private Long id;
	@Column(unique = true, nullable = false)
	private String email;
	@Column(nullable = false)
	private String name;
	@Column(nullable = false)
	private String password;
	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(
		name = "memberRole",
		joinColumns = @JoinColumn(name = "id", referencedColumnName = "id")
	)
	@Column(name = "role")
	private List<String> roles;

	@Builder
	public Member(String email, String name, String password) {
		ensureEmail(email);
		ensurePassword(password);
		ensureName(name);
		this.email = email;
		this.name = name;
		this.password = password;
		this.roles = List.of(RoleType.ROLE_MEMBER.name());
	}

	private void ensureEmail(String email) {
		if (Objects.isNull(email) || email.isBlank()) {
			throw InvalidFormatException.emptyEmail();
		}
		if (!EMAIL_FORMAT.matcher(email).matches()) {
			throw InvalidFormatException.invalidEmail();
		}
	}

	private void ensurePassword(String password) {
		if (Objects.isNull(password) || password.isBlank()) {
			throw InvalidFormatException.emptyPassword();
		}

		if (password.length() < 8 || password.length() > 20) {
			throw InvalidFormatException.invalidPassword();
		}
	}

	private void ensureName(String name) {
		if (Objects.isNull(name) || name.isBlank()) {
			throw InvalidFormatException.emptyName();
		}
	}
}
