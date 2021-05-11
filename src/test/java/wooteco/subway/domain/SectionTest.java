package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class SectionTest {

    private Section section;

    @BeforeEach
    void setUp() {
        section = new Section(
            1L, new Line("2호선", "bg-red-600"), new Station(1L, "왕십리역"),
            new Station(2L, "답삽리역"), new Distance(3));
    }

    @DisplayName("ID가 같으면 같은 구간으로 취급한다.")
    @Test
    void equals() {
        assertThat(section).isEqualTo(new Section(
            1L, new Line("3호선", "bg-blue-600"), new Station(3L, "서울역"),
            new Station(4L, "시청역"), new Distance(4)));
    }

    @DisplayName("ID가 다르면 다른 구간으로 취급한다.")
    @Test
    void notEquals() {
        assertThat(section).isNotEqualTo(new Section(
            2L, new Line("2호선", "bg-red-600"), new Station(1L, "왕십리역"),
            new Station(2L, "답삽리역"), new Distance(3)));
    }

    @DisplayName("구간의 상행 역과 하행 역이 같으면 예외처리 된다.")
    @Test
    void createSectionWithSameUpStationAndDownStation() {
        assertThatIllegalArgumentException()
            .isThrownBy(() -> new Section(
                new Line("1호선", "bg-red-600"), new Station(1L, "왕십리역"),
                new Station(1L, "왕십리역"), new Distance(3)))
            .withMessage("상행역과 하행역이 같을 수 없습니다.");
        assertThatIllegalArgumentException()
            .isThrownBy(() -> new Section(
                1L, new Line("1호선", "bg-red-600"), new Station(1L, "왕십리역"),
                new Station(1L, "왕십리역"), new Distance(3)))
            .withMessage("상행역과 하행역이 같을 수 없습니다.");
    }

    @DisplayName("상행역 또는 하행역에 같은 역이 하나만 있는지 확인한다.")
    @ParameterizedTest
    @CsvSource(value = {"3, 4, false", "1, 3, true", "3, 2, true",
        "1, 2, false", "2, 3, false"})
    void hasOnlyOneSameStation(Long stationId1, Long stationId2, boolean expected) {
        // when
        Section sectionToCompare = new Section(new Line("1호선", "bg-blue-600"),
            new Station(stationId1, ""), new Station(stationId2, ""), new Distance(3));

        // then
        assertThat(section.hasOnlyOneSameStation(sectionToCompare)).isEqualTo(expected);
    }

    @DisplayName("상행역 또는 하행역으로 해당 역을 가지고 있는지 확인한다.")
    @ParameterizedTest
    @CsvSource(value = {"1, true", "2, true", "3, false"})
    void hasStation(Long stationId, boolean expected) {
        // when
        Station stationToCompare = new Station(stationId, "");

        // then
        assertThat(section.hasStation(stationToCompare)).isEqualTo(expected);
    }

    @DisplayName("상행선 또는 하행역에 같은 역을 반환한다.")
    @ParameterizedTest
    @CsvSource(value = {"1, 3, 1", "3, 2, 2"})
    void sameStation(Long stationId1, Long stationId2, Long expected) {
        // when
        Section sectionToCompare = new Section(new Line("1호선", "bg-blue-600"),
            new Station(stationId1, ""), new Station(stationId2, ""), new Distance(3));

        // then
        assertThat(section.sameStation(sectionToCompare).getId()).isEqualTo(expected);
    }

    @DisplayName("상행선 또는 하행역에 같은 역을 반환한다.(존재하지 않으면 예외처리된다.)")
    @ParameterizedTest
    @CsvSource(value = {"3, 4", "2, 3"})
    void sameStationWithInvalidSection(Long stationId1, Long stationId2) {
        // when
        Section sectionToCompare = new Section(new Line("1호선", "bg-blue-600"),
            new Station(stationId1, ""), new Station(stationId2, ""), new Distance(3));

        // then
        assertThatIllegalArgumentException()
            .isThrownBy(() -> section.sameStation(sectionToCompare))
            .withMessage("같은 역이 존재하지 않습니다.");
    }
}
