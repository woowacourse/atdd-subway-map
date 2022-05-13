package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class SectionsTest {

    private Sections sections;

    @BeforeEach
    void setUp() {
        sections = new Sections(List.of(new Section(1L, 1L, 1L, 2L, 7)));
        sections.addSection(new Section(2L, 1L, 2L, 3L, 6));
        sections.addSection(new Section(3L, 1L, 3L, 5L, 7));
    }

    @DisplayName("Section의 상행선과 하행선이 둘다 일치할 때 예외가 발생한다.")
    @ParameterizedTest
    @CsvSource(value = {"1L, 2L", "2L, 1L", "2L, 3L", "3L, 2L", "3L, 5L", "5L, 3L"})
    void validateSameSection() {
        assertThatThrownBy(() -> sections.addSection(new Section(1L, 1L, 2L, 5)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("동일한 Section은 추가할 수 없습니다.");
    }

    @DisplayName("Section의 상행선과 하행선이 현재 노선에 포함되지 않으면 예외가 발생한다.")
    @Test
    void validateNotExistStation() {
        assertThatThrownBy(() -> sections.addSection(new Section(1L, 13L, 14L, 5)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("상행역, 하행역 둘 다 포함되지 않으면 추가할 수 없습니다.");
    }

    @DisplayName("Section이 추가될 때 기존 구간의 거리보다 멀면 예외가 발생한다.")
    @Test
    void validateDistance() {
        assertThatThrownBy(() -> sections.addSection(new Section(1L, 1L, 3L, 7)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("새로 들어오는 구간의 거리가 추가될 구간보다 작아야 합니다.");
    }

    @DisplayName("가장 하행선에서 오른쪽에 구간을 추가하는 경우")
    @Test
    void addSectionRight() {
        Optional<Section> addPoint = sections.addSection(new Section(4L, 1L, 5L, 7L, 5));

        assertThat(addPoint.get()).isEqualTo(new Section(1L, 3L, 5L, 7));
    }

    @DisplayName("가장 하행선에서 왼쪽에 구간을 추가하는 경우")
    @Test
    void addSectionBetweenRight() {
        Optional<Section> addPoint = sections.addSection(new Section(4L, 1L, 4L, 5L, 5));

        assertThat(addPoint.get()).isEqualTo(new Section(1L, 3L, 4L, 7));
    }

    @DisplayName("가장 상행선에서 왼쪽에 구간을 추가하는 경우")
    @Test
    void addSectionLight() {
        Optional<Section> addPoint = sections.addSection(new Section(4L, 1L, 7L, 1L, 3));

        assertThat(addPoint.get()).isEqualTo(new Section(1L, 1L, 2L, 7));
    }

    @DisplayName("가장 상행선에서 오른쪽에 구간을 추가하는 경우")
    @Test
    void addSectionLightRight() {
        Optional<Section> addPoint = sections.addSection(new Section(4L, 1L, 1L, 7L, 4));

        assertThat(addPoint.get()).isEqualTo(new Section(1L, 7L, 2L, 3));
    }

    @DisplayName("상행선 하행선이 아닌 지점에서 오른쪽에 구간을 추가하는 경우")
    @Test
    void addCenterRight() {
        Optional<Section> addPoint = sections.addSection(new Section(4L, 1L, 2L, 7L, 3));

        assertThat(addPoint.get()).isEqualTo(new Section(1L, 7L, 3L, 3));
    }

    @DisplayName("상행선 하행선이 아닌 지점에서 왼쪽에 구간을 추가하는 경우")
    @Test
    void addCenterLeft() {
        Optional<Section> addPoint = sections.addSection(new Section(4L, 1L, 7L, 2L, 4));

        assertThat(addPoint.get()).isEqualTo(new Section(1L, 1L, 7L, 3));
    }

    @DisplayName("상행선을 삭제하는 경우")
    @Test
    void deleteUpStation() {
        sections.deleteSection(1L);
        assertThat(sections.getSections()).containsExactly(new Section(2L, 2L, 3L, 6), new Section(3L, 3L, 5L, 7));
    }

    @DisplayName("하행선을 삭제하는 경우")
    @Test
    void deleteDownStation() {
        sections.deleteSection(5L);
        assertThat(sections.getSections()).containsExactly(new Section(1L, 1L, 2L, 7), new Section(2L, 2L, 3L, 6));
    }

    @DisplayName("최상행역, 최하행역이 아닌 가운데 지점 하행선-상행선을 지우는 경우")
    @Test
    void deleteCenterStation() {
        sections.deleteSection(2L);
        assertThat(sections.getSections()).containsExactly(new Section(1L, 3L, 5L, 7), new Section(1L, 1L, 3L, 13));
    }

    @DisplayName("노선에 역이 2개 존재하는데 지우려는 경우 예외가 발생한다.")
    @Test
    void deleteException() {
        sections.deleteSection(2L);
        sections.deleteSection(3L);

        assertThatThrownBy(() -> sections.deleteSection(5L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("노선에 역이 2개 존재하는 경우는 삭제할 수 없습니다.");
    }

    @DisplayName("구간 내부의 지하철역을 정렬한다.")
    @Test
    void sortSection() {
        Section section1 = new Section(1L, 1L, 1L, 2L, 7);
        Section section2 = new Section(4L, 1L, 5L, 4L, 3);
        Section section3 = new Section(2L, 1L, 2L, 3L, 6);
        Section section4 = new Section(3L, 1L, 3L, 5L, 7);
        List<Section> unsortedSections
                = List.of(section1, section2, section3, section4);

        Sections sections = new Sections(unsortedSections);
        List<Long> stationIds = sections.sortSection();
        assertThat(stationIds).containsExactly(1L, 2L, 3L, 5L, 4L);
    }
}
