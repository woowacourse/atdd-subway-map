package wooteco.subway.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

public class SectionsTest {

    private Sections sections;

    @BeforeEach
    void setUp() {
        sections = new Sections(new Section(0, 1L, 1L, 2L, 7));
    }

    @Test
    @DisplayName("상행 종점인 구간을 추가한다")
    void insertSectionUpStations() {
        sections.add(new Section(0, 1L, 3L, 1L, 5));

        assertThat(sections.getStations().size()).isEqualTo(3);
    }

    @Test
    @DisplayName("하행 종점인 구간을 추가한다")
    void insertSectionDownStations() {
        sections.add(new Section(0, 1L, 2L, 3L, 5));

        assertThat(sections.getStations().size()).isEqualTo(3);
    }

    @Test
    @DisplayName("갈래길을 방지한다.")
    void insertSectionWithFork() {
        sections.add(new Section(0, 1L, 1L, 3L, 5));
        Section inserted = getSectionByUpStationId(1L);
        Section separated = getSectionByUpStationId(3L);

        assertAll(
                () -> assertThat(sections.getStations().size()).isEqualTo(3),
                () -> assertThat(inserted.getDistance()).isEqualTo(5),
                () -> assertThat(separated.getDistance()).isEqualTo(2)
        );
    }

    private Section getSectionByUpStationId(Long sectionId) {
        for (Section section : sections.getSections()) {
            if (section.isSameUpStationId(sectionId)) {
                return section;
            }
        }
        return null;
    }

    @Test
    @DisplayName("상행이 같은 길이면서 기존 길보다 길이보다 긴 길이 들어오는 경우 예외 처리를 한다.")
    void insertUpSectionWithForkExceedDistance() {
        assertThatThrownBy(
                () -> sections.add(new Section(0, 1L, 1L, 3L, 10))
        ).isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("하행이 같은 길이면서 기존 길보다 길이보다 긴 길이 들어오는 경우 예외 처리를 한다.")
    void insertDownSectionWithForkExceedDistance() {
        assertThatThrownBy(
                () -> sections.add(new Section(0, 1L, 3L, 2L, 10))
        ).isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("노선을 추가할 때 상행과 하행 모두 기존 노선에 존재하지 않는 역을 예외를 발생시킨다")
    void insertSectionWithExclude() {
        assertThatThrownBy(
                () -> sections.add(new Section(0, 1L, 10L, 11L, 10))
        ).isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("노선을 추가할 때 상행과 하행 모두 기존 노선에 존재하는 경우 예외를 발생시킨다")
    void insertSectionWithInclude() {
        assertThatThrownBy(
                () -> sections.add(new Section(0, 1L, 2L, 1L, 10))
        ).isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("section이 1개만 있는 경우 삭제할 수 없다.")
    void removeTest() {
        assertThatThrownBy(
                () -> sections.remove(1L)
        ).isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("역을 삭제하는 경우, 상행과 하행에 모두 걸려 있다면 합쳐준다.")
    void removeUpAndDownSection() {
        sections.add(new Section(0, 1L, 2L, 3L, 5));
        sections.remove(2L);

        assertAll(
                () -> assertThat(sections.getSectionContainsStation(1L).get(0).getDownStationId()).isEqualTo(3),
                () -> assertThat(sections.getSectionContainsStation(1L).get(0).getDistance()).isEqualTo(12)
        );
    }
}

