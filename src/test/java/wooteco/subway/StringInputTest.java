package wooteco.subway;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.exception.SubwayCustomException;
import wooteco.subway.exception.SubwayException;

@DisplayName("문자열 입력 객체 테스트")
class StringInputTest {

    @Test
    @DisplayName("입력을 null 상태로 객체를 만들면 에러가 발생한다.")
    public void createWithDataNull() {
        assertThatThrownBy(() -> new StringInput(null))
            .isInstanceOf(SubwayCustomException.class)
            .hasMessage(SubwayException.INVALID_INPUT_NAME_OR_COLOR_EXCEPTION.message());
    }

    @Test
    @DisplayName("입력을 공백 상태로 객체를 만들면 에러가 발생한다.")
    public void createWithDataSpace() {
        assertThatThrownBy(() -> new StringInput(" "))
            .isInstanceOf(SubwayCustomException.class)
            .hasMessage(SubwayException.INVALID_INPUT_NAME_OR_COLOR_EXCEPTION.message());
    }
}