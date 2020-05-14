package wooteco.subway.admin.domain;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class StationTest {

    @Test
    void create_Success() {
        assertThatCode(() -> new Station("지하철"))
            .doesNotThrowAnyException();
    }

    @Test
    void create_Fail_When_NameEmpty() {
        assertThatThrownBy(() -> new Station(""))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("name은 빈 값이 올 수 없습니다.");
    }

    @Test
    void create_Fail_When_NameContainsSpace() {
        assertThatThrownBy(() -> new Station("지 하철1"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("name은 공백이 포함 될 수 없습니다.");
    }

    @Test
    void create_Fail_When_NameContainsNumber() {
        assertThatThrownBy(() -> new Station("지하철1"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Station.name은 숫자가 포함 될 수 없습니다.");
    }
}