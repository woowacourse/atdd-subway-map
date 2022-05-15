package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.exception.SubwayException;

public class SectionTest {

    @DisplayName("지하철 구간을 생성할 때 상핵역과 하행역이 같으면 예외가 발생한다.")
    @Test
    void saveSameStations() {
        assertThatThrownBy(() -> new Section(1L, 1L, 1L, 1))
                .isInstanceOf(SubwayException.class)
                .hasMessage("상행역과 하행역은 같을 수 없습니다.");
    }

    @DisplayName("지하철 구간을 생성할 때 상핵역과 하행역이 같으면 예외가 발생한다.")
    @Test
    void saveNotPositiveDistance() {
        assertThatThrownBy(() -> new Section(1L, 1L, 2L, 0))
                .isInstanceOf(SubwayException.class)
                .hasMessage("구간의 길이는 양수여야 합니다.");
    }
}
