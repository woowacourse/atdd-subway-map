package wooteco.subway.section.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.station.domain.Station;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Section 테스트")
class SectionTest {
    @DisplayName("하행역이 입력되지 않았을 경우 예외")
    @Test
    public void whenNotContainDownStation() {
        assertThatThrownBy(() -> new Section(new Station(1L, "강남역"), null, new Distance(10)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("역을 모두 입력해주세요.");
    }

    @DisplayName("상행역이 입력되지 않았을 경우 예외")
    @Test
    public void whenNotContainUpStation() {
        assertThatThrownBy(() -> new Section(null, new Station(1L, "강남역"), new Distance(10)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("역을 모두 입력해주세요.");
    }

    @DisplayName("거리를 입력하지 않았을 경우 예외")
    @Test
    public void whenNotInputDistance() {
        assertThatThrownBy(() -> new Section(
                new Station(1L, "강남역"),
                new Station(2L, "역삼역"), null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("거리를 입력해주세요.");
    }

    @DisplayName("거리가 양수가 아닌 경우 예외")
    @Test
    public void whenNotPositiveDistance() {
        assertThatThrownBy(() -> new Section(
                new Station(1L, "강남역"),
                new Station(2L, "역삼역"), new Distance(-1)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("거리는 양수여야 합니다.");
    }
}