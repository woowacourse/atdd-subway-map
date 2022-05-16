package wooteco.subway.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.domain.section.*;
import wooteco.subway.domain.station.Station;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class SectionsTest {

    private Sections sections;

    @BeforeEach
    void setSections(){
        List<Section> sectionList = new ArrayList<>();
        sectionList.add(new Section(1L, 1L, 1L, 2L, 10));

        sections = new Sections(sectionList, new ConcreteCreationStrategy(), new ConcreteDeletionStrategy(), new ConcreteSortStrategy());
    }

    @Test
    @DisplayName("섹션을 성공적으로 생성")
    void saveSection(){
        Section section = new Section(2L, 1L, 2L, 3L, 10);
        sections.save(section);

        assertTrue(sections.getStationIds().contains(3L));
    }

    @Test
    @DisplayName("상행역과 하행역이 모두 존재하는 경우에 대한 예외처리")
    void checkExistence(){
        Section section = new Section(2L, 1L, 1L, 2L, 15);

        assertThatThrownBy(()->sections.save(section))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("상행역과 하행역이 이미 모두 존재합니다.");
    }

    @Test
    @DisplayName("노선에 상행역과 하행역이 모두 존재하지 않는 경우에 대한 예외처리")
    void checkConnected(){
        Section section = new Section(2L, 1L, 3L, 4L, 15);

        assertThatThrownBy(()->sections.save(section))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("기존 노선과 연결된 구간이 아닙니다.");

    }

    @Test
    @DisplayName("갈래길의 길이가 기존 구간보다 긴 경우에 대한 예외처리")
    void checkDistance(){
        Section section = new Section(2L, 1L, 1L, 3L, 15);

        assertThatThrownBy(()->sections.save(section))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("적절한 거리가 아닙니다.");
    }

    @Test
    @DisplayName("갈래길이 발생하지 않으면 Optional.empty를 반환.")
    void overLappedSectionNotExist(){
        Section section = new Section(2L, 1L, 2L, 3L, 10);

        assertTrue(sections.fixOverLappedSection(section).isEmpty());
    }

    @Test
    @DisplayName("갈래길이 발생하면 수정된 기존 구간을 반환.")
    void fixOverLappedSection(){
        Section section = new Section(2L, 1L, 1L, 3L, 10);

        assertTrue(sections.fixOverLappedSection(section).isPresent());
    }

    @Test
    @DisplayName("섹션 제거")
    void deleteSection(){
        Section section = new Section(2L, 1L, 2L, 3L, 10);
        sections.save(section);
        sections.delete(1L, 2L);

        assertFalse(sections.getStationIds().contains(2L));
    }

    @Test
    @DisplayName("제거하려는 구간이 노선의 유일한 구간인 경우에 대한 예외처리")
    void checkDelete(){
        assertThatThrownBy(()->sections.delete(1L, 2L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("노선의 유일한 구간은 삭제할 수 없습니다.");
    }

    @Test
    @DisplayName("구간 제거로 인해 끊긴 구간이 없으면 Optional.empty를 반환")
    void disconnectedSectionNotExist(){
        Section section = new Section(2L, 1L, 2L, 3L, 10);
        sections.save(section);

        assertTrue(sections.fixDisconnectedSection(1L, 3L).isEmpty());
    }

    @Test
    @DisplayName("구간 제거로 인해 끊긴 구간이 있으면 수정된 구간을 반환")
    void fixDisconnectedSection(){
        Section section = new Section(2L, 1L, 2L, 3L, 10);
        sections.save(section);

        assertTrue(sections.fixDisconnectedSection(1L, 2L).isPresent());
    }

    @Test
    @DisplayName("역 정렬")
    void SortStations(){

        List<Station> stations = new ArrayList<>(List.of(new Station(1L, "강남역"), new Station(2L, "역삼역"), new Station(3L, "선릉역")));
        Section section = new Section(2L, 1L, 3L, 1L, 5);

        sections.save(section);
        sections.fixOverLappedSection(section);

        List<Station> sortedStations = sections.sort(stations);

        assertThat(sortedStations.get(0).getName()).isEqualTo("선릉역");
        assertThat(sortedStations.get(1).getName()).isEqualTo("강남역");
        assertThat(sortedStations.get(2).getName()).isEqualTo("역삼역");
    }
}
