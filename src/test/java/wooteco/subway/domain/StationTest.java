package wooteco.subway.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wooteco.subway.domain.Station;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class StationTest {

    @Test
    @DisplayName("역 객체를 생성한다.")
    void create() {
        assertThatCode(() -> new Station(1L, "아마찌역"))
                .doesNotThrowAnyException();
    }
}