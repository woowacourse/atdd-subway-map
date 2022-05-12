package wooteco.subway.domain.section;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SectionTest {
    
    @DisplayName("구간을 생성한다.")
    @Test
    void create_success() {
        //given & when
        final Long lineId = 1L;
        final int distance = 1;

        //then
        Assertions.assertDoesNotThrow(() -> new Section(1L, lineId, 1L, 2L, distance));
    }

    @DisplayName("구간 생성시, 상행 종점과 하행 종점이 같으면 예외를 발생한다.")
    @Test
    void create_fail_same_station() {
        //given & when
        final Long lineId = 1L;
        final int distance = 1;

        //then
        assertThatThrownBy(() -> new Section(1L, lineId, 1L, 1L, distance))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("[ERROR] 상행 종점과 하행 종점이 같을 수 없습니다.");
    }

    @DisplayName("구간 생성시, 부적절한 거리가 입력되면 예외를 발생한다.")
    @Test
    void create_fail_invalid_distance() {
        //given & when
        final Long lineId = 1L;
        final int distance = 0;

        //then
        assertThatThrownBy(() -> new Section(1L, lineId, 1L, 2L, distance))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("[ERROR] 부적절한 거리가 입력되었습니다. 0보다 큰 거리를 입력해주세요.");
    }
}
