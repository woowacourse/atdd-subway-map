package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static wooteco.subway.domain.Sections.DUPLICATE_STATION_ERROR_MESSAGE;
import static wooteco.subway.domain.Sections.IMPOSSIBLE_ADDING_EXCEPTION_MESSAGE;
import static wooteco.subway.domain.Sections.IMPOSSIBLE_DELETE_EXCEPTION_MESSAGE;
import static wooteco.subway.domain.Sections.NONE_DUPLICATE_STATION_ERROR_MESSAGE;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class SectionsTest {

    private Sections sections;

    @BeforeEach
    void beforeEach() {
        Section section1 = new Section(1L, 1L, 2L, 10);
        Section section2 = new Section(1L, 2L, 3L, 10);
        sections = new Sections(new ArrayList<>(List.of(section1, section2)));
    }

    @Test
    @DisplayName("상행역과 하행역 모두 라인에 존재하지 않는 경우에는 에러가 발생한다.")
    void addThrowDuplicateException1() {
        assertThatThrownBy(() ->
                sections.add(new Section(1L, 4L, 5L, 5)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(NONE_DUPLICATE_STATION_ERROR_MESSAGE);
    }

    @Test
    @DisplayName("상행역과 하행역 모두 이미 라인에 등록되어 있으면 에러가 발생한다.")
    void addThrowDuplicateException2() {
        assertThatThrownBy(() ->
                sections.add(new Section(1L, 1L, 2L, 5)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(DUPLICATE_STATION_ERROR_MESSAGE);
    }

    @ParameterizedTest
    @CsvSource(value = {"4,1", "3,4", "2,4", "4,3"})
    @DisplayName("상행역과 하행역을 추가할 수 있다.")
    void add(Long upStationId, Long downStationId) {
        Sections newSections = sections.add(new Section(1L, upStationId, downStationId, 5));
        assertThat(newSections.getValue().size()).isEqualTo(3);
    }

    @Test
    @DisplayName("거리가 기존의 노선사이의 거리보다 긴 노선은 추가할 수 없다.")
    void addThrowExceptionWithDistance() {
        assertThatThrownBy(() -> sections.add(new Section(1L, 2L, 4L, 10)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(IMPOSSIBLE_ADDING_EXCEPTION_MESSAGE);
    }

    @Test
    @DisplayName("존재하지 않는 구간은 삭제할 수 없다.")
    void deleteSectionThrowException1() {
        assertThatThrownBy(() -> sections.delete(1L, 4L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(IMPOSSIBLE_DELETE_EXCEPTION_MESSAGE);
    }

    @Test
    @DisplayName("구간이 1개 밖에 없으면 삭제할 수 없다.")
    void deleteSectionThrowException2() {
        Section section = new Section(1L, 1L, 2L, 10);
        Sections sections = new Sections(new ArrayList<>(List.of(section)));

        assertThatThrownBy(() -> sections.delete(1L, 1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(IMPOSSIBLE_DELETE_EXCEPTION_MESSAGE);
    }

    @Test
    @DisplayName("종점 구간의 역은 삭제할 수 있다.")
    void deleteSection1() {
        Sections newSections = sections.delete(1L, 3L);
        assertThat(newSections.getValue().size()).isEqualTo(1);

    }

    @Test
    @DisplayName("중간에 있는 구역의 역은 삭제할 수 있다.")
    void deleteSection2() {
        Sections newSections = sections.delete(1l, 2L);
        assertThat(newSections.getValue().size()).isEqualTo(1);
    }
}
