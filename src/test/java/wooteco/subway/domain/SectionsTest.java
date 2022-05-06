package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SectionsTest {

    @Test
    @DisplayName("생성 시 size가 0이면 예외 발생")
    void createExceptionByEmptySize() {
        assertThatThrownBy(() -> new Sections(new ArrayList<>()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("sections는 크기가 0으로는 생성할 수 없습니다.");
    }

    @Test
    @DisplayName("정렬된 Station을 반환할 수 있다.")
    void calculateSortedStations() {
        // given
        Line line = new Line(1L, "신분당선", "bg-red-600");
        Station station1 = new Station(1L, "오리");
        Station station2 = new Station(2L, "배카라");
        Station station3 = new Station(3L, "오카라");
        Station station4 = new Station(4L, "배리");
        Sections sections = new Sections(List.of(new Section(3L, line, station3, station4, 4),
                new Section(1L, line, station1, station2, 1),
                new Section(2L, line, station2, station3, 2)));

        // when
        List<Station> stations = sections.calculateSortedStations();

        // then
        assertThat(stations).containsExactly(station1, station2, station3, station4);
    }
}
