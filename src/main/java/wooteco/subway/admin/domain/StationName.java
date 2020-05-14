package wooteco.subway.admin.domain;

import java.util.Objects;

public class StationName {
	private static final String BLANK = " ";

	private String name;

	public StationName(String name) {
		validateNull(name);
		validateEmpty(name);
		validateBlank(name);
		validateNumber(name);
		this.name = name;
	}

	private void validateNumber(String name) {
		for (char c : name.toCharArray()) {
			if (Character.isDigit(c)) {
				throw new IllegalArgumentException("숫자 없이 입력해주세요.");
			}
		}
	}

	private void validateBlank(String name) {
		if (name.contains(BLANK)) {
			throw new IllegalArgumentException("공백 없이 입력해주세요.");
		}
	}

	private void validateEmpty(String name) {
		if (name.isEmpty()) {
			throw new IllegalArgumentException("값을 입력해주세요.");
		}
	}

	private void validateNull(String name) {
		if (Objects.isNull(name)) {
			throw new IllegalArgumentException("값을 입력해주세요.");
		}
	}

	public String getName() {
		return name;
	}
}
