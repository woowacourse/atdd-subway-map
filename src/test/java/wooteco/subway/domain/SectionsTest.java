package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import java.util.List;

class SectionsTest {

    @DisplayName("추가하려는 구간의 역들이 기존 구간에 모두 존재하지 않으면 예외를 발생한다.")
    @Test
    void add_throwsNoStationExistException() {
        final Station station1 = new Station(1L, "아차산역");
        final Station station2 = new Station(2L, "군자역");
        final Section section = new Section(1L, station1, station2, 10, 1L);

        final Station newStation1 = new Station(3L, "여의도역");
        final Station newStation2 = new Station(4L, "마장역");
        final Section newSection = new Section(newStation1, newStation2, 5, 1L);

        final Sections sections = new Sections(List.of(section));

        assertThatThrownBy(() -> sections.add(newSection))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 구간을 추가할 수 없습니다.");
    }

    @DisplayName("추가하려는 구간의 역들이 기존 구간에 모두 존재하면 예외를 발생한다.")
    @Test
    void add_throwsAllStationExistException() {
        final Station station1 = new Station(1L, "아차산역");
        final Station station2 = new Station(2L, "군자역");
        final Section section = new Section(1L, station1, station2, 10, 1L);

        final Section newSection = new Section(station1, station2, 5, 1L);

        final Sections sections = new Sections(List.of(section));

        assertThatThrownBy(() -> sections.add(newSection))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없습니다.");
    }

    @DisplayName("상행 구간 등록시 역 사이 거리가 기존 구간보다 크거나 같을 경우 예외를 발생한다.")
    @ParameterizedTest
    @ValueSource(ints = {10, 11, 15})
    void add_throwsUpSectionDistanceException(final int distance) {
        final Station station1 = new Station(1L, "아차산역");
        final Station station2 = new Station(2L, "마장역");
        final Section section = new Section(1L, station1, station2, 10, 1L);

        final Station newStation1 = new Station(1L, "아차산역");
        final Station newStation2 = new Station(3L, "군자역");
        final Section newSection = new Section(newStation1, newStation2, distance, 1L);

        final Sections sections = new Sections(List.of(section));

        assertThatThrownBy(() -> sections.add(newSection))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("역 사이에 새로운 역을 등록할 경우 기존 구간 거리보다 적어야 합니다.");
    }

    @DisplayName("하행 구간 등록시 역 사이 거리가 기존 구간보다 크거나 같을 경우 예외를 발생한다.")
    @ParameterizedTest
    @ValueSource(ints = {10, 11, 15})
    void add_throwsDownSectionDistanceException(final int distance) {
        final Station station1 = new Station(1L, "아차산역");
        final Station station2 = new Station(2L, "마장역");
        final Section section = new Section(1L, station1, station2, 10, 1L);

        final Station newStation1 = new Station(3L, "장한평역");
        final Station newStation2 = new Station(2L, "마장역");
        final Section newSection = new Section(newStation1, newStation2, distance, 1L);

        final Sections sections = new Sections(List.of(section));

        assertThatThrownBy(() -> sections.add(newSection))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("역 사이에 새로운 역을 등록할 경우 기존 구간 거리보다 적어야 합니다.");
    }

    @DisplayName("등록할 구간이 갈래길인지 확인한다.")
    @ParameterizedTest
    @CsvSource(value = {"1,아차산역,3,장한평역,true", "3,천호역,1,아차산역,false"})
    void isBranched(final long stationId1, final String stationName1,
                    final long stationId2, final String stationName2, final boolean expected) {
        final Station station1 = new Station(1L, "아차산역");
        final Station station2 = new Station(2L, "마장역");
        final Section section = new Section(1L, station1, station2, 10, 1L);

        final Station newStation1 = new Station(stationId1, stationName1);
        final Station newStation2 = new Station(stationId2, stationName2);
        final Section newSection = new Section(newStation1, newStation2, 5, 1L);

        final Sections sections = new Sections(List.of(section));

        assertThat(sections.isBranched(newSection)).isEqualTo(expected);
    }
}
