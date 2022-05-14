package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class SectionsTest {

    @Test
    @DisplayName("기존 구간의 거리보다 큰 거리의 구간을 추가하면 예외를 반환한다.")
    void validateDistance_longer() {
        Section section1 = new Section(1L, "강남역", "선릉역", 4);
        Section section2 = new Section(1L, "선릉역", "잠실역", 4);
        Sections sections = new Sections(Arrays.asList(section1, section2));

        assertThatThrownBy(() -> sections.validateDistance(10))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("기존 구간의 거리보다 크거나 같은 구간은 추가할 수 없습니다.");
    }

    @Test
    @DisplayName("기존 구간의 거리와 같은 거리의 구간을 추가하면 예외를 반환한다.")
    void validateDistance_same() {
        Section section1 = new Section(1L, "강남역", "선릉역", 4);
        Section section2 = new Section(1L, "선릉역", "잠실역", 4);
        Sections sections = new Sections(Arrays.asList(section1, section2));

        assertThatThrownBy(() -> sections.validateDistance(8))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("기존 구간의 거리보다 크거나 같은 구간은 추가할 수 없습니다.");
    }

    @Test
    @DisplayName("이미 존재하는 구간을 추가하면 에외를 반환한다.")
    void checkBothExist() {
        Section section = new Section(1L, "선릉역", "잠실역", 4);
        Sections sections = new Sections(List.of(section));

        assertThatThrownBy(() -> sections.validateStations(new Station("선릉역"), new Station("잠실역")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미 존재하는 구간입니다.");
    }

    @Test
    @DisplayName("상행역과 하행역이 모두 없는 구간을 추가하면 예외를 반환한다.")
    void checkBothDoNotExist() {
        Section section = new Section(1L, "선릉역", "잠실역", 4);
        Sections sections = new Sections(List.of(section));

        assertThatThrownBy(() -> sections.validateStations(new Station("강남역"), new Station("역삼역")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("상행역과 하행역 모두 존재하지 않습니다.");
    }

    @Test
    @DisplayName("상행역이 일치하는 section을 갈래길이 생기지 않게 2개로 분리한다.")
    void splitSectionIfSameUpStation() {
        Section section = new Section(1L, "강남역", "잠실역", 8);
        Sections sections = new Sections(List.of(section));

        Sections updatedSections = sections.updateSection(new Station("강남역"), new Station("선릉역"), 4);

        Section section1 = new Section(1L, "강남역", "선릉역", 4);
        Section section2 = new Section(1L, "선릉역", "잠실역", 4);
        assertThat(updatedSections).isEqualTo(new Sections(Arrays.asList(section1, section2)));
    }

    @Test
    @DisplayName("하행역이 일치하는 section을 갈래길이 생기지 않게 2개로 분리한다.")
    void splitSectionIfSameDownStation() {
        Section section = new Section(1L, "강남역", "잠실역", 8);
        Sections sections = new Sections(List.of(section));

        Sections updatedSections = sections.updateSection(new Station("선릉역"), new Station("잠실역"), 4);

        Section section1 = new Section(1L, "선릉역", "잠실역", 4);
        Section section2 = new Section(1L, "강남역", "선릉역", 4);
        assertThat(updatedSections).isEqualTo(new Sections(Arrays.asList(section1, section2)));
    }

    @Test
    @DisplayName("두 구간을 하나로 합친다.")
    void concatSections() {
        Section section1 = new Section(1L, "강남역", "선릉역", 4);
        Section section2 = new Section(1L, "선릉역", "잠실역", 4);
        Sections sections = new Sections(Arrays.asList(section1, section2));

        Sections deletedSections = sections.deleteSection(new Station("선릉역"));

        Section section = new Section(1L, "강남역", "잠실역", 8);
        assertThat(deletedSections).isEqualTo(new Sections(List.of(section)));
    }
}
