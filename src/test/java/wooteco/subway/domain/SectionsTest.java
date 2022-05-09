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
    private final Section section1 = new Section(1L, 2L, 30);
    private final Section section2 = new Section(2L, 3L, 20);
    private final Section section3 = new Section(3L, 4L, 5);
    private Sections sections;

    @BeforeEach
    void setUp() {
        sections = new Sections(List.of(section1, section2, section3));
    }

    @Test
    @DisplayName("상행역 종점 구간을 등록한다.")
    void saveUpStationSection() {
        final Section section = new Section(5L, 1L, 20);

        sections.add(section);

        assertThat(sections.hasSection(section)).isTrue();
    }

    @Test
    @DisplayName("하행역 종점 구간을 등록한다.")
    void saveDownStationSection() {
        final Section section = new Section(4L, 6L, 30);

        sections.add(section);

        assertThat(sections.hasSection(section)).isTrue();
    }

    @Test
    @DisplayName("상행역이 같은 구간을 등록할 때 구간을 쪼개서 등록한다.")
    void saveSectionBySameUpStation() {
        final Section section = new Section(1L, 5L, 5);
        final Section expected = new Section(5L, 2L, 25);

        sections.add(section);

        assertThat(sections.hasSection(expected)).isTrue();
    }

    @Test
    @DisplayName("하행역이 같은 구간을 등록할 때 구간을 쪼개서 등록한다.")
    void saveSectionBySameDownStation() {
        final Section section = new Section(5L, 2L, 25);
        final Section expected = new Section(1L, 5L, 5);

        sections.add(section);

        assertThat(sections.hasSection(expected)).isTrue();
    }

    @Test
    @DisplayName("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 예외를 발생시킨다.")
    void saveLongerSection() {
        final Section section = new Section(1L, 7L, 35);

        assertThatThrownBy(() -> sections.add(section))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("존재하는 구간보다 긴 구간을 등록할 수 없습니다. 상행역, 하행역을 다시 설정해주세요.");
    }

    @ParameterizedTest
    @DisplayName("상행역과 하행역이 이미 노선에 존재할 경우 등록하려할 때 예외를 발생시킨다.")
    @CsvSource({"2, 3, 20", "1, 3, 50", "3, 2, 20", "4, 2, 25"})
    void saveWhenSameStations(Long upStationId, Long downStationId, int distance) {
        final Section section = new Section(upStationId, downStationId, distance);

        assertThatThrownBy(() -> sections.add(section))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("상행역과 하행역이 이미 노선에 존재합니다.");
    }

    @Test
    @DisplayName("노선에 상행역과 하행역 둘 다 포함돼있지 않은 구간을 등록하려할 때 예외를 발생시킨다.")
    void saveWhenNotSameStations() {
        final Section section = new Section(5L, 6L, 40);

        assertThatThrownBy(() -> sections.add(section))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("등록하려는 구간의 상행역과 하행역 둘 중 하나는 노선에 포함된 역이어야 합니다.");
    }

    @Test
    @DisplayName("상행역 종점을 제거한다.")
    void deleteFinalUpStation() {
        sections.delete(new Station(1L));

        assertThat(sections.hasSection(section1)).isFalse();
    }

    @Test
    @DisplayName("하행역 종점을 제거한다.")
    void deleteFinalDownStation() {
        sections.delete(new Station(4L));

        assertThat(sections.hasSection(section3)).isFalse();
    }

    @Test
    @DisplayName("중간역을 제거한다.")
    void deleteMiddleStation() {
        final Section section =
                new Section(2L, 4L, 25);

        sections.delete(new Station(3L));

        assertThat(sections.hasSection(section)).isTrue();
    }

    @Test
    @DisplayName("존재하지 않는 역을 제거하려 할 때 예외를 발생시킨다.")
    void deleteNotExistSection() {
        assertThatThrownBy(() -> sections.delete(new Station(5L)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("존재하지 않는 역입니다.");
    }

    @Test
    @DisplayName("구간이 하나인 노선에서 역을 제거할 때 예외를 발생시킨다.")
    void deleteWhenOnlyOneSection() {
        sections.delete(new Station(3L));
        sections.delete(new Station(2L));

        assertThatThrownBy(() -> sections.delete(new Station(4L)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("구간이 하나만 존재하는 노선입니다.");
    }
}
