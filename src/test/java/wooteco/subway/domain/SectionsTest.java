package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

public class SectionsTest {

    @Test
    @DisplayName("상행 종점에 구간을 추가한다.")
    void addUpDestination() {
        //given
        Section section = new Section(new Station("역삼"), new Station("강남"), 5);
        Sections sections = new Sections(section);

        //when
        Section newSection = new Section(new Station("선릉"), new Station("역삼"), 5);
        sections.add(newSection);

        //then
        assertThat(sections.getUpDestination()).isEqualTo(new Station("선릉"));
    }

    @Test
    @DisplayName("하행 종점에 구간을 추가한다.")
    void addDownDestination() {
        //given
        Section section = new Section(new Station("역삼"), new Station("강남"), 5);
        Sections sections = new Sections(section);

        //when
        Section newSection = new Section(new Station("강남"), new Station("서초"), 6);
        sections.add(newSection);

        //then
        assertThat(sections.getDownDestination()).isEqualTo(new Station("서초"));
    }

    @Test
    @DisplayName("상행 종점 뒤에 새 구간을 추가한다.")
    void addSectionAfterUpDestination() {
        //given
        Section section = new Section(new Station("역삼"), new Station("강남"), 5);
        Sections sections = new Sections(section);

        //when
        Section newSection = new Section(new Station("역삼"), new Station("서초"), 3);
        sections.add(newSection);

        //then
        assertAll(
            () -> assertThat(sections.getValues().get(0)).isEqualTo(
                new Section(new Station("역삼"), new Station("서초"), 3)),
            () -> assertThat(sections.getValues().get(1)).isEqualTo(
                new Section(new Station("서초"), new Station("강남"), 2))
        );
    }

    @Test
    @DisplayName("하행 종점 앞에 새 구간을 추가한다.")
    void addSectionBeforeDownDestination() {
        //given
        Section section = new Section(new Station("역삼"), new Station("강남"), 5);
        Sections sections = new Sections(section);

        //when
        Section newSection = new Section(new Station("서초"), new Station("강남"), 3);
        sections.add(newSection);

        //then
        assertAll(
            () -> assertThat(sections.getValues().get(0)).isEqualTo(
                new Section(new Station("역삼"), new Station("서초"), 2)),
            () -> assertThat(sections.getValues().get(1)).isEqualTo(
                new Section(new Station("서초"), new Station("강남"), 3))
        );
    }

    @ParameterizedTest
    @ValueSource(ints = {5, 6})
    @DisplayName("추가하려는 구간 길이가 추가할 구간 사이 길이보다 크거나 같으면 예외를 던진다.")
    void addSectionWithOverDistanceException(int distance) {
        //given
        Section section = new Section(new Station("역삼"), new Station("강남"), 5);
        Sections sections = new Sections(section);

        //when
        Section newSection = new Section(new Station("역삼"), new Station("서초"), distance);

        //then
        assertThatThrownBy(() -> sections.add(newSection))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("거리는 0보다 작을 수 없습니다.");
    }

