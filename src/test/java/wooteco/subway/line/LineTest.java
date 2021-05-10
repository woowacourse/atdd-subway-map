package wooteco.subway.line;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.exception.NotInputDataException;

@DisplayName("Line객체 테스트")
class LineTest {

    @Test
    @DisplayName("입력을 null 상태로 객체를 만들면 에러가 발생한다.")
    public void createWithDataNull() {
        assertThatThrownBy(() -> new Line(0L, null, null))
            .isInstanceOf(NotInputDataException.class);
    }

    @Test
    @DisplayName("입력을 공백 상태로 객체를 만들면 에러가 발생한다.")
    public void createWithDataSpace() {
        assertThatThrownBy(() -> new Line(0L, " ", " "))
            .isInstanceOf(NotInputDataException.class);
    }
}