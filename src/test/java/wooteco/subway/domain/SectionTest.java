package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class SectionTest {

    @Test
    @DisplayName("상행 종점과 하행 종점이 같으면 예외를 반환한다.")
    void checkWhetherStationsAreDifferent() {
        assertThatThrownBy(() -> new Section(1L, "강남역", "강남역", 5))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("상행 종점과 하행 종점이 같을 수 없습니다.");
    }

    @Test
    @DisplayName("두 종점간의 거리가 0보다 작거나 같으면 예외를 반환한다.")
    void checkValidDistance_zero() {
        assertThatThrownBy(() -> new Section(1L, "강남역", "선릉역", 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("두 종점간의 거리는 0보다 커야합니다.");
    }

    @Test
    @DisplayName("두 종점간의 거리가 0보다 작거나 같으면 예외를 반환한다.")
    void checkValidDistance_negative() {
        assertThatThrownBy(() -> new Section(1L, "강남역", "선릉역", 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("두 종점간의 거리는 0보다 커야합니다.");
    }

    @Test
    @DisplayName("상행역이 일치하는 section을 갈래길이 생기지 않게 2개로 분리한다.")
    void splitSectionIfSameUpStation() {
        Section section = new Section(1L, "강남역", "잠실역", 8);

        List<Section> sections = section.splitSectionIfSameUpStation(new Station("선릉역"), 4);

        Section splitSection1 = new Section(1L, "강남역", "선릉역", 4);
        Section splitSection2 = new Section(1L, "선릉역", "잠실역", 4);
        assertThat(sections).isEqualTo(Arrays.asList(splitSection1, splitSection2));
    }

    @Test
    @DisplayName("하행역이 일치하는 section을 갈래길이 생기지 않게 2개로 분리한다.")
    void splitSectionIfSameDownStation() {
        Section section = new Section(1L, "강남역", "잠실역", 8);

        List<Section> sections = section.splitSectionIfSameDownStation(new Station("선릉역"), 4);

        Section splitSection1 = new Section(1L, "선릉역", "잠실역", 4);
        Section splitSection2 = new Section(1L, "강남역", "선릉역", 4);
        assertThat(sections).isEqualTo(Arrays.asList(splitSection1, splitSection2));
    }

    @Test
    @DisplayName("두 구간을 하나로 합친다.")
    void concatSections() {
        Section section1 = new Section(1L, "강남역", "선릉역", 4);
        Section section2 = new Section(1L, "선릉역", "잠실역", 4);

        Section section = section1.concatSections(section2);

        assertThat(section).isEqualTo(new Section(1L, "강남역", "잠실역", 8));
    }
}

