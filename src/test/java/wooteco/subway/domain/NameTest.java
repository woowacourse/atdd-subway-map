package wooteco.subway.domain;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class NameTest {

    @Test
    @DisplayName("이름이 null이면 예외를 반환한다.")
    void checkNull() {
        Assertions.assertThatThrownBy(() -> new Name(null))
                .isInstanceOf(IllegalArgumentException.class)
                        .hasMessage("이름은 null이 될 수 없습니다.");
    }

    @Test
    @DisplayName("이름에 공백이 포함되면 예외를 반환한다.")
    void checkEmpty() {
        Assertions.assertThatThrownBy(() -> new Name("      "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이름에 공백이 포함될 수 없습니다.");
    }

    @Test
    @DisplayName("이름에 특수문자가 포함되면 예외를 반환한다.")
    void checkSpecialChar() {
        Assertions.assertThatThrownBy(() -> new Name("yaho!"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이름에 특수문자가 포함될 수 없습니다.");
    }
}
