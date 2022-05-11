package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import wooteco.subway.exception.section.DuplicatedSectionException;
import wooteco.subway.exception.section.LongerSectionDistanceException;
import wooteco.subway.exception.section.NonExistenceStationsSectionException;
import wooteco.subway.exception.section.OnlySectionDeletionException;

class SectionsTest {

    private final Long lineId = 1L;
    private final Section first = new Section(1L, lineId, 2L, 3L, 8);
    private final Section second = new Section(2L, lineId, 3L, 4L, 8);
    private final Sections sections = new Sections(new ArrayList<>(Arrays.asList(second, first)));

    @DisplayName("이미 존재하는 역들로 이루어진 구간을 추가하려고 하면 예외를 발생시킨다.")
    @ParameterizedTest
    @CsvSource({"2, 4", "4, 2"})
    void add_exceptionByDuplicatedStations(Long upStationId, Long downStationId) {
        Section invalidSection = new Section(3L, lineId, upStationId, downStationId, 8);

        assertThatThrownBy(() -> sections.connect(invalidSection))
                .isInstanceOf(DuplicatedSectionException.class);
    }

    @Test
    @DisplayName("구간들에 존재하지 않는 역으로 이루어진 구간을 추가하려고 하면 예외를 발생시킨다.")
    void add_exceptionByNonExistenceStations() {
        Section invalidSection = new Section(3L, lineId, 1L, 5L, 8);

        assertThatThrownBy(() -> sections.connect(invalidSection))
                .isInstanceOf(NonExistenceStationsSectionException.class);
    }

    @DisplayName("구간 사이에 새로운 구간을 추가하려고 하는데 새 구간의 거리가 크거가 같으면 예외를 발생시킨다.")
    @ParameterizedTest
    @CsvSource({"2, 5, 8", "5, 4, 8"})
    void add_exception(Long upStationId, Long downStationId, int distance) {
        Section invalidSection = new Section(3L, lineId, upStationId, downStationId, distance);

        assertThatThrownBy(() -> sections.connect(invalidSection))
                .isInstanceOf(LongerSectionDistanceException.class);
    }

    @DisplayName("구간 사이에 새로운 구간을 추가할 수 있다.")
    @ParameterizedTest
    @CsvSource({"2, 5, 7", "5, 4, 7"})
    void add_between_success(Long upStationId, Long downStationId, int distance) {
        Section invalidSection = new Section(3L, lineId, upStationId, downStationId, distance);

        assertThatCode(() -> sections.connect(invalidSection))
                .doesNotThrowAnyException();
    }

    @DisplayName("전체 구간의 앞 혹은 뒤에 새로운 구간을 추가할 수 있다.")
    @ParameterizedTest
    @CsvSource({"1, 2, 8", "4, 5, 8"})
    void add_backOrForth(Long upStationId, Long downStationId, int distance) {
        Section invalidSection = new Section(3L, lineId, upStationId, downStationId, distance);

        assertThatCode(() -> sections.connect(invalidSection))
                .doesNotThrowAnyException();
    }

    @DisplayName("새로운 구간을 앞 혹은 뒤에 추가할 수 있다.")
    @ParameterizedTest
    @CsvSource({"1, 2", "4, 5"})
    void addBackOrForth(Long upStationId, Long downStationId) {
        Section newSection = new Section(3L, 1L, upStationId, downStationId, 3);

        assertThatCode(() -> sections.connect(newSection))
                .doesNotThrowAnyException();
    }

    @DisplayName("다른 Sections를 받아, 자신과 다른 Section들을 반환한다.")
    @Test
    void findDifferentSections() {
        Section third = new Section(3L, lineId, 4L, 5L, 8);
        Sections another = new Sections(Arrays.asList(second, third));

        assertThat(sections.findDifferentSections(another)).isEqualTo(Collections.singletonList(first));
    }

    @DisplayName("동일한 상행선을 기준으로 구간 사이에 새로운 구간을 추가할 수 있다.")
    @Test
    void addBetweenBasedOnUpStation() {
        Section newSection = new Section(3L, 1L, 2L, 5L, 2);
        Section changedSectionByNewSection = new Section(3L, 1L, 5L, 3L, 6);
        Sections expected = new Sections(Arrays.asList(changedSectionByNewSection, second, newSection));

        Sections updated = sections.connect(newSection);

        assertThat(updated).isEqualTo(expected);
    }

    @DisplayName("동일한 하행선을 기준으로 구간 사이에 새로운 구간을 추가할 수 있다.")
    @Test
    void addBetweenBasedOnDownStation() {
        Section newSection = new Section(3L, 1L, 5L, 4L, 2);
        Section changedSectionByNewSection = new Section(3L, 1L, 3L, 5L, 6);
        Sections expected = new Sections(Arrays.asList(first, changedSectionByNewSection, newSection));

        final Sections updated = sections.connect(newSection);

        assertThat(updated).isEqualTo(expected);
    }

    @DisplayName("상행역의 id를 반환한다.")
    @Test
    void findUpStationId() {
        Long upStationId = sections.findUpStationId();

        assertThat(upStationId).isEqualTo(2L);
    }

    @DisplayName("하행역의 id를 반환한다.")
    @Test
    void findDownStationId() {
        Long upStationId = sections.findDownStationId();

        assertThat(upStationId).isEqualTo(4L);
    }

    @DisplayName("구간이 하나뿐인데 구간을 삭제하려고 하면 예외를 발생시킨다.")
    @Test
    void delete_exceptionByOnlySection() {
        Sections sections = new Sections(Collections.singletonList(first));
        assertThatThrownBy(() -> sections.delete(2L))
                .isInstanceOf(OnlySectionDeletionException.class);
    }

    @DisplayName("하행역 혹은 상행역을 삭제할 수 있다.")
    @Test
    void deleteForth() {
        Sections deleted = sections.delete(2L);

        assertThat(deleted).isEqualTo(new Sections(Collections.singletonList(second)));
    }

    @DisplayName("상행역을 삭제할 수 있다.")
    @Test
    void deleteBack() {
        Sections deleted = sections.delete(4L);

        assertThat(deleted).isEqualTo(new Sections(Collections.singletonList(first)));
    }

    @DisplayName("구간들 사이에 있는 역을 삭제할 수 있다.")
    @Test
    void deleteBetween() {
        Sections deleted = sections.delete(3L);
        Section combinedSection = new Section(1L, 2L, 4L, 16);

        assertThat(deleted).isEqualTo(new Sections(Collections.singletonList(combinedSection)));
    }
}
