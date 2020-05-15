package wooteco.subway.admin.domain;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class StationTest {
    @ParameterizedTest
    @ValueSource(strings = {"", " ", "12역"})
    void create_validateName(String name) {
        assertThatThrownBy(() -> new Station(name)).isInstanceOf(IllegalArgumentException.class);
    }
}
