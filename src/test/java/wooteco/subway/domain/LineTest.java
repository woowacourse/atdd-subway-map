package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LineTest {

    @DisplayName("유효한 이름을 가진 지하철 노선을 생성한다.")
    @Test
    void create_success() {
        assertDoesNotThrow(() -> new Line("1호선", "GREEN", 1L, 2L, 10));
    }

    @DisplayName("지하철 노선의 이름이 빈칸이면 예외가 발생한다.")
    @Test
    void create_fail_empty() {
        assertThatThrownBy(() -> new Line(" ", "GREEN", 1L, 2L, 10))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("[ERROR] 이름에 빈칸 입력은 허용하지 않습니다.");
    }

    @DisplayName("지하철 노선의 이름이 2자 미만이면 예외가 발생한다.")
    @Test
    void create_fail_length() {
        assertThatThrownBy(() -> new Line("역", "GREEN", 1L, 2L, 10))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("[ERROR] 이름은 2글자 이상이어야합니다.");
    }

    @DisplayName("지하철 노선의 이름에 특수문자가 포함되어있으면 예외가 발생한다.")
    @Test
    void create_fail_special_character() {
        assertThatThrownBy(() -> new Line("역★", "GREEN", 1L, 2L, 10))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("[ERROR] 이름에 특수문자를 입력할 수 없습니다.");
    }
}
