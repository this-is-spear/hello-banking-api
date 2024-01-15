package bankingapi.idempotent.dto;

/**
 * https://datatracker.ietf.org/doc/draft-ietf-httpapi-idempotency-key-header/에서 참고했습니다.
 *
 * @param type
 * @param title
 * @param detail
 */
public record ExceptionMessage(
	String type,
	String title,
	String detail
) {
}
