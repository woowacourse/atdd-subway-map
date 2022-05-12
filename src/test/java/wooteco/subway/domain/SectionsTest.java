package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class SectionsTest {

    @DisplayName("지하철역을 반환한다.")
    @Test
    void getStations() {
        Sections sections = new Sections(List.of(
                new Section(1L, 2L, 1L, 1),
                new Section(2L, 3L, 1L, 1),
                new Section(3L, 4L, 1L, 1)
        ));
        Sections sections1 = new Sections(List.of(
                new Section(1L, 2L, 1L, 1),
                new Section(2L, 3L, 1L, 1)
        ));
        assertThat(sections.getStationsId()).containsExactly(1L, 2L, 3L, 4L);
    }

    @DisplayName("구간을 등록한다.")
    @Test
    void add() {
        Sections sections = new Sections(List.of(
                new Section(1L, 2L, 1L, 10),
                new Section(2L, 3L, 1L, 10),
                new Section(3L, 4L, 1L, 10)
        ));

        Sections actual = sections.update(new Section(1L, 5L, 1L, 5))
                .update(new Section(6L, 4L, 1L, 5));

        assertThat(actual.getStationsId()).contains(1L, 2L, 3L, 4L, 5L, 6L);
    }

    @Nested
    @DisplayName("구간 등록을 할 수 없는 경우")
    class SectionsValidateAddable {

        private Sections sections = new Sections(List.of(
                new Section(1L, 2L, 3L, 10),
                new Section(2L, 3L, 3L, 10),
                new Section(3L, 4L, 3L, 10)
        ));

        @DisplayName("상행선과 하행선 둘 다 기존 지하철역에 포함되지 않는다.")
        @Test
        void notContainsUpStationAndDownStation() {
            Section section = new Section(5L, 6L, 3L, 1);
            assertThatThrownBy(() -> sections.update(section))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("상행선과 하행선이 노선에 없습니다.");
        }

        @DisplayName("상행성과 하행선 둘 다 기존 지하철역에 포함한다.")
        @Test
        void containsUpStationAndDownStation() {
            Section section = new Section(1L, 2L, 3L, 1);
            assertThatThrownBy(() -> sections.update(section))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("상행선과 하행선 둘 다 노선에 이미 존재합니다.");
        }

        @DisplayName("상행선끼리 같거나 하행선끼리 같을 때 기존 구간의 거리보다 추가할 거리가 크다.")
        @Test
        void containsSamePositionStationAndLongerDistance() {
            Section section = new Section(1L, 7L, 3L, 11);
            assertThatThrownBy(() -> sections.update(section))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("이미 존재하는 구간의 거리보다 거리가 길거나 같습니다.");
        }
    }

    @DisplayName("구간 중 특정 지하철역을 삭제한다.")
    @Test
    void deleteByStation() {
        Sections sections = new Sections(List.of(
                new Section(1L, 2L, 1L, 10),
                new Section(2L, 3L, 1L, 10),
                new Section(3L, 4L, 1L, 10)
        ));
        assertThat(sections.deleteByStation(new Station(2L, "제거될 역"))
                .value()).hasSize(2);
    }

    @DisplayName("구간이 하나인 노선은 구간을 제거할 수 없다.")
    @Test
    void exceptionWhenLineHasOneSection() {
        Sections sections = new Sections(List.of(new Section(1L, 2L, 1L, 10)));
        assertThatThrownBy(() -> sections.deleteByStation(new Station(2L, "제거될 역")))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("구간이 하나인 노선은 구간을 제거할 수 없습니다.");
    }
}
