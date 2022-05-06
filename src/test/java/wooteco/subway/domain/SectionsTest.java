package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SectionsTest {

    @Test
    @DisplayName("생성 시 size가 0이면 예외 발생")
    void createExceptionByEmptySize() {
        assertThatThrownBy(() -> new Sections(new ArrayList<>()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("sections는 크기가 0으로는 생성할 수 없습니다.");
    }
}
