package wooteco.subway.station.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wooteco.subway.common.exception.InvalidInputException;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class StationTest {
    @Test
    @DisplayName("역 객체를 생성한다.")
    void create() {
        assertThatCode(() -> new Station(1L, "아마찌역"))
                .doesNotThrowAnyException();
    }

    @ParameterizedTest
    @ValueSource(strings = {"아마찌", "아마찌역!"})
    @DisplayName("잘 못된 이름의 역이 들어올 시 예외가 발생한다")
    void createException(String name) {
        assertThatThrownBy(() -> new Station(1L, name))
                .isInstanceOf(InvalidInputException.class);
    }
}
