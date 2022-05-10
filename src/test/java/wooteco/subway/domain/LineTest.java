package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LineTest {

    @DisplayName("구간을 추가한다")
    @Test
    void addSection() {
        // given
        Station station1 = new Station(1L, "station1");
        Station station2 = new Station(2L, "station2");
        Section section1 = new Section(1L, station1, station2, 10);
        Line line = new Line(1L, "line", "color", section1);

        // when
        Station station3 = new Station(3L, "station3");
        Section section2 = new Section(2L, station2, station3, 5);
        line.addSection(section2);

        // then
        assertAll(
                () -> assertThat(line.getSections()).containsExactly(section1, section2),
                () -> assertThat(line.getStations()).containsExactly(station1, station2, station3)
        );
    }

    @DisplayName("상행역과 하행역 모두 노선에 이미 등록되어 있는 경우 예외를 던진다")
    @Test
    void throwExceptionWhenBothAlreadyRegistered() {
        // given
        Station station1 = new Station(1L, "station1");
        Station station2 = new Station(2L, "station2");
        Section section1 = new Section(1L, station1, station2, 10);
        Line line = new Line(1L, "line", "color", section1);

        // when && then
        Section section2 = new Section(1L, station1, station2, 5);
        Assertions.assertThatThrownBy(() -> line.addSection(section2))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("상행역과 하행역이 이미 노선에 등록되어 있습니다.");
    }

    @DisplayName("상행역과 하행역 모두 노선에 등록되어 있지 않은 경우 예외를 던진다")
    @Test
    void throwExceptionWhenNeitherRegistered() {
        // given
        Station station1 = new Station(1L, "station1");
        Station station2 = new Station(2L, "station2");
        Section section1 = new Section(1L, station1, station2, 10);
        Line line = new Line(1L, "line", "color", section1);

        // when && then
        Station station3 = new Station(3L, "station3");
        Station station4 = new Station(4L, "station4");
        Section section2 = new Section(1L, station3, station4, 10);
        Assertions.assertThatThrownBy(() -> line.addSection(section2))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("상행역과 하행역 모두 노선에 등록되어 있지 않습니다.");
    }

    @DisplayName("새로운 구간을 등록할 때 갈래길이 생기지 않도록 추가한다 - 상행역이 같은 경우")
    @Test
    void addSectionNotMakingForkRoadCaseOne() {
        // given
        Station station1 = new Station(1L, "station1");
        Station station2 = new Station(2L, "station2");
        Section section1 = new Section(1L, station1, station2, 10);
        Line line = new Line(1L, "line", "color", section1);

        // when
        Station station3 = new Station(3L, "station3");
        Section section2 = new Section(2L, station1, station3, 7);
        line.addSection(section2);

        // then
        List<Section> sections = line.getSections();
        assertThat(sections).containsOnly(
                new Section(1L, station1, station3, 7),
                new Section(2L, station3, station2, 3)
        );
    }

    @DisplayName("갈래길이 생기지 않도록 추가할 때 거리 조건을 만족하지 않으면 예외를 던진다 - 상행역이 같은 경우")
    @Test
    void throwExceptionWhenAddSectionNotMakingForkRoadCaseOne() {
        // given
        Station station1 = new Station(1L, "station1");
        Station station2 = new Station(2L, "station2");
        Section section1 = new Section(1L, station1, station2, 10);
        Line line = new Line(1L, "line", "color", section1);

        // when && then
        Station station3 = new Station(3L, "station3");
        Section section2 = new Section(2L, station1, station3, 10);
        assertThatThrownBy(() -> line.addSection(section2))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("구간의 길이가 올바르지 않습니다.");
    }

    @DisplayName("새로운 구간을 등록할 때 갈래길이 생기지 않도록 추가한다 - 하행역이 같은 경우")
    @Test
    void addSectionNotMakingForkRoadCaseTwo() {
        // given
        Station station1 = new Station(1L, "station1");
        Station station2 = new Station(2L, "station2");
        Section section1 = new Section(1L, station1, station2, 10);
        Line line = new Line(1L, "line", "color", section1);

        // when
        Station station3 = new Station(3L, "station3");
        Section section2 = new Section(2L, station3, station2, 7);
        line.addSection(section2);

        // then
        List<Section> sections = line.getSections();
        assertThat(sections).containsOnly(
                new Section(1L, station1, station3, 3),
                new Section(2L, station3, station2, 7)
        );
    }

    @DisplayName("갈래길이 생기지 않도록 추가할 때 거리 조건을 만족하지 않으면 예외를 던진다 - 하행역이 같은 경우")
    @Test
    void throwExceptionWhenAddSectionNotMakingForkRoadCaseTwo() {
        // given
        Station station1 = new Station(1L, "station1");
        Station station2 = new Station(2L, "station2");
        Section section1 = new Section(1L, station1, station2, 10);
        Line line = new Line(1L, "line", "color", section1);

        // when && then
        Station station3 = new Station(3L, "station3");
        Section section2 = new Section(2L, station3, station2, 10);
        assertThatThrownBy(() -> line.addSection(section2))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("구간의 길이가 올바르지 않습니다.");
    }

    @DisplayName("상행 방향으로 구간을 추가한다.")
    @Test
    void addSectionToUp() {
        // given
        Station station1 = new Station(1L, "station1");
        Station station2 = new Station(2L, "station2");
        Section section1 = new Section(1L, station1, station2, 10);
        Line line = new Line(1L, "line", "color", section1);

        // when
        Station station3 = new Station(3L, "station3");
        Section section2 = new Section(2L, station3, station1, 10);
        line.addSection(section2);

        // then
        List<Section> sections = line.getSections();
        assertThat(sections).containsOnly(
                new Section(1L, station1, station2, 10),
                new Section(2L, station3, station1, 10)
        );
    }

    @DisplayName("하행 방향으로 구간을 추가한다.")
    @Test
    void addSectionToDown() {
        // given
        Station station1 = new Station(1L, "station1");
        Station station2 = new Station(2L, "station2");
        Section section1 = new Section(1L, station1, station2, 10);
        Line line = new Line(1L, "line", "color", section1);

        // when
        Station station3 = new Station(3L, "station3");
        Section section2 = new Section(2L, station2, station3, 10);
        line.addSection(section2);

        // then
        List<Section> sections = line.getSections();
        assertThat(sections).containsOnly(
                new Section(1L, station1, station2, 10),
                new Section(2L, station2, station3, 10)
        );
    }

    @DisplayName("상행 종점을 제거한다.")
    @Test
    void removeUpStation() {
        // given
        Station station1 = new Station(1L, "station1");
        Station station2 = new Station(2L, "station2");
        Section section1 = new Section(1L, station1, station2, 10);
        Line line = new Line(1L, "line", "color", section1);

        Station station3 = new Station(3L, "station3");
        Section section2 = new Section(2L, station3, station1, 10);
        line.addSection(section2);

        // when
        line.removeStation(station3);

        // then
        List<Section> sections = line.getSections();
        assertThat(sections).containsOnly(section1);
    }

    @DisplayName("하행 종점을 제거한다.")
    @Test
    void removeDownStation() {
        // given
        Station station1 = new Station(1L, "station1");
        Station station2 = new Station(2L, "station2");
        Section section1 = new Section(1L, station1, station2, 10);
        Line line = new Line(1L, "line", "color", section1);

        Station station3 = new Station(3L, "station3");
        Section section2 = new Section(2L, station2, station3, 10);
        line.addSection(section2);

        // when
        line.removeStation(station3);

        // then
        List<Section> sections = line.getSections();
        assertThat(sections).containsOnly(section1);
    }

    @DisplayName("구간이 하나인 노선에서는 역을 제거할 수 없다.")
    @Test
    void cannotRemoveStationWhenSingleSection() {
        // given
        Station station1 = new Station(1L, "station1");
        Station station2 = new Station(2L, "station2");
        Section section1 = new Section(1L, station1, station2, 10);
        Line line = new Line(1L, "line", "color", section1);

        // when && then
        assertAll(
                () -> assertThatThrownBy(() -> line.removeStation(station1))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessageContaining("구간이 하나인 노선에서는 역을 제거할 수 없습니다."),
                () -> assertThatThrownBy(() -> line.removeStation(station2))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessageContaining("구간이 하나인 노선에서는 역을 제거할 수 없습니다.")
        );
    }

    @DisplayName("입력된 역이 노선 내에 존재하지 않는 경우 예외를 던진다.")
    @Test
    void cannotRemoveNonExistStation() {
        // given
        Station station1 = new Station(1L, "station1");
        Station station2 = new Station(2L, "station2");
        Section section1 = new Section(1L, station1, station2, 10);
        Line line = new Line(1L, "line", "color", section1);

        // when && then
        Station station3 = new Station(3L, "station3");
        assertThatThrownBy(() -> line.removeStation(station3))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("제거하려는 역이 노선 내에 존재하지 않습니다.");
    }

    @DisplayName("다른 역 사이에 있는 역을 제거할 경우 구간을 재배치한다.")
    @Test
    void removeInterStation() {
        // given
        Station station1 = new Station(1L, "station1");
        Station station2 = new Station(2L, "station2");
        Section section1 = new Section(1L, station1, station2, 10);
        Line line = new Line(1L, "line", "color", section1);

        Station station3 = new Station(3L, "station3");
        Section section2 = new Section(2L, station2, station3, 10);
        line.addSection(section2);

        // when
        line.removeStation(station2);

        // then
        List<Section> sections = line.getSections();
        assertThat(sections).containsOnly(new Section(1L, station1, station3, 20));
    }
}
