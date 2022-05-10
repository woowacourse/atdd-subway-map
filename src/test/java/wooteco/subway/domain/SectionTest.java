package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SectionTest {

    @Test
    @DisplayName("상행역과 하행역이 같을 경우 예외를 발생시킨다.")
    void sameUpAndDownStationId() {
        //given
        Long upStationId = 1L;
        Long downStationId = 1L;
        //when

        //then
        assertThatThrownBy(() -> new Section(1L, upStationId, downStationId, 10))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("상행역과 하행역이 같습니다.");
    }

}