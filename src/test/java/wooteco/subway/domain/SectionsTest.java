package wooteco.subway.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Sections 도메인 객체 테스트")
class SectionsTest {

    private final Section section1 = new Section(1L, 2L, 10);
    private final Section section2 = new Section(2L, 3L, 10);
    private final Section section3 = new Section(3L, 4L, 10);

    private final Sections sections = new Sections(List.of(section1, section2, section3));

    @ParameterizedTest
    @ValueSource(longs = {2L, 3L, 4L})
    @DisplayName("상행역과 하행역이 구간 목록에 존재할 경우 예외가 발생한다.")
    void addAlreadyExistUpAndDownStation(long downStationId) {
        // given
        Section newSection = new Section(1L, downStationId, 10);

        // when & then
        assertThatThrownBy(() -> sections.add(newSection))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("상행역과 하행역이 이미 지하철 노선에 존재합니다.");
    }

    @DisplayName("추가하려는 구간의 지하철역들이 구간 목록에 없을 경우 예외가 발생한다.")
    @Test
    void addNotExistUpAndDownStation() {
        // given
        Section newSection = new Section(5L, 6L, 10);

        // when & then
        assertThatThrownBy(() -> sections.add(newSection))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("추가하려는 구간이 노선에 포함되어 있지 않습니다.");
    }

    @DisplayName("상행 종점을 추가한다.")
    @Test
    void addEndUpStation() {
        // given
        Section newSection = new Section(4L, 5L, 10);

        // when
        sections.add(newSection);

        // then
        assertThat(sections.getSections()).contains(newSection);
    }

    @DisplayName("하행 종점을 추가한다.")
    @Test
    void addEndDownStation() {
        // given
        Section newSection = new Section(9L, 1L, 10);

        // when
        sections.add(newSection);

        // then
        assertThat(sections.getSections()).contains(newSection);
    }

    @DisplayName("상행역을 추가한다.")
    @Test
    void addUpStation() {
        // given
        Section newSection = new Section(9L, 2L, 5);

        // when
        sections.add(newSection);

        // then
        assertAll(
                () -> assertThat(sections.getSections()).contains(new Section(1L, 9L, 5)),
                () -> assertThat(sections.getSections()).contains(new Section(9L, 2L, 5))
        );
    }

    @DisplayName("하행역을 추가한다.")
    @Test
    void addDownStation() {
        // given
        Section newSection = new Section(1L, 9L, 5);

        // when
        sections.add(newSection);

        // then
        assertAll(
                () -> assertThat(sections.getSections()).contains(new Section(1L, 9L, 5)),
                () -> assertThat(sections.getSections()).contains(new Section(9L, 2L, 5))
        );
    }

    @DisplayName("구간을 삭제할 경우 노선에 구간이 하나만 존재할 경우 예외가 발생한다.")
    @Test
    void deleteOnlyOneSection() {
        // given
        Sections sections = new Sections(List.of(new Section(1L, 2L, 10)));

        // when & then
        assertThatThrownBy(() -> sections.delete(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("노선에 구간이 하나만 존재하기 때문에 삭제할 수 없습니다.");
    }

    @ParameterizedTest
    @ValueSource(longs = {1L, 4L})
    @DisplayName("종점을 삭제한다.")
    void deleteLastUpStation(long stationId) {
        // when
        sections.delete(stationId);

        // then
        assertThat(sections.getSections()).hasSize(2);
    }

    @DisplayName("중간역을 삭제한다.")
    @Test
    void deleteStationInSection() {
        // when
        sections.delete(2L);

        // then
        assertThat(sections.getSections()).contains(new Section(1L, 3L, 20));
    }
}
