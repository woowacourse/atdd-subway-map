package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import wooteco.subway.domain.exception.UnmergeableException;
import wooteco.subway.domain.exception.UnsplittableException;

public class SectionTest {

    @DisplayName("상행역과 동일한 경우 구간 분리")
    @Test
    void splitByUpStation() {
        Section section1 = new Section(1L, 1L, new SectionEdge(1L, 2L, 10));
        Section section2 = new Section(2L, 1L, new SectionEdge(1L, 3L, 6));

        Section newSection = section1.split(section2);
        SectionEdge newEdge = newSection.getEdge();

        assertThat(newSection.getLineId()).isEqualTo(1L);
        assertThat(newEdge.getUpStationId()).isEqualTo(3L);
        assertThat(newEdge.getDownStationId()).isEqualTo(2L);
        assertThat(newEdge.getDistance()).isEqualTo(4);
    }

    @DisplayName("하행역과 동일한 경우 구간 분리")
    @Test
    void splitByDownStation() {
        Section section1 = new Section(1L, 1L, new SectionEdge(1L, 2L, 10));
        Section section2 = new Section(2L, 1L, new SectionEdge(3L, 2L, 7));

        Section newSection = section1.split(section2);
        SectionEdge newEdge = newSection.getEdge();

        assertThat(newSection.getLineId()).isEqualTo(1L);
        assertThat(newEdge.getUpStationId()).isEqualTo(1L);
        assertThat(newEdge.getDownStationId()).isEqualTo(3L);
        assertThat(newEdge.getDistance()).isEqualTo(3);
    }

    @DisplayName("거리가 더 긴 구간으로 분리 시 예외 발생")
    @Test
    void splitByLongerDistanceSection() {
        Section section1 = new Section(1L, 1L, new SectionEdge(1L, 2L, 10));
        Section section2 = new Section(2L, 1L, new SectionEdge(3L, 2L, 10));
        assertThatThrownBy(() -> section1.split(section2))
            .isInstanceOf(UnsplittableException.class);
    }

    @DisplayName("겹치지 않는 구간으로 분리 시 예외 발생")
    @Test
    void splitByNotOverlapSection() {
        Section section1 = new Section(1L, 1L, new SectionEdge(1L, 2L, 10));
        Section section2 = new Section(2L, 1L, new SectionEdge(3L, 4L, 5));
        assertThatThrownBy(() -> section1.split(section2))
            .isInstanceOf(UnsplittableException.class);
    }

    @DisplayName("동일하지 않는 노선의 구간으로 분리 시 예외 발생")
    @Test
    void splitByNotSameLine() {
        Section section1 = new Section(1L, 1L, new SectionEdge(1L, 2L, 10));
        Section section2 = new Section(2L, 2L, new SectionEdge(1L, 3L, 5));
        assertThatThrownBy(() -> section1.split(section2))
            .isInstanceOf(UnsplittableException.class);
    }

    @DisplayName("동일한 상행, 하행역으로 구간 분리 시 예외 발생")
    @ParameterizedTest
    @CsvSource({"1,2,1,2", "1,2,2,1"})
    void splitBySameUpAndDownStation(long upStationId1, long downStationId1,
                                     long upStationId2, long downStationId2) {
        Section section1 = new Section(1L, 1L, new SectionEdge(upStationId1, downStationId1, 10));
        Section section2 = new Section(2L, 1L, new SectionEdge(upStationId2, downStationId2, 5));

        assertThatThrownBy(() -> section1.split(section2))
            .isInstanceOf(UnsplittableException.class);
    }

    @DisplayName("동일하지 않는 노선의 구간 병합 시 예외 발생")
    @Test
    void mergeByNotSameLine() {
        Section section1 = new Section(1L, 1L, new SectionEdge(1L, 2L, 10));
        Section section2 = new Section(2L, 2L, new SectionEdge(1L, 3L, 5));
        assertThatThrownBy(() -> section1.merge(section2))
            .isInstanceOf(UnmergeableException.class);
    }

    @DisplayName("동일한 상행, 하행역으로 구간 병합 시 예외 발생")
    @ParameterizedTest
    @CsvSource({"1,2,1,2", "1,2,2,1"})
    void mergeBySameUpAndDownStation(long upStationId1, long downStationId1,
                                     long upStationId2, long downStationId2) {
        Section section1 = new Section(1L, 1L, new SectionEdge(upStationId1, downStationId1, 10));
        Section section2 = new Section(2L, 1L, new SectionEdge(upStationId2, downStationId2, 5));

        assertThatThrownBy(() -> section1.merge(section2))
            .isInstanceOf(UnmergeableException.class);
    }

    @DisplayName("겹치는 구간이 있는 경우 병합")
    @ParameterizedTest
    @CsvSource({"1,2,2,3,1,3", "1,2,3,1,3,2"})
    void mergeByOverlapSection(long upStationId1, long downStationId1,
                               long upStationId2, long downStationId2,
                               long expectUpStationId, long expectDownStationId) {
        Section section1 = new Section(1L, 1L, new SectionEdge(upStationId1, downStationId1, 10));
        Section section2 = new Section(2L, 1L, new SectionEdge(upStationId2, downStationId2, 5));

        Section newSection = section1.merge(section2);
        SectionEdge newEdge = newSection.getEdge();

        assertThat(newSection.getLineId()).isEqualTo(1L);
        assertThat(newEdge.getUpStationId()).isEqualTo(expectUpStationId);
        assertThat(newEdge.getDownStationId()).isEqualTo(expectDownStationId);
        assertThat(newEdge.getDistance()).isEqualTo(15);
    }

    @DisplayName("겹치지 않는 구간 병합 시 예외 발생")
    @Test
    void mergeByNotOverlapSection() {
        Section section1 = new Section(1L, 1L, new SectionEdge(1L, 2L, 10));
        Section section2 = new Section(2L, 1L, new SectionEdge(3L, 4L, 5));
        assertThatThrownBy(() -> section1.merge(section2))
            .isInstanceOf(UnmergeableException.class);
    }

}
