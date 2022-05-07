package wooteco.subway.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class SectionsTest {
    private final Section section1 = new Section(new Station("건대입구역"), new Station("잠실역"), 30);
    private final Section section2 = new Section(new Station("잠실역"), new Station("선릉역"), 20);
    private Sections sections;

    @BeforeEach
    void setUp() {
        sections = new Sections(List.of(section1, section2));
    }

    @Test
    @DisplayName("올바른 구간을 등록한다.")
    void saveSection() {
        final Section section = new Section(new Station("선릉역"), new Station("강남역"), 5);

        sections.add(section);

        assertThat(sections.hasSection(section)).isTrue();
    }

    @Test
    @DisplayName("상행역이 같은 구간을 등록할 때 구간을 쪼개서 등록한다.")
    void saveSectionBySameUpStation() {
        final Section section = new Section(new Station("건대입구역"), new Station("구의역"), 5);
        final Section expected = new Section(new Station("구의역"), new Station("잠실역"), 25);

        sections.add(section);

        assertThat(sections.hasSection(expected)).isTrue();
    }

    @Test
    @DisplayName("하행역이 같은 구간을 등록할 때 구간을 쪼개서 등록한다.")
    void saveSectionBySameDownStation() {
        final Section section = new Section(new Station("구의역"), new Station("잠실역"), 25);
        final Section expected = new Section(new Station("건대입구역"), new Station("구의역"), 5);

        sections.add(section);

        assertThat(sections.hasSection(expected)).isTrue();
    }

    @Test
    @DisplayName("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 예외를 발생시킨다.")
    void saveLongerSection() {
        final Section section = new Section(new Station("건대입구역"), new Station("강남역"), 55);

        assertThatThrownBy(() -> sections.add(section))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("존재하는 구간보다 긴 구간을 등록할 수 없습니다. 상행역, 하행역을 다시 설정해주세요.");
    }

    @ParameterizedTest
    @DisplayName("상행역과 하행역이 이미 노선에 존재할 경우 등록하려할 때 예외를 발생시킨다.")
    @CsvSource({"'잠실역', '선릉역', 20", "'건대입구역', '선릉역', 50", "'선릉역', '잠실역', 20"})
    void saveWhenSameStations(String upStation, String downStation, int distance) {
        final Section section = new Section(new Station(upStation), new Station(downStation), distance);

        assertThatThrownBy(() -> sections.add(section))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("상행역과 하행역이 이미 노선에 존재합니다.");
    }

    @Test
    @DisplayName("노선에 상행역과 하행역 둘 다 포함돼있지 않은 구간을 등록하려할 때 예외를 발생시킨다.")
    void saveWhenNotSameStations() {
        final Section section = new Section(new Station("구의역"), new Station("삼성역"), 40);

        assertThatThrownBy(() -> sections.add(section))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("등록하려는 구간의 상행역과 하행역 둘 중 하나는 노선에 포함된 역이어야 합니다.");
    }
}
