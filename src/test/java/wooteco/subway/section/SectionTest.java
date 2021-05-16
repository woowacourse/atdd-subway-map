package wooteco.subway.section;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.exception.section.DuplicateStationException;
import wooteco.subway.line.Line;
import wooteco.subway.station.Station;

class SectionTest {
    @Test
    @DisplayName("중복된 지하철역이 입력되었을 시 예외처리")
    public void validateDuplicatedStation() {
        // given
        Station upStation = new Station( "신설동역");
        Station downStation = new Station("신설동역");
        Line line = new Line("1호선", "파란");
        int distance = 1;

        // when

        // then
        assertThatThrownBy(() -> new Section(line, upStation, downStation, distance))
            .isInstanceOf(DuplicateStationException.class);
    }
}