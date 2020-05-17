package wooteco.subway.admin.domain;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

class StationNameTest {
	@DisplayName("null이나 empty String인 경우 예외 발생")
	@ParameterizedTest
	@NullAndEmptySource
	void validateNullAndEmpty(String str) {
		assertThatThrownBy(() -> new StationName(str))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("값을 입력해주세요.");
	}

	@DisplayName("공백이 포함된 경우 예외 발생")
	@Test
	void validateBlank() {
		assertThatThrownBy(() -> new StationName("a b"))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("공백 없이 입력해주세요.");
	}

	@DisplayName("숫자가 포함된 경우 예외 발생")
	@Test
	void validateNumber() {
		assertThatThrownBy(() -> new StationName("a1b"))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("숫자 없이 입력해주세요.");
	}
}