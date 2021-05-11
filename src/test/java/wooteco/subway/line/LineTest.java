package wooteco.subway.line;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.exception.service.ValidationFailureException;
import wooteco.subway.line.section.Section;
import wooteco.subway.line.section.Sections;
import wooteco.subway.station.Station;
import wooteco.subway.station.Stations;

class LineTest {
    private Line line;

    @BeforeEach
    void setUp() {
        final List<Section> sectionGroup = new ArrayList<>();
        sectionGroup.add(new Section(1L, 3L, 2L, 10));
        sectionGroup.add(new Section(1L, 4L, 5L, 5));
        sectionGroup.add(new Section(1L, 2L, 1L, 6));
        sectionGroup.add(new Section(1L, 1L, 4L, 8));
        final Sections sections = new Sections(sectionGroup);

        final List<Station> stationsGroup = sections.distinctStationIds().stream()
            .map(id -> new Station(id, "역" + id))
            .collect(Collectors.toList());
        line = new Line(1L, "2호선", "black", sections, new Stations(stationsGroup));
    }

    @DisplayName("특정 지하철역을 구간에 추가할 수 있는지 확인한다.")
    @Test
    void validateStationsToAddSection() {
        assertThatCode(() -> line.validateStationsToAddSection(3L, 6L))
            .doesNotThrowAnyException();
    }

    @DisplayName("종점역 앞이나 뒤에 지하철역을 추가할 수 있는지 확인한다.")
    @Test
    void isAddableTerminalStation() {
        assertThat(line.isAddableTerminalStation(6L, 3L)).isTrue();
        assertThat(line.isAddableTerminalStation(5L, 6L)).isTrue();
    }

    @DisplayName("해당 역이 종점역인지 확인한다.")
    @Test
    void isTerminalStation() {
        assertThat(line.isTerminalStation(3L)).isTrue();
        assertThat(line.isTerminalStation(5L)).isTrue();
    }

    @DisplayName("수정할 구간을 찾는다.")
    @Test
    void findUpdatedTarget() {
        final Section updatedTarget = line.findUpdatedTarget(3L, 6L, 5);
        assertThat(updatedTarget)
            .usingRecursiveComparison()
            .ignoringFields("id")
            .isEqualTo(new Section(1L, 3L, 2L, 10));
    }

    @DisplayName("특정 상행역을 갖는 구간을 찾는다.")
    @Test
    void findSectionHasUpStation() {
        assertThat(line.findSectionHasUpStation(2L))
            .usingRecursiveComparison()
            .ignoringFields("id")
            .isEqualTo(new Section(1L, 2L, 1L, 6));
    }

    @DisplayName("특정 하행역을 갖는 구간을 찾는다.")
    @Test
    void findSectionHasDownStation() {
        assertThat(line.findSectionHasDownStation(2L))
            .usingRecursiveComparison()
            .ignoringFields("id")
            .isEqualTo(new Section(1L, 3L, 2L, 10));
    }

    @DisplayName("구간이 1개 이하라면, 구간을 제거할 수 없다.")
    @Test
    void validateSizeToDeleteSection() {
        final List<Section> sectionGroup = new ArrayList<>();
        sectionGroup.add(new Section(1L, 3L, 2L, 10));

        final Line newLine = new Line(1L, "temp", "black", sectionGroup, Collections.emptyMap());
        assertThatThrownBy(newLine::validateSizeToDeleteSection)
            .isInstanceOf(ValidationFailureException.class)
            .hasMessage("구간이 한 개 이하면 지울 수 없습니다.");
    }

    @DisplayName("상행 종점역을 찾는다.")
    @Test
    void findUpTerminalSection() {
        assertThat(line.findTerminalSection(3L))
            .usingRecursiveComparison()
            .ignoringFields("id")
            .isEqualTo(new Section(1L, 3L, 2L, 10));
    }

    @DisplayName("하행 종점역을 찾는다.")
    @Test
    void findDownTerminalSection() {
        assertThat(line.findTerminalSection(5L))
            .usingRecursiveComparison()
            .ignoringFields("id")
            .isEqualTo(new Section(1L, 4L, 5L, 5));
    }

    @DisplayName("종점역을 찾을 수 없으면 예외를 발생한다.")
    @Test
    void findTerminalSection_fail() {
        assertThatThrownBy(() -> line.findTerminalSection(4L))
            .isInstanceOf(ValidationFailureException.class)
            .hasMessage("해당역은 종점이 아닙니다.");
    }
}