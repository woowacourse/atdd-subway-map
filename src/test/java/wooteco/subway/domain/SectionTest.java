package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class SectionTest {

    @DisplayName("Section에 들어오는 Station은 null일 수 없다.")
    @Test
    void validateNull() {
        assertThatThrownBy(() -> new Section(null, null, 7))
                .isInstanceOf(NullPointerException.class);
    }

    @DisplayName("Section에 들어오는 distance는 0 이하면 안된다.")
    @Test
    void validateDistanceOverZero() {
        assertThatThrownBy(() -> new Section(new Station("신림역"), new Station("신대방역"), -1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("거리는 0 이하가 될 수 없습니다.");
    }
}
