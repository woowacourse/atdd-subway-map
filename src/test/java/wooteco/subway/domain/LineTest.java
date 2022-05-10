package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LineTest {

    private Station station1;
    private Station station2;
    private Section station1ToStation2;
    private Line line1;

    @BeforeEach
    void setUp() {
        // given
        station1 = new Station(1L, "station1");
        station2 = new Station(2L, "station2");
        station1ToStation2 = new Section(1L, station1, station2, 10);
        line1 = new Line(1L, "line1", "blue");
        line1.addSection(station1ToStation2);
    }

    @DisplayName("구간을 추가한다")
    @Test
    void addSection() {
        // when
        Station station3 = new Station(3L, "station3");
        Section station2ToStation3 = new Section(2L, station2, station3, 10);
        line1.addSection(station2ToStation3);

        // then
        assertAll(
                () -> assertThat(line1.getSections()).containsExactly(station1ToStation2, station2ToStation3),
                () -> assertThat(line1.getStations()).containsExactly(station1, station2, station3)
        );
    }

    @DisplayName("상행역과 하행역 모두 노선에 이미 등록되어 있는 경우 예외를 던진다")
    @Test
    void throwExceptionWhenBothAlreadyRegistered() {
        // when && then
        Section alreadyExist = new Section(1L, station1, station2, 10);
        Assertions.assertThatThrownBy(() -> line1.addSection(alreadyExist))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("상행역과 하행역이 이미 노선에 등록되어 있습니다.");
    }

    @DisplayName("상행역과 하행역 모두 노선에 등록되어 있지 않은 경우 예외를 던진다")
    @Test
    void throwExceptionWhenNeitherRegistered() {
        // when && then
        Station station3 = new Station(3L, "station3");
        Station station4 = new Station(4L, "station4");
        Section station3ToStation4 = new Section(2L, station3, station4, 10);
        Assertions.assertThatThrownBy(() -> line1.addSection(station3ToStation4))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("상행역과 하행역 모두 노선에 등록되어 있지 않습니다.");
    }

    @DisplayName("새로운 구간을 등록할 때 갈래길이 생기지 않도록 추가한다 - 상행역이 같은 경우")
    @Test
    void addSectionNotMakingForkRoadCaseOne() {
        // when
        Station station3 = new Station(3L, "station3");
        Section station1ToStation3 = new Section(2L, station1, station3, 7);
        line1.addSection(station1ToStation3);

        // then
        List<Section> sections = line1.getSections();
        assertThat(sections).containsOnly(
                new Section(1L, station1, station3, 7),
                new Section(2L, station3, station2, 3)
        );
    }

    @DisplayName("갈래길이 생기지 않도록 추가할 때 거리 조건을 만족하지 않으면 예외를 던진다 - 상행역이 같은 경우")
    @Test
    void throwExceptionWhenAddSectionNotMakingForkRoadCaseOne() {
        // when && then
        int greaterThanOrEqualDistance = 10;
        Station station3 = new Station(3L, "station3");
        Section station1ToStation3 = new Section(2L, station1, station3, greaterThanOrEqualDistance);
        assertThatThrownBy(() -> line1.addSection(station1ToStation3))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("구간의 길이가 올바르지 않습니다.");
    }

    @DisplayName("새로운 구간을 등록할 때 갈래길이 생기지 않도록 추가한다 - 하행역이 같은 경우")
    @Test
    void addSectionNotMakingForkRoadCaseTwo() {
        // when
        Station station3 = new Station(3L, "station3");
        Section station3ToStation2 = new Section(2L, station3, station2, 7);
        line1.addSection(station3ToStation2);

        // then
        List<Section> sections = line1.getSections();
        assertThat(sections).containsOnly(
                new Section(1L, station1, station3, 3),
                new Section(2L, station3, station2, 7)
        );
    }

    @DisplayName("갈래길이 생기지 않도록 추가할 때 거리 조건을 만족하지 않으면 예외를 던진다 - 하행역이 같은 경우")
    @Test
    void throwExceptionWhenAddSectionNotMakingForkRoadCaseTwo() {
        // when && then
        Station station3 = new Station(3L, "station3");
        int greaterThanOrEqualDistance = 10;
        Section station3ToStation2 = new Section(2L, station3, station2, greaterThanOrEqualDistance);
        assertThatThrownBy(() -> line1.addSection(station3ToStation2))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("구간의 길이가 올바르지 않습니다.");
    }

    @DisplayName("상행 방향으로 구간을 추가한다.")
    @Test
    void addSectionToUp() {
        // when
        Station station3 = new Station(3L, "station3");
        Section station3ToStation1 = new Section(2L, station3, station1, 10);
        line1.addSection(station3ToStation1);

        // then
        List<Section> sections = line1.getSections();
        assertThat(sections).containsOnly(
                new Section(1L, station1, station2, 10),
                new Section(2L, station3, station1, 10)
        );
    }

    @DisplayName("하행 방향으로 구간을 추가한다.")
    @Test
    void addSectionToDown() {
        // when
        Station station3 = new Station(3L, "station3");
        Section station2ToStation3 = new Section(2L, station2, station3, 10);
        line1.addSection(station2ToStation3);

        // then
        List<Section> sections = line1.getSections();
        assertThat(sections).containsOnly(
                new Section(1L, station1, station2, 10),
                new Section(2L, station2, station3, 10)
        );
    }

    @DisplayName("상행 종점을 제거한다.")
    @Test
    void removeUpStation() {
        Station station3 = new Station(3L, "station3");
        Section station3ToStation1 = new Section(2L, station3, station1, 10);
        line1.addSection(station3ToStation1);

        // when
        line1.removeStation(station3);

        // then
        List<Section> sections = line1.getSections();
        assertThat(sections).containsOnly(station1ToStation2);
    }

    @DisplayName("하행 종점을 제거한다.")
    @Test
    void removeDownStation() {
        Station station3 = new Station(3L, "station3");
        Section station2ToStation3 = new Section(2L, station2, station3, 10);
        line1.addSection(station2ToStation3);

        // when
        line1.removeStation(station3);

        // then
        List<Section> sections = line1.getSections();
        assertThat(sections).containsOnly(station1ToStation2);
    }

    @DisplayName("구간이 하나인 노선에서는 역을 제거할 수 없다.")
    @Test
    void cannotRemoveStationWhenSingleSection() {
        // when && then
        assertAll(
                () -> assertThatThrownBy(() -> line1.removeStation(station1))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessageContaining("구간이 하나인 노선에서는 역을 제거할 수 없습니다."),
                () -> assertThatThrownBy(() -> line1.removeStation(station2))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessageContaining("구간이 하나인 노선에서는 역을 제거할 수 없습니다.")
        );
    }

    @DisplayName("입력된 역이 노선 내에 존재하지 않는 경우 예외를 던진다.")
    @Test
    void cannotRemoveNonExistStation() {
        // when && then
        Station station3 = new Station(3L, "station3");
        assertThatThrownBy(() -> line1.removeStation(station3))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("제거하려는 역이 노선 내에 존재하지 않습니다.");
    }

    @DisplayName("다른 역 사이에 있는 역을 제거할 경우 구간을 재배치한다.")
    @Test
    void removeInterStation() {
        // given
        Station station3 = new Station(3L, "station3");
        Section station2ToStation3 = new Section(2L, station2, station3, 10);
        line1.addSection(station2ToStation3);

        // when
        line1.removeStation(station2);

        // then
        List<Section> sections = line1.getSections();
        assertThat(sections).containsOnly(new Section(1L, station1, station3, 20));
    }
}
