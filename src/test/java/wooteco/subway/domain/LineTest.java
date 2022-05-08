package wooteco.subway.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wooteco.subway.exception.constant.BlankArgumentException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LineTest {

    @DisplayName("지하철 노선 이름에 빈 문자열을 저장할 수 없다")
    @ParameterizedTest
    @ValueSource(strings = {"", "  ", "     "})
    void saveEmptyName(String lineName) {
        assertThatThrownBy(() -> new Line(lineName, "bg-red-600"))
                .isInstanceOf(BlankArgumentException.class);
    }
}