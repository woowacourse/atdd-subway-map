package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.domain.station.Station;

public class SectionTest {

    @DisplayName("구간 생성 - 성공")
    @Test
    public void create(){
        Station station1 = new Station(1L, "잠실새내역");
        Station station2 = new Station(2L, "잠실역");

        // when
        Section section = new Section(station1, station2, 5);

        // then
        assertThat(section)
            .extracting("upStation", "downStation", "distance")
            .doesNotContainNull()
            .containsExactly(station1, station2, 5);
    }
}
