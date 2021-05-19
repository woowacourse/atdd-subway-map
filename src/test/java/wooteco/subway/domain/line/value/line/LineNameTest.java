package wooteco.subway.domain.line.value.line;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.exception.line.NameLengthException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LineNameTest {

    @DisplayName("LineName의 글자 수는 1 이상이어야 한다.")
    @Test
    void lineColor() {
        assertThatThrownBy(() -> new LineName(""))
                .isInstanceOf(NameLengthException.class)
                .hasMessage("이름은 0 보다 커야 합니다.");
    }


    @Test
    void asString() {
        LineName test = new LineName("test");

        assertThat(test.asString()).isEqualTo("test");
    }
}