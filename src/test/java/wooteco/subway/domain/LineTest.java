package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class LineTest {

    @ParameterizedTest
    @CsvSource(value = {"라:1", "라:30"}, delimiter = ':')
    @DisplayName("정상적인 길이의 이름으로 Line 을 생성한다.")
    void createLine(String name, int repeatCount) {
        //given
        String lineName = name.repeat(repeatCount);

        //when
        Line line = new Line(lineName, "loopy");

        //then
        assertThat(line.getName()).isEqualTo(lineName);
    }

    @ParameterizedTest
    @CsvSource(value = {"라:0", "라:31"}, delimiter = ':')
    @DisplayName("길이 범위를 벗어나는 이름으로 Line 을 생성할 경우 예외를 던진다.")
    void createLineWithInvalidLength(String name, int repeatCount) {
        //given
        String lineName = name.repeat(repeatCount);

        //when, then
        assertThatThrownBy(() -> new Line(lineName, "loopy"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("이름은 1~30 자 이내여야 합니다.");
    }

    @Test
    @DisplayName("이름을 null 값으로 Line 을 생성할 경우 예외를 던진다.")
    void createLineWithNull() {
        assertThatThrownBy(() -> new Line(null, "loopy"))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("이름은 Null 일 수 없습니다.");
    }
}
