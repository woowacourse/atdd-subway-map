package wooteco.subway.domain;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

class SectionsTest {

    @DisplayName("추가하려는 구간의 역들이 기존 구간에 모두 존재하지 않으면 예외를 발생한다.")
    @Test
    void add_throwsNoStationExistException() {
        final Station station1 = new Station(1L, "아차산역");
        final Station station2 = new Station(2L, "군자역");
        final Section section = new Section(1L, station1, station2, 10, 1L);

        final Station newStation1 = new Station(1L, "여의도역");
        final Station newStation2 = new Station(2L, "마장역");
        final Section newSection = new Section(newStation1, newStation2, 5, 1L);

        final Sections sections = new Sections(List.of(section));

        Assertions.assertThatThrownBy(() -> sections.add(newSection))
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

        Assertions.assertThatThrownBy(() -> sections.add(newSection))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없습니다.");
    }
}