    @ParameterizedTest
    @CsvSource(value = {"역삼:강남", "강남:역삼", "강남:서초", "서초:강남"}, delimiter = ':')
    @DisplayName("등록하려는 구간의 두 역이 이미 존재할 경우 등록할 수 없다(한 구간과 모두 동일한 역을 가진 경우).")
    void addOverlapAllStationsInSection(String stationNameA, String stationNameB) {
        //given
        Section sectionA = new Section(new Station("역삼"), new Station("강남"), 5);
        Section sectionB = new Section(new Station("강남"), new Station("서초"), 5);
        Section sectionC = new Section(new Station("서초"), new Station("선릉"), 5);

        Sections sections = new Sections(sectionA);
        sections.add(sectionB);
        sections.add(sectionC);

        //when, then
        assertThatThrownBy(() -> sections.add(new Section(new Station(stationNameA), new Station(stationNameB), 4)))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @CsvSource(value = {"강남:선릉", "선릉:강남", "서초:선릉", "선릉:서초"}, delimiter = ':')
    @DisplayName("등록하려는 구간의 두 역이 이미 존재할 경우 등록할 수 없다(중간에 있는 두 역을 포함하는 경우).")
    void addOverlapTwoStationsInMiddle() {
        //given
        Section sectionA = new Section(new Station("역삼"), new Station("강남"), 5);
        Section sectionB = new Section(new Station("강남"), new Station("서초"), 5);
        Section sectionC = new Section(new Station("서초"), new Station("선릉"), 5);
        Section sectionD = new Section(new Station("선릉"), new Station("삼성"), 5);

        Sections sections = new Sections(sectionA);
        sections.add(sectionB);
        sections.add(sectionC);
        sections.add(sectionD);

        //when, then
        assertThatThrownBy(() -> sections.add(new Section(new Station("강남"), new Station("선릉"), 4)))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @CsvSource(value = {"역삼:선릉", "선릉:역삼"}, delimiter = ':')
    @DisplayName("등록하려는 구간의 두 역이 이미 존재할 경우 등록할 수 없다(상행 종점과 하행 종점 두 역을 포함하는 경우).")
    void addOverlapWithTwoDestinations(String stationNameA, String stationNameB) {
        //given
        Section sectionA = new Section(new Station("역삼"), new Station("강남"), 5);
        Section sectionB = new Section(new Station("강남"), new Station("서초"), 5);
        Section sectionC = new Section(new Station("서초"), new Station("선릉"), 5);

        Sections sections = new Sections(sectionA);
        sections.add(sectionB);
        sections.add(sectionC);

        //when, then
        assertThatThrownBy(() -> sections.add(new Section(new Station(stationNameA), new Station(stationNameB), 4)))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("구간이 한 개인 경우 삭제할 수 없다.")
    void deleteWithOneSection() {
        //given
        Section section = new Section(new Station("역삼"), new Station("강남"), 5);
        Sections sections = new Sections(section);

        //when, then
        assertThatThrownBy(() -> sections.delete(new Station("역삼")))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("구간이 하나인 경우 삭제할 수 없습니다.");
    }

    @Test
    @DisplayName("상행 종점을 삭제한다.")
    void deleteUpDestination() {
        //given
        Section sectionA = new Section(new Station("역삼"), new Station("강남"), 5);
        Section sectionB = new Section(new Station("강남"), new Station("서초"), 5);
        Section sectionC = new Section(new Station("서초"), new Station("선릉"), 5);

        Sections sections = new Sections(sectionA);
        sections.add(sectionB);
        sections.add(sectionC);

        //when
        sections.delete(new Station("역삼"));

        //then
        assertThat(sections.getUpDestination()).isEqualTo(new Station("강남"));
    }

    @Test
    @DisplayName("하행 종점을 삭제한다.")
    void deleteDownDestination() {
        //given
        Section sectionA = new Section(new Station("역삼"), new Station("강남"), 5);
        Section sectionB = new Section(new Station("강남"), new Station("서초"), 5);
        Section sectionC = new Section(new Station("서초"), new Station("선릉"), 5);

        Sections sections = new Sections(sectionA);
        sections.add(sectionB);
        sections.add(sectionC);

        //when
        sections.delete(new Station("선릉"));

        //then
        assertThat(sections.getDownDestination()).isEqualTo(new Station("서초"));
    }

    @Test
    @DisplayName("구간의 중간에 있는 역을 삭제한다.")
    void deleteStationInMiddle() {
        //given
        Section sectionA = new Section(new Station("역삼"), new Station("강남"), 5);
        Section sectionB = new Section(new Station("강남"), new Station("서초"), 5);
        Section sectionC = new Section(new Station("서초"), new Station("선릉"), 5);

        Sections sections = new Sections(sectionA);
        sections.add(sectionB);
        sections.add(sectionC);

        //when
        sections.delete(new Station("서초"));

        //then
        assertThat(sections.getValues()).isEqualTo(List.of(sectionA,
            new Section(new Station("강남"), new Station("선릉"), 10)
        ));
    }

    @Test
    @DisplayName("삭제할 역이 목록에 존재하지 않을 경우 예외를 던진다.")
    void deleteWithStationNotExists() {
        //given
        Section section = new Section(new Station("역삼"), new Station("강남"), 5);
        Sections sections = new Sections(section);

        //when, then
        assertThatThrownBy(() -> sections.delete(new Station("서초")))
            .isInstanceOf(IllegalArgumentException.class);
    }
}
