package wooteco.subway.section.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.exception.NotFoundException;
import wooteco.subway.exception.SectionAdditionException;
import wooteco.subway.line.model.Line;
import wooteco.subway.station.model.Station;

public class SectionsTest {

    private Sections sections;

    private Line line = new Line(1L, "2호선", "green");

    private Station aStation = new Station(1L, "A");
    private Station bStation = new Station(2L, "B");
    private Station cStation = new Station(3L, "C");

    private Section abSection = new Section(line, aStation, bStation, 10);
    private Section bcSection = new Section(line, bStation, cStation, 10);

    @BeforeEach
    void setUp() {
        sections = new Sections(new ArrayList<>(Arrays.asList(
            abSection, bcSection)));
    }

    @DisplayName("구간 추가하는 기능")
    @Test
    void addSection() {
        //given
        Section newUpStationSection = new Section(line, aStation, new Station(4L, "D"), 9);
        Section newUpStationSection2 = new Section(line, cStation, new Station(5L, "G"), 11);
        Section newDownStationSection = new Section(line, new Station(6L, "E"), cStation, 9);
        Section newDownStationSection2 = new Section(line, new Station(7L, "F"), aStation, 11);

        //when
        sections.add(newUpStationSection);
        sections.add(newUpStationSection2);
        sections.add(newDownStationSection);
        sections.add(newDownStationSection2);

        //then
        assertThat(sections.sections()).hasSize(6);
        assertThat(sections.sections()).containsAll(Arrays.asList(newUpStationSection,
            newUpStationSection2, newDownStationSection, newDownStationSection2));
    }

    @DisplayName("추가할 수 없는 구간 및 거리 입력 검증")
    @Test
    void addError() {
        //given
        Section unConnectableSection = new Section(line, new Station(4L, "D"), new Station(5L, "E"),
            8);
        Section inValidDistanceSection = new Section(line, new Station(5L, "E"), cStation, 11);

        //then
        assertThatThrownBy(() -> sections.add(unConnectableSection))
            .isInstanceOf(SectionAdditionException.class)
            .hasMessage("추가될 수 없는 구간입니다.");
        assertThatThrownBy(() -> sections.add(inValidDistanceSection))
            .isInstanceOf(SectionAdditionException.class)
            .hasMessage("추가하는 구간의 거리가 더 짧아야합니다.");
    }

    @DisplayName("구간 삭제하는 기능")
    @Test
    void deleteSection() {
        //when
        sections.delete(bStation.getId());
        //then
        assertThat(sections.sections()).hasSize(1);
        assertThat(sections.sections()).contains(abSection.merge(bcSection));
    }

    @DisplayName("구간 삭제 요청 시 존재하지 않는 역일 때 예외처리")
    @Test
    void name() {
        //given
        Long notExistId = -1L;

        //then
        assertThatThrownBy(() -> sections.delete(notExistId))
            .isInstanceOf(NotFoundException.class)
            .hasMessage("노선 내 존재하는 역이 없습니다.");
    }

}
