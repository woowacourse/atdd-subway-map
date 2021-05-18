package wooteco.subway.line;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.exception.ValidationFailureException;
import wooteco.subway.line.section.Section;
import wooteco.subway.line.section.Sections;

class LineTest {

    private Line line;

    @BeforeEach
    void setUp() {
        final List<Section> sectionGroup = new ArrayList<>();
        sectionGroup.add(Section.Builder().lineId(1L).upStationId(3L).downStationId(2L).distance(10).build());
        sectionGroup.add(Section.Builder().lineId(1L).upStationId(4L).downStationId(5L).distance(5).build());
        sectionGroup.add(Section.Builder().lineId(1L).upStationId(2L).downStationId(1L).distance(6).build());
        sectionGroup.add(Section.Builder().lineId(1L).upStationId(1L).downStationId(4L).distance(8).build());
        final Sections sections = new Sections(sectionGroup);

        line = new Line(1L, "2호선", "black", sections);
    }

    @DisplayName("특정 지하철역을 구간에 추가할 수 있는지 확인한다.")
    @Test
    void validateStationsToAddSection() {
        assertThatCode(() -> line.validateStationsToAddSection(3L, 6L))
            .doesNotThrowAnyException();
    }

    @DisplayName("종점역 앞이나 뒤에 지하철역을 추가할 수 있는지 확인한다.")
    @Test
    void includesTerminalStation() {
        assertThat(line.includesTerminalStation(6L, 3L)).isTrue();
        assertThat(line.includesTerminalStation(5L, 6L)).isTrue();
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
            .isEqualTo(Section.Builder().lineId(1L).upStationId(3L).downStationId(2L).distance(10).build());
    }

    @DisplayName("특정 상행역을 갖는 구간을 찾는다.")
    @Test
    void findSectionHasUpStation() {
        assertThat(line.findSectionHasUpStation(2L))
            .usingRecursiveComparison()
            .ignoringFields("id")
            .isEqualTo(Section.Builder().lineId(1L).upStationId(2L).downStationId(1L).distance(6).build());
    }

    @DisplayName("특정 하행역을 갖는 구간을 찾는다.")
    @Test
    void findSectionHasDownStation() {
        assertThat(line.findSectionHasDownStation(2L))
            .usingRecursiveComparison()
            .ignoringFields("id")
            .isEqualTo(Section.Builder().lineId(1L).upStationId(3L).downStationId(2L).distance(10).build());
    }

    @DisplayName("상행 하행 구간을 연결하는 새로운 구간을 만든다.")
    @Test
    void createConnectedSection() {
        final Section leftSection = Section.Builder().lineId(1L).upStationId(1L).downStationId(2L).distance(3).build();
        final Section rightSection = Section.Builder().lineId(1L).upStationId(2L).downStationId(3L).distance(7).build();

        final Section connectedSection = line.createConnectedSection(leftSection, rightSection);
        assertThat(connectedSection.getUpStationId()).isEqualTo(leftSection.getUpStationId());
        assertThat(connectedSection.getDownStationId()).isEqualTo(rightSection.getDownStationId());
        assertThat(connectedSection.getDistance()).isEqualTo(leftSection.getDistance() + rightSection.getDistance());
    }

    @DisplayName("구간이 1개 이하라면, 구간을 제거할 수 없다.")
    @Test
    void validateSizeToDeleteSection() {
        final List<Section> sectionGroup = new ArrayList<>();
        sectionGroup.add(Section.Builder().lineId(1L).upStationId(3L).downStationId(2L).distance(10).build());

        final Line newLine = new Line(1L, "temp", "black", sectionGroup);
        assertThatThrownBy(newLine::validateSizeToDeleteSection)
            .isInstanceOf(ValidationFailureException.class)
            .hasMessage("구간이 2개 미만이면 지울 수 없습니다.");
    }

    @DisplayName("상행 종점역을 찾는다.")
    @Test
    void findUpTerminalSection() {
        assertThat(line.findTerminalSection(3L))
            .usingRecursiveComparison()
            .ignoringFields("id")
            .isEqualTo(Section.Builder().lineId(1L).upStationId(3L).downStationId(2L).distance(10).build());
    }

    @DisplayName("하행 종점역을 찾는다.")
    @Test
    void findDownTerminalSection() {
        assertThat(line.findTerminalSection(5L))
            .usingRecursiveComparison()
            .ignoringFields("id")
            .isEqualTo(Section.Builder().lineId(1L).upStationId(4L).downStationId(5L).distance(5).build());
    }

    @DisplayName("종점역을 찾을 수 없으면 예외를 발생한다.")
    @Test
    void findTerminalSection_fail() {
        assertThatThrownBy(() -> line.findTerminalSection(4L))
            .isInstanceOf(ValidationFailureException.class)
            .hasMessage("해당역은 종점이 아닙니다.");
    }
}