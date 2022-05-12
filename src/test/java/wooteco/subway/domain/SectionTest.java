package wooteco.subway.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import wooteco.subway.dto.section.SectionRequest;
import wooteco.subway.exception.BusinessException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SectionTest {

    @Test
    @DisplayName("상행종점과 하행종점이 동일한 경우")
    void sameUpDown() {
        assertThatThrownBy(() -> Section.of(1L, new SectionRequest(1L, 1L, 5))).isInstanceOf(BusinessException.class).hasMessageContaining("상행종점과 하행종점은 같은 지하철역일 수 없습니다.");
    }

    @Test
    @DisplayName("거리가 1미만의 정수인 경우")
    void wrongDistance() {
        assertThatThrownBy(() -> Section.of(1L, new SectionRequest(1L, 2L, 0))).isInstanceOf(BusinessException.class).hasMessageContaining("거리는 1이상의 정수만 허용됩니다.");
    }

    @Test
    @DisplayName("upStationId와 같다면 true 반환")
    void isSameUpStationWhenTrue() {
        Section section = new Section(1L, 1L, 1L, 2L, 5);

        assertThat(section.isSameUp(1L)).isTrue();
    }

    @Test
    @DisplayName("upStationId와 다르다면 false 반환")
    void isSameUpStationWhenFalse() {
        Section section = new Section(1L, 1L, 1L, 2L, 5);

        assertThat(section.isSameUp(3L)).isFalse();
    }

    @Test
    @DisplayName("downStationId와 같다면 true 반환")
    void isSameDownStationWhenTrue() {
        Section section = new Section(1L, 1L, 1L, 2L, 5);

        assertThat(section.isSameDown(2L)).isTrue();
    }

    @Test
    @DisplayName("upStationId와 다르다면 false 반환")
    void isSameDownStationWhenFalse() {
        Section section = new Section(1L, 1L, 1L, 2L, 5);

        assertThat(section.isSameDown(5L)).isFalse();
    }

    @Test
    @DisplayName("upStationId, downStationId 모두 같다면 true 반환")
    void isSourceWhenTrue() {
        Section source = new Section(1L, 1L, 1L, 2L, 5);
        Section target = new Section(1L, 1L, 1L, 2L, 5);

        assertThat(source.isSameUpDown(target)).isTrue();
    }

    @ParameterizedTest
    @CsvSource({"1,3", "3,2", "3,5"})
    @DisplayName("upStationId, downStationId 모두 같지 않다면 false 반환")
    void isSourceWhenFalse(Long up, Long down) {
        Section source = new Section(1L, 1L, 1L, 2L, 5);
        Section target = new Section(1L, 1L, up, down, 5);

        assertThat(source.isSameUpDown(target)).isFalse();
    }

    @ParameterizedTest
    @ValueSource(ints = {3, 4, 5})
    @DisplayName("추가하려는 구간보다 거리가 짧거나 같다면 true 반환")
    void isShorterDistanceWhenTrue(int distance) {
        Section source = new Section(1L, 1L, 1L, 2L, distance);
        Section target = new Section(1L, 1L, 1L, 2L, 5);

        assertThat(source.isShorterDistance(target)).isTrue();
    }

    @Test
    @DisplayName("추가하려는 구간보다 거리가 길다면 false 반환")
    void isShorterDistanceWhenFalse() {
        Section source = new Section(1L, 1L, 1L, 2L, 10);
        Section target = new Section(1L, 1L, 1L, 2L, 5);

        assertThat(source.isShorterDistance(target)).isFalse();
    }

    @Test
    @DisplayName("분리된 나머지 구간 정보 반환")
    void makeRest() {
        Section source = new Section(1L, 1L, 1L, 2L, 10);
        Section target = new Section(1L, 1L, 1L, 3L, 3);

        Section result = source.makeRest(target);

        assertThat(result.getUpStationId()).isEqualTo(3L);
        assertThat(result.getDownStationId()).isEqualTo(2L);
        assertThat(result.getDistance()).isEqualTo(7);
    }

    @Test
    @DisplayName("합쳐진 구간 정보 반환")
    void combine() {
        Section source = new Section(1L, 1L, 2L, 3L, 10);
        Section target = new Section(1L, 1L, 1L, 2L, 3);

        Section result = source.combine(target);

        assertThat(result.getUpStationId()).isEqualTo(1L);
        assertThat(result.getDownStationId()).isEqualTo(3L);
        assertThat(result.getDistance()).isEqualTo(13);

    }
}
