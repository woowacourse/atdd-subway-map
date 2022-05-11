package wooteco.subway.domain.section;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.domain.Station;

class SectionTest {

    private static final Station STATION_잠실역 = new Station(1L, "잠실역");
    private static final Station STATION_역삼역 = new Station(2L, "역삼역");

    /**
     * create table if not exists SECTION ( id bigint auto_increment not null, line_id bigint not null, up_station_id
     * bigint not null, down_station_id bigint not null, distance int, primary key(id) );
     */

    @DisplayName("구간을 생성한다.")
    @Test
    void create_success() {
        final Long lineId = 1L;
        final int distance = 1;

        Assertions.assertDoesNotThrow(() -> new Section(1L, lineId, 1L, 2L, distance));
    }

    @DisplayName("구간 생성시, 상행 종점과 하행 종점이 같으면 예외를 발생한다.")
    @Test
    void create_fail_same_station() {
        final Long lineId = 1L;
        final int distance = 1;

        assertThatThrownBy(() -> new Section(1L, lineId, 1L, 1L, distance))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("[ERROR] 상행 종점과 하행 종점이 같을 수 없습니다.");
    }

    @DisplayName("구간 생성시, 부적절한 거리가 입력되면 예외를 발생한다.")
    @Test
    void create_fail_invalid_distance() {
        final Long lineId = 1L;
        final int distance = 0;

        assertThatThrownBy(() -> new Section(1L, lineId, 1L, 2L, distance))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("[ERROR] 부적절한 거리가 입력되었습니다. 0보다 큰 거리를 입력해주세요.");
    }
}
