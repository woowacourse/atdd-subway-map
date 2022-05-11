package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SectionsTest {

    private Sections sections;
    private Section section1, section2, section3;

    @BeforeEach
    void setUp() {
        section1 = new Section(1L, 1L, 2L, 10);
        section2 = new Section(1L, 2L, 3L, 10);
        section3 = new Section(1L, 5L, 1L, 10);
        sections = new Sections(List.of(section1, section2, section3));
    }

    @Test
    @DisplayName("노선이 상행에서 하행 순으로 정렬된다.")
    void arrangeFromUpToDown() {
        assertThat(sections.getValue())
                .extracting("upStationId")
                .containsExactly(5L, 1L, 2L);
    }

    @Test
    @DisplayName("구간을 등록할 때 상행과 하행 중 하나라도 존재하지 않으면 예외가 발생한다.")
    void checkStationExist() {
        assertThatThrownBy(() -> sections.add(new Section(1L, 100L, 101L, 10)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("구간 등록은 노선에 존재하는 상행역과 하행역 중 하나를 포함하고 있어야 합니다.");
    }

    @Test
    @DisplayName("구간을 등록할 때 이미 존재하는 노선이면 예외가 발생한다.")
    void checkDuplicateSection() {
        assertThatThrownBy(() -> sections.add(new Section(1L, 1L, 2L, 5)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("해당 경로가 이미 존재합니다.");
    }

    @Test
    @DisplayName("구간 사이에 구간을 추가할 때 기존 거리보다 거리가 길거나 같으면 예외가 발생한다.")
    void validateDistance() {
        assertThatThrownBy(() -> sections.add(new Section(1L, 2L, 4L, 10)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("추가하고자 하는 구간의 길이는 기존의 구간보다 짧아야 합니다.");
    }

    @Test
    @DisplayName("상행 종점에 역을 추가한다.")
    void addEndOfUpStation() {
        sections.add(new Section(1L, 6L, 5L, 10));
        assertThat(sections.getValue())
                .extracting("upStationId")
                .containsExactly(6L, 5L, 1L, 2L);
    }

    @Test
    @DisplayName("하행 종점에 역을 추가한다.")
    void addEndOfDownStation() {
        sections.add(new Section(1L, 3L, 4L, 10));
        assertThat(sections.getValue())
                .extracting("upStationId")
                .containsExactly(5L, 1L, 2L, 3L);
    }

    @Test
    @DisplayName("구간의 중간에 상행이 같은 역을 추가한다")
    void addBetweenWithUpStation() {
        sections.add(new Section(1L, 2L, 4L, 5));
        assertThat(sections.getValue())
                .extracting("upStationId", "distance")
                .containsExactly(tuple(5L, 10), tuple(1L, 10), tuple(2L, 5), tuple(4L, 5));
    }

    @Test
    @DisplayName("구간의 중간에 하행이 같은 역을 추가한다")
    void addBetweenWithDownStation() {
        sections.add(new Section(1L, 4L, 3L, 5));
        assertThat(sections.getValue())
                .extracting("upStationId", "distance")
                .containsExactly(tuple(5L, 10), tuple(1L, 10), tuple(2L, 5), tuple(4L, 5));
    }
}
