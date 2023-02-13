package numble.bankingapi.banking.ui;

import java.util.List;

public record HistoryResponses(
	List<HistoryResponse> historyResponses
) {

	@Override
	public List<HistoryResponse> historyResponses() {
		return List.copyOf(historyResponses);
	}
}
