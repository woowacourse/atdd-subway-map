package wooteco.subway.domain.line.value.line;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class LineNameTest {

    @DisplayName("LineName의 글자 수는 1 이상이어야 한다.")
    @Test
    void lineColor() {
        assertThatThrownBy(() -> new LineName(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("글자 수는 0보다 커야 합니다.");
    }


    @Test
    void asString() {
        LineName test = new LineName("test");

        assertThat(test.asString()).isEqualTo("test");
    }
}