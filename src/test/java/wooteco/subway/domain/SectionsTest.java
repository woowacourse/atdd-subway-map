package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.exception.InvalidSectionOnLineException;
import wooteco.subway.exception.NotFoundException;

class SectionsTest {

    private Sections sections;

    @BeforeEach
    void setUp() {
        Section section1 = new Section(1L, 1L, 2L, 10);
        Section section2 = new Section(1L, 2L, 3L, 10);
        Section section3 = new Section(1L, 3L, 4L, 10);

        sections = new Sections(Arrays.asList(section1, section2, section3));
    }

    @Test
    @DisplayName("제공된 구간이 노선의 상/하행 끝 구간인지 확인")
    void isBothEndSection() {
        // given
        Section upHeadSection = new Section(1L, 1L, 2L, 10);
        Section upMiddleSection = new Section(1L, 2L, 3L, 10);
        Section upTailSection = new Section(1L, 3L, 4L, 10);

        Section downHeadSection = new Section(1L, 4L, 3L, 10);
        Section downMiddleSection = new Section(1L, 3L, 2L, 10);
        Section downTailSection = new Section(1L, 2L, 1L, 10);

        // when
        boolean upHeadSectionResult = sections.isBothEndSection(upHeadSection);
        boolean upMiddleSectionResult = sections.isBothEndSection(upMiddleSection);
        boolean upTailSectionResult = sections.isBothEndSection(upTailSection);

        boolean downHeadSectionResult = sections.isBothEndSection(downHeadSection);
        boolean downMiddleSectionResult = sections.isBothEndSection(downMiddleSection);
        boolean downTailSectionResult = sections.isBothEndSection(downTailSection);

        // then
        assertThat(upHeadSectionResult).isTrue();
        assertThat(upMiddleSectionResult).isFalse();
        assertThat(upTailSectionResult).isTrue();

        assertThat(downHeadSectionResult).isTrue();
        assertThat(downMiddleSectionResult).isFalse();
        assertThat(downTailSectionResult).isTrue();
    }

    @Test
    @DisplayName("제공된 지하철 역이 노선의 상/하행 끝 역인지 확인")
    void isBothEndStation() {
        // given
        Long headStationId = 1L;
        Long middleStationId = 2L;
        Long tailStationId = 4L;

        // when
        boolean headStationResult = sections.isBothEndStation(headStationId);
        boolean middleStationResult = sections.isBothEndStation(middleStationId);
        boolean tailStationResult = sections.isBothEndStation(tailStationId);

        // then
        assertThat(headStationResult).isTrue();
        assertThat(middleStationResult).isFalse();
        assertThat(tailStationResult).isTrue();
    }

    @Test
    @DisplayName("정렬된 지하철 역 아이디 반환")
    void sortedStationIds() {
        // given
        List<Section> legacySections = Arrays.asList(
            new Section(1L, 3L, 4L, 10),
            new Section(1L, 1L, 2L, 10),
            new Section(1L, 2L, 3L, 10)
        );

        sections = new Sections(legacySections);

        // when
        Deque<Long> sortedStationIds = sections.sortedStationIds();

        // then
        assertThat(sortedStationIds).containsExactly(1L, 2L, 3L, 4L);
    }


    @Test
    @DisplayName("이미 등록되어 있는 구간이거나, 구간 등록을 위한 역이 1개도 없는지 확인")
    void validateInsertable() {
        // given
        Section alreadyExistSection = new Section(1L, 1L, 2L, 10);
        Section nonExistSection = new Section(1L, 5L, 6L, 10);

        // when

        // then
        assertThatThrownBy(() -> sections.validateInsertable(alreadyExistSection))
            .isInstanceOf(InvalidSectionOnLineException.class);
        assertThatThrownBy(() -> sections.validateInsertable(nonExistSection))
            .isInstanceOf(InvalidSectionOnLineException.class);
    }

    @Test
    @DisplayName("구간을 제거할 수 있는지 검증")
    void validateDeletableCount() {
        // given
        Sections sections = new Sections(Collections.singletonList(
            new Section(1L, 1L, 2L, 10)
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
        Long nonExistStationId = 9L;

        // when

        // then
        assertThatThrownBy(() -> sections.validateExistStation(nonExistStationId))
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
        Section section = new Section(1L, 1L, 2L, 10);

        // when
        Section foundSection = sections.findByStationId(section);

        // then
        assertThat(foundSection.hasSameStationBySection(section)).isTrue();
        assertThat(foundSection.hasSameStationBySection(section)).isTrue();
    }

    @Test
    @DisplayName("현재 노선에서 지하철 상/하행 역이 일치하는 구간이 없을 경우 예외처리")
    void findByStationIdException() {
        // given
        Section invalidSection = new Section(1L, 51L, 9L, 10);

        // when

        // then
        assertThatThrownBy(() -> sections.findByStationId(invalidSection))
            .isInstanceOf(InvalidSectionOnLineException.class);
    }
}