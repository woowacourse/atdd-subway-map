package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static wooteco.subway.Fixtures.SECTION;
import static wooteco.subway.Fixtures.SECTION_4;
import static wooteco.subway.Fixtures.STATION;
import static wooteco.subway.Fixtures.STATION_2;
import static wooteco.subway.Fixtures.STATION_3;
import static wooteco.subway.Fixtures.STATION_4;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SectionsTest {

    @DisplayName("역의 노선들을 순서대로 반환한다.")
    @Test
    void calculateStations() {
        Section Section = new Section(1L, 1L, STATION, STATION_2, 5);
        Section Section2 = new Section(3L, 1L, STATION_2, STATION_3, 10);
        Section Section3 = new Section(2L, 1L, STATION_3, STATION_4, 6);
        Sections sections = new Sections(List.of(Section2, Section, Section3));
        assertThat(sections.calculateStations())
                .containsOnly(STATION, STATION_2, STATION_3, STATION_4);
    }

    @DisplayName("해당 구간이 노선에 있는지 검증한다.")
    @Test
    void checkUniqueSection() {
        assertThatThrownBy(() -> Sections.forSave(List.of(SECTION), SECTION)).isInstanceOf(
                IllegalArgumentException.class).hasMessage("중복된 구간입니다.");
    }

    @DisplayName("해당 구간이 노선과 연결 가능한지 검증한다.")
    @Test
    void checkIsLinked() {
        assertDoesNotThrow(() -> Sections.forSave(List.of(), SECTION));
        assertThatThrownBy(() -> Sections.forSave(List.of(SECTION), SECTION_4)).isInstanceOf(
                IllegalArgumentException.class).hasMessage("해당 구간은 역과 연결될 수 없습니다.");
    }

    @DisplayName("해당 중간 역 양옆 역들을 이은 새로운 구간을 계산한다.")
    @Test
    void calculateCombinedSection() {
        Section section = new Section(1L, 1L, STATION, STATION_2, 5);
        Section section2 = new Section(3L, 1L, STATION_2, STATION_3, 10);
        Section section3 = new Section(2L, 1L, STATION_3, STATION_4, 6);
        Sections sections = new Sections(List.of(section2, section, section3));
        assertThat(sections.findCombinedLink(STATION_2))
                .isEqualTo(new Section(1L, STATION, STATION_3, 15));
    }

    @DisplayName("노선에서 해당 역을 포함한 모든 구간들을 찾는다.")
    @Test
    void findLinks() {
        Section section = new Section(1L, 1L, STATION, STATION_2, 5);
        Section section2 = new Section(2L, 1L, STATION_2, STATION_3, 10);
        Section section3 = new Section(3L, 1L, STATION_3, STATION_4, 6);
        Sections sections = new Sections(List.of(section2, section, section3));
        assertThat(sections.findLinks(STATION_2))
                .containsOnly(section, section2);
    }

    @DisplayName("노선에서 해당 역이 맨 끝일 경우 종점 구간을 찾는다.")
    @Test
    void findSide() {
        Section section = new Section(1L, 1L, STATION, STATION_2, 5);
        Section section2 = new Section(2L, 1L, STATION_2, STATION_3, 10);
        Section section3 = new Section(3L, 1L, STATION_3, STATION_4, 6);
        Sections sections = new Sections(List.of(section2, section, section3));
        assertThat(sections.findSide(STATION).orElseThrow())
                .isEqualTo(section.getId());
        assertThat(sections.findSide(STATION_2))
                .isEmpty();
    }

    @DisplayName("노선에서 해당 구간을 추가할 때 변경되는 구간 정보를 찾는다.")
    @Test
    void findMiddleResult() {
        Section section = new Section(1L, STATION, STATION_2, 5);
        Section section2 = new Section(1L, STATION_2, STATION_3, 10);
        Section section3 = new Section(1L, STATION_2, STATION_4, 6);
        Sections sections = new Sections(List.of(section, section2));
        assertThat(sections.findUpdateResult(section3).orElseThrow())
                .isEqualTo(section2.toRemain(section3));
    }
}
