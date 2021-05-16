package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.exception.InvalidSectionOnLineException;
import wooteco.subway.exception.NotFoundException;

class SectionsTest {

    private Sections sections;

    private final Line 칠호선 = new Line(7L, "7호선", "bg-green-100");
    private final Station 상봉역 = new Station(1L, "상봉역");
    private final Station 면목역 = new Station(2L, "면목역");
    private final Station 사가정역 = new Station(3L, "사가정역");
    private final Station 용마산역 = new Station(4L, "용마산역");
    private final Distance 거리 = new Distance(10);

    @BeforeEach
    void setUp() {
        Section section1 = new Section(칠호선, 상봉역, 면목역, 거리);
        Section section2 = new Section(칠호선, 면목역, 사가정역, 거리);
        Section section3 = new Section(칠호선, 사가정역, 용마산역, 거리);

        sections = new Sections(Arrays.asList(section1, section2, section3));
    }

    @Test
    @DisplayName("제공된 구간이 노선의 상/하행 끝 구간인지 확인")
    void isBothEndSection() {
        // given
        Section headSection = new Section(칠호선, 상봉역, 면목역, 거리);
        Section middleSection = new Section(칠호선, 면목역, 사가정역, 거리);
        Section tailSection = new Section(칠호선, 사가정역, 용마산역, 거리);

        // when
        boolean headSectionResult = sections.isBothEndSection(headSection);
        boolean middleSectionResult = sections.isBothEndSection(middleSection);
        boolean tailSectionResult = sections.isBothEndSection(tailSection);

        // then
        assertThat(headSectionResult).isTrue();
        assertThat(middleSectionResult).isFalse();
        assertThat(tailSectionResult).isTrue();
    }

    @Test
    @DisplayName("제공된 지하철 역이 노선의 상/하행 끝 역인지 확인")
    void isBothEndStation() {
        // given

        // when
        boolean headStationResult = sections.isBothEndStation(상봉역);
        boolean middleStationResult = sections.isBothEndStation(면목역);
        boolean tailStationResult = sections.isBothEndStation(용마산역);

        // then
        assertThat(headStationResult).isTrue();
        assertThat(middleStationResult).isFalse();
        assertThat(tailStationResult).isTrue();
    }

    @Test
    @DisplayName("정렬된 지하철 역 아이디 반환")
    void sortedStationIds() {
        // given
        Section 사가정_용마산 = new Section(칠호선, 사가정역, 용마산역, 거리);
        Section 상봉_면목 = new Section(칠호선, 상봉역, 면목역, 거리);
        Section 면목_사가정 = new Section(칠호선, 면목역, 사가정역, 거리);

        sections = new Sections(Arrays.asList(사가정_용마산, 상봉_면목, 면목_사가정));

        // when
        Deque<Station> 오름차순_정렬된_역목록 = sections.sortedStations();

        // then
        assertThat(오름차순_정렬된_역목록).containsExactly(상봉역, 면목역, 사가정역, 용마산역);
    }


    @Test
    @DisplayName("이미 등록되어 있는 구간이거나, 구간 등록을 위한 역이 1개도 없는지 확인")
    void validateInsertable() {
        // given
        Station 새로운역1 = new Station(5L, "새로운역1");
        Station 새로운역2 = new Station(6L, "새로운역2");
        Section 이미_양역이_함께_구간으로_등록 = new Section(칠호선, 상봉역, 면목역, 거리);
        Section 구간으로_등록된_역이_없음 = new Section(칠호선, 새로운역1, 새로운역2, 거리);

        // when

        // then
        assertThatThrownBy(() -> sections.validateInsertable(이미_양역이_함께_구간으로_등록))
            .isInstanceOf(InvalidSectionOnLineException.class);
        assertThatThrownBy(() -> sections.validateInsertable(구간으로_등록된_역이_없음))
            .isInstanceOf(InvalidSectionOnLineException.class);
    }

    @Test
    @DisplayName("구간을 제거할 수 있는지 검증")
    void validateDeletableCount() {
        // given
        Sections sections = new Sections(Collections.singletonList(
            new Section(칠호선, 상봉역, 면목역, 거리)
        ));

        // when

        // then
        assertThatThrownBy(sections::validateDeletableCount)
            .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("현재 노선에 지하철 역이 존재하는지 검증")
    void validateExistStation() {
        // given
        Station 새로운역 = new Station(9L, "새로운역");

        // when

        // then
        assertThatThrownBy(() -> sections.validateExistStation(새로운역))
            .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("현재 노선이 비어있는지 확인")
    void isNotEmpty() {
        // given
        Sections emptySections = new Sections(new ArrayList<>());

        // when
        boolean notEmpty = this.sections.isNotEmpty();
        boolean empty = emptySections.isNotEmpty();

        // then
        assertThat(notEmpty).isTrue();
        assertThat(empty).isFalse();
    }

    @Test
    @DisplayName("현재 노선에서 지하철 상/하행 역이 일치하는 구간을 찾아서 반환")
    void findByStationId() {
        // given
        Section section = new Section(칠호선, 상봉역, 면목역, 거리);

        // when
        Section foundSection = sections.findByMatchStation(section);

        // then
        assertThat(foundSection.hasSameStationBySection(section)).isTrue();
        assertThat(foundSection.hasSameStationBySection(section)).isTrue();
    }

    @Test
    @DisplayName("현재 노선에서 지하철 상/하행 역이 일치하는 구간이 없을 경우 예외처리")
    void findByStationIdException() {
        // given
        Station 새로운역1 = new Station(5L, "새로운역1");
        Station 새로운역2 = new Station(6L, "새로운역2");
        Section invalidSection = new Section(칠호선, 새로운역1, 새로운역2, 거리);

        // when

        // then
        assertThatThrownBy(() -> sections.findByMatchStation(invalidSection))
            .isInstanceOf(InvalidSectionOnLineException.class);
    }
}