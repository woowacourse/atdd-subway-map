package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.exception.DuplicateException;

class SectionTest {

    @Test
    @DisplayName("중복된 지하철역이 입력되었을 시 예외처리")
    public void validateDuplicatedStation() {
        // given
        Long upStationId = 1L;
        Long downStationId = 1L;
        Long lineId = 1L;
        int distance = 1;

        // when

        // then
        assertThatThrownBy(() -> new Section(lineId, upStationId, downStationId, distance))
            .isInstanceOf(DuplicateException.class);
    }

    @Test
    @DisplayName("구간 등록을 위해 기존 연결 지하철 역과 거리가 달라진 새로운 구간 생성")
    void updateForSave() {
        // given
        Section section = new Section(1L, 1L, 2L, 10);
        Section newSection = new Section(1L, 1L, 3L, 5);

        // when
        Section updatedSection = section.updateForSave(newSection);

        // then
        assertThat(updatedSection.getUpStationId()).isEqualTo(3L);
        assertThat(updatedSection.getDownStationId()).isEqualTo(2L);
        assertThat(updatedSection.getDistance()).isEqualTo(5);
    }

    @Test
    @DisplayName("구간 삭제를 위해 기존 연결 지하철 역과 거리가 달라진 새로운 구간 생성")
    void updateForDelete() {
        // given
        Section section = new Section(1L, 1L, 2L, 10);
        Section newSection = new Section(1L, 2L, 3L, 5);

        // when
        Section updatedSection = section.updateForDelete(newSection);

        // then
        assertThat(updatedSection.getUpStationId()).isEqualTo(1L);
        assertThat(updatedSection.getDownStationId()).isEqualTo(3L);
        assertThat(updatedSection.getDistance()).isEqualTo(15);
    }

    @Test
    @DisplayName("구간과 비교했을 때 서로 일치하는 지하철 상행/하행역을 가졌는지 확인")
    void hasSameStation() {
        // given
        Section section = new Section(1L, 1L, 2L, 10);
        Section compareSection1 = new Section(1L, 4L, 2L, 20);
        Section compareSection2 = new Section(1L, 4L, 3L, 20);

        // when
        boolean hasSameStation = section.hasSameStationBySection(compareSection1);
        boolean noHasSameStation = section.hasSameStationBySection(compareSection2);

        // then
        assertThat(hasSameStation).isTrue();
        assertThat(noHasSameStation).isFalse();
    }
}