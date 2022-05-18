package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class StationTest {

    @DisplayName("빈 값이 들어오면 예외를 발생시킨다.")
    @Test
    void exceptionEmptyName() {
        assertThatThrownBy(() -> new Station(" "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("역의 이름은 빈 값이면 안됩니다.");
    }
}
