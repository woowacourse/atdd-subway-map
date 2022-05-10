package wooteco.subway.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.dto.section.SectionRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SectionTest {

    @Test
    @DisplayName("상행종점과 하행종점이 동일한 경우")
    void sameUpDown() {
        assertThatThrownBy(() -> Section.of(1L, new SectionRequest(1L, 1L, 5)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("상행종점과 하행종점은 같은 지하철역일 수 없습니다.");
    }

    @Test
    @DisplayName("거리가 1미만의 정수인 경우")
    void wrongDistance() {
        assertThatThrownBy(() -> Section.of(1L, new SectionRequest(1L, 2L, 0)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("거리는 1이상의 정수만 허용됩니다.");
    }

    @Test
    @DisplayName("해당 구간이 Target 바로 앞 구간이라면 true 반환")
    void isFrontWhenTrue() {
        Section source = new Section(1L, 1L, 1L, 2L, 5);
        Section target = new Section(1L, 1L, 2L, 3L, 5);

        assertThat(source.isFront(target)).isTrue();
    }

    @Test
    @DisplayName("해당 구간이 Target 바로 뒤 구간이 아니라면 false 반환")
    void isFrontWhenFalse() {
        Section source = new Section(1L, 1L, 1L, 2L, 5);
        Section target = new Section(1L, 1L, 5L, 6L, 5);

        assertThat(source.isFront(target)).isFalse();
    }

    @Test
    @DisplayName("해당 구간이 Target 바로 뒤 구간이라면 true 반환")
    void isBackWhenTrue() {
        Section source = new Section(1L, 1L, 2L, 3L, 5);
        Section target = new Section(1L, 1L, 1L, 2L, 5);

        assertThat(source.isBack(target)).isTrue();
    }

    @Test
    @DisplayName("해당 구간이 Target 바로 뒤 구간이 아니라면 false 반환")
    void isBackWhenFalse() {
        Section source = new Section(1L, 1L, 1L, 2L, 5);
        Section target = new Section(1L, 1L, 5L, 6L, 5);

        assertThat(source.isBack(target)).isFalse();
    }
}
