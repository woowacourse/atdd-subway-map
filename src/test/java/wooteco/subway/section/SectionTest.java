package wooteco.subway.section;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.section.domain.Section;
import wooteco.subway.section.exception.SectionError;
import wooteco.subway.section.exception.SectionException;
import wooteco.subway.station.domain.Station;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SectionTest {
    @Test
    @DisplayName("같은 역 입력 들어왔을 때")
    void sectionSameStation() {
        Station station = new Station(5L, "new");

        assertThatThrownBy(() -> new Section(station, station, 3))
                .isInstanceOf(SectionException.class)
                .hasMessage(SectionError.SAME_STATION_INPUT.getMessage());
    }
}
