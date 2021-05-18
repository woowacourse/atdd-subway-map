package wooteco.subway.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.domain.section.Section;
import wooteco.subway.domain.section.Sections;
import wooteco.subway.domain.station.Station;
import wooteco.subway.exception.SubwayException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SectionsTest {
    private final List<Section> unOrderedSections = new ArrayList<>();
    private Station gangnam;
    private Station jamsil;
    private Station seocho;
    private Station sadang;
    private Sections sections;

    @BeforeEach
    void setUp() {
        gangnam = new Station(1L, "강남역");
        jamsil = new Station(2L, "잠실역");
        seocho = new Station(3L, "서초역");
        sadang = new Station(4L, "사당역");

        Section section1 = new Section(gangnam, jamsil, 3);
        Section section2 = new Section(jamsil, seocho, 5);
        Section section3 = new Section(seocho, sadang, 7);

        unOrderedSections.addAll(Arrays.asList(section2, section3, section1));
        sections = new Sections(unOrderedSections);
    }

    @DisplayName("정렬된 Station들을 반환한다.")
    @Test
    void getStationsTest() {
        List<Station> stations = sections.getStations();
        assertThat(stations).containsExactly(
                new Station(1L, "강남역"),
                new Station(2L, "잠실역"),
                new Station(3L, "서초역"),
                new Station(4L, "사당역")
        );
    }

    @DisplayName("상행 종점 구간을 반환한다.")
    @Test
    void getTopSectionTest() {
        Section topSection = sections.getTopSection();
        assertThat(topSection).isEqualTo(new Section(gangnam, jamsil, 3));
    }

    @DisplayName("하행 종점 구간을 반환한다.")
    @Test
    void getBottomSectionTest() {
        Section topSection = sections.getBottomSection();
        assertThat(topSection).isEqualTo(new Section(seocho, sadang, 7));
    }

    @DisplayName("구간 추가 시 구간에 포함된 두 역 모두 이미 노선에 존재할 경우 예외가 발생한다.")
    @Test
    void validateIfAlreadyExistsInLineTest() {
        Section section = new Section(gangnam, sadang, 10);
        Throwable exception = assertThrows(
                SubwayException.class,
                () -> sections.validateIfPossibleToInsert(section)
        );
        assertThat(exception.getMessage()).isEqualTo("두 역이 이미 노선에 등록되어 있습니다.");
    }

    @DisplayName("구간 추가 시 구간에 포함된 두 역 모두 노선에 존재하지 않을 경우 예외가 발생한다.")
    @Test
    void validateIfBothStationNotExistsInLineTest() {
        Station yeonsu = new Station("yeonsu");
        Station woninjae = new Station("woninjae");
        Section section = new Section(yeonsu, woninjae, 10);
        assertThatThrownBy(() -> sections.validateIfPossibleToInsert(section))
                .isInstanceOf(SubwayException.class)
                .hasMessage("노선에 역들이 존재하지 않습니다.");
    }

    @DisplayName("구간 추가 시 구간에 포함 된 두 역 중 하나만 노선에 존재할 경우 예외가 발생하지 않는다.")
    @Test
    void validateWhenOneStationExistsSuccessTest() {
        Station yeonsu = new Station(5L, "yeonsu");
        Section section = new Section(seocho, yeonsu, 3);
        assertDoesNotThrow(() -> sections.validateIfPossibleToInsert(section));
    }

    @DisplayName("구간 사이에 구간 추가 시 추가되는 구간의 거리가 더 크거나 같은 경우 예외가 발생한다.")
    @Test
    void validateDistanceFailTest() {
        Station yeonsu = new Station(5L, "yeonsu");
        Section section = new Section(seocho, yeonsu, 7);
        assertThatThrownBy(() -> sections.validateIfPossibleToInsert(section))
                .isInstanceOf(SubwayException.class)
                .hasMessage("추가되는 구간의 거리는 기존 구간보다 클 수 없습니다.");
    }

    @DisplayName("구간 사이에 구간 추가 시 추가되는 구간의 거리가 작은 경우 예외가 발생하지 않는다.")
    @Test
    void validateDistanceSuccessTest() {
        Station yeonsu = new Station(5L, "yeonsu");
        Section section = new Section(seocho, yeonsu, 6);
        assertDoesNotThrow(() -> sections.validateIfPossibleToInsert(section));
    }

    @DisplayName("구간 추가 시 새로 등록되는 역이 하행역으로 등록되는지 확인한다. - True")
    @Test
    void isNewStationDownwardTrueTest() {
        Station yeonsu = new Station(5L, "yeonsu");
        Section section = new Section(seocho, yeonsu, 3);
        assertThat(sections.isNewStationDownward(section)).isTrue();
    }

    @DisplayName("구간 추가 시 새로 등록되는 역이 하행역으로 등록되는지 확인한다. - False")
    @Test
    void isNewStationDownwardFalseTest() {
        Station yeonsu = new Station(5L, "yeonsu");
        Section section = new Section(yeonsu, seocho, 3);
                assertThat(sections.isNewStationDownward(section)).isFalse();
    }

    @DisplayName("구간 삭제 시 구간이 하나 뿐일 때 예외가 발생한다.")
    @Test
    void isNewStationDownwardTest() {
        Station yeonsu = new Station(5L, "yeonsu");
        Section section = new Section(seocho, yeonsu, 7);
        Sections sections = new Sections(Collections.singletonList(section));
        assertThatThrownBy(sections::validateIfPossibleToDelete)
                .isInstanceOf(SubwayException.class)
                .hasMessage("구간이 하나뿐이므로 삭제 불가능합니다.");
    }

    @DisplayName("중간 구간 삭제 시 양 옆 구간이 통합된다.")
    @Test
    void createMergedSectionAfterDeletionTest() {
        Section mergedSection = sections.createMergedSectionAfterDeletion(2L);
        assertThat(mergedSection).isEqualTo(new Section(gangnam, seocho, 8));
    }

    @DisplayName("id에 해당하는 역을 상행역으로 가지고 있는 Section이 있는지 여부를 반환한다.")
    @Test
    void hasStationAsUpwardTest() {
        assertThat(sections.hasStationAsUpward(1L)).isTrue();
        assertThat(sections.hasStationAsUpward(2L)).isTrue();
        assertThat(sections.hasStationAsUpward(3L)).isTrue();
        assertThat(sections.hasStationAsUpward(4L)).isFalse();
    }

    @DisplayName("id에 해당하는 역을 하행역으로 가지고 있는 Section이 있는지 여부를 반환한다.")
    @Test
    void hasStationAsDownwardTest() {
        assertThat(sections.hasStationAsDownward(1L)).isFalse();
        assertThat(sections.hasStationAsDownward(2L)).isTrue();
        assertThat(sections.hasStationAsDownward(3L)).isTrue();
        assertThat(sections.hasStationAsDownward(4L)).isTrue();
    }
}
