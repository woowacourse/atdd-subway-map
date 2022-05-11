package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.exception.LineColorLengthException;

class LineTest {

    @Test
    @DisplayName("20자가 넘는 노선 색으로 라인을 생성하면 예외가 발생한다.")
    void createOverColorLength() {
        assertThatThrownBy(() -> Line.createWithoutId("2호선", "1234567890abcdefghijklmnop", null))
                .isInstanceOf(LineColorLengthException.class)
                .hasMessage("[ERROR] 노선 색은 20자 이하여야 합니다.");
    }
}