package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.exception.NullNameException;

class NameTest {

    @Test
    @DisplayName("생성자에 NULL이 들어왔을 경우 예외처리")
    void createNullException() {
        // given

        // when

        // then
        assertThatThrownBy(() -> new Name(null))
            .isInstanceOf(NullNameException.class);
    }
}