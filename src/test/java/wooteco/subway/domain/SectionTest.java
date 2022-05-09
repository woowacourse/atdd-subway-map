package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class SectionTest {

    @Test
    @DisplayName("길이가 0이하일 경우 예외를 던진다.")
    void validateDistance() {
        //when, then
        assertThatThrownBy(() -> new Section(new Station("역삼"), new Station("강남"), 0))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("거리는 0보다 작을 수 없습니다.");
    }
}
