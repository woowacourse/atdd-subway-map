package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThatCode;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class SectionsTest {

    private static final Station STATION_FIXTURE1 = new Station(1L, "선릉역");
    private static final Station STATION_FIXTURE2 = new Station(2L, "대림역");

    @Test
    @DisplayName("노선 id, 상행 지하철역 id, 하행 지하철역 id, 거리를 전달하여 구간을 생성할 수 있다 ")
    void createSection() {
        // given
        final Long id = 1L;
        final Long lineId = 2L;
        final Station upStation = STATION_FIXTURE1;
        final Station downStation = STATION_FIXTURE2;
        final int distance = 10;

        // when & then
        assertThatCode(() -> new Section(id, lineId, upStation, downStation, distance))
                .doesNotThrowAnyException();
    }
    

}
