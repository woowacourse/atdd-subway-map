package wooteco.subway.section.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.exception.SectionAdditionException;
import wooteco.subway.line.model.Line;
import wooteco.subway.station.model.Station;

import java.util.ArrayList;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SectionsTest {

    private Sections sections;

    private Line line = new Line(1L, "2호선", "green");

    private Station aStation = new Station(1L, "A");
    private Station bStation = new Station(2L, "B");
    private Station cStation = new Station(3L, "C");

    @BeforeEach
    void setUp() {
        sections = new Sections(new ArrayList<>(Arrays.asList(
                new Section(1L, line, aStation, bStation, 10),
                new Section(2L, line, bStation, cStation, 10))));
    }

    @DisplayName("구간 추가하는 기능")
    @Test
    void addSection() {
        //given
        Section newUpStationSection = new Section(1L, line, aStation, new Station(4L, "D"), 9);
        Section newUpStationSection2 = new Section(2L, line, cStation, new Station(5L, "G"), 11);
        Section newDownStationSection = new Section(3L, line, new Station(6L, "E"), cStation, 9);
        Section newDownStationSection2 = new Section(4L, line, new Station(7L, "F"), aStation, 11);
        //when
        sections.add(newUpStationSection);
        sections.add(newUpStationSection2);
        sections.add(newDownStationSection);
        sections.add(newDownStationSection2);
        //then
        assertThat(sections.sections()).hasSize(6);
        assertThat(sections.sections()).containsAll(Arrays.asList(newUpStationSection, newUpStationSection2, newDownStationSection, newDownStationSection2));
    }

    @DisplayName("추가할 수 없는 구간 및 거리 입력 검증")
    @Test
    void addError() {
        //given
        Section unConnectableSection = new Section(1L, line, new Station(4L, "D" ), new Station(5L, "E"), 8);
        Section inValidDistanceSection = new Section(1L, line, new Station(5L, "E"), cStation, 11);

        //then
        assertThatThrownBy(() -> sections.add(unConnectableSection))
                .isInstanceOf(SectionAdditionException.class)
                .hasMessage("추가될 수 없는 구간입니다.");
        assertThatThrownBy(() -> sections.add(inValidDistanceSection))
                .isInstanceOf(SectionAdditionException.class)
                .hasMessage("추가하는 구간의 거리가 더 짧아야합니다.");
    }
}