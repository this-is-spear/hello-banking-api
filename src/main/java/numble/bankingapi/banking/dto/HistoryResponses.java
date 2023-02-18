package numble.bankingapi.banking.dto;

import java.util.List;

import numble.bankingapi.banking.domain.Money;

public record HistoryResponses(
	Money balance,
	List<HistoryResponse> historyResponses
) {

	@Override
	public List<HistoryResponse> historyResponses() {
		return List.copyOf(historyResponses);
	}
}
