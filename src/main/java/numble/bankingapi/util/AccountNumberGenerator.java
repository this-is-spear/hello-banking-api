package numble.bankingapi.util;

import java.util.List;
import java.util.random.RandomGenerator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import numble.bankingapi.banking.domain.AccountNumber;

public class AccountNumberGenerator {
	private static final String DASH = "-";
	private static final List<Integer> CACHED_NUMBERS = IntStream.range(0, 9).boxed().toList();
	private static final int STREAM_SIZE = 13;
	private static final int RANDOM_NUMBER_ORIGIN = 0;
	private static final int RANDOM_NUMBER_BOUND = 10;

	public static AccountNumber generate() {
		RandomGenerator generator = RandomGenerator.getDefault();
		List<String> list = generator.ints(STREAM_SIZE, RANDOM_NUMBER_ORIGIN, RANDOM_NUMBER_BOUND).boxed()
			.map(String::valueOf).collect(Collectors.toList());
		list.add(3, DASH);
		list.add(8, DASH);
		return new AccountNumber(list.stream().reduce((s1, s2) -> s1 + s2).get());
	}
}
