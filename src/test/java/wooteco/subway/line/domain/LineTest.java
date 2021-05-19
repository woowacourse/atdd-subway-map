package wooteco.subway.line.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wooteco.subway.common.exception.InvalidNameException;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LineTest {
    @Test
    @DisplayName("라인 정상 생성 테스트 ")
    void create() {
        assertThatCode(() -> new Line("신분당선", "bg-red-600"))
                .doesNotThrowAnyException();
    }

    @ParameterizedTest
    @ValueSource(strings = {"아마찌", "아마찌선!"})
    @DisplayName("잘 못된 이름의 라인이 들어올 시 예외가 발생한다")
    void createException(String name) {
        assertThatThrownBy(() -> new Line(1L, name, "bg-red-600"))
                .isInstanceOf(InvalidNameException.class);
    }
}
