package wooteco.subway.name.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wooteco.subway.common.exception.InvalidInputException;
import wooteco.subway.station.domain.StationName;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class StationNameTest {
    @Test
    @DisplayName("name 객체를 생성한다.")
    void create() {
        assertThatCode(() -> new StationName("아마찌역"))
                .doesNotThrowAnyException();
    }

    @ParameterizedTest
    @ValueSource(strings = {"검프", "검 프 역", "gump역"})
    @DisplayName("유효하지 않은 이름의 객체를 가져올 시 예외가 발생한다.")
    void validateName(String name) {
        assertThatThrownBy(() ->
                new StationName(name)
        ).isInstanceOf(InvalidInputException.class);
    }
}
