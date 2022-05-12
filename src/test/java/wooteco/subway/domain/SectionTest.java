package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static wooteco.subway.Fixtures.SECTION;
import static wooteco.subway.Fixtures.SECTION_2;
import static wooteco.subway.Fixtures.SECTION_3;
import static wooteco.subway.Fixtures.STATION;
import static wooteco.subway.Fixtures.STATION_2;
import static wooteco.subway.Fixtures.STATION_3;
import static wooteco.subway.Fixtures.STATION_4;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SectionTest {

    @DisplayName("기존 구간의 상행과 하행을 뒤집은 구간을 반환한다.")
    @Test
    void toReverse() {
        assertThat(SECTION.toReverse())
                .isEqualTo(new Section(1L, STATION_2,STATION,10));
    }

    @DisplayName("기존 구간에서 새 구간을 연결하고 남은 구간을 계산한다.")
    @Test
    void calculateRemainSection() {
        assertThat(SECTION_2.toRemain(SECTION_3))
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(new Section(1L, STATION_4,STATION_3,2));
    }

    @DisplayName("구간에 해당역이 포함되는지 확인한다.")
    @Test
    void contains() {
        assertThat(SECTION.contains(STATION))
                .isTrue();
        assertThat(SECTION.contains(STATION_3))
                .isFalse();
    }
}
