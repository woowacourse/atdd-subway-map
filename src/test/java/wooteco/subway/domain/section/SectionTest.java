package wooteco.subway.domain.section;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.domain.section.Section;
import wooteco.subway.domain.station.Station;

public class SectionTest {

    @Test
    @DisplayName("구간 생성하기")
    public void createSection() {
        // given
        Station station1 = new Station(1L, "잠실새내역");
        Station station2 = new Station(2L, "잠실역");

        // when
        Section section = new Section(station1, station2, 5);

        // then
        assertThat(section)
            .extracting("upStationId", "downStationId", "distance")
            .doesNotContainNull()
            .containsExactly(station1.getId(), station2.getId(), 5);
    }
}