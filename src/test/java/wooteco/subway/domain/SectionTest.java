package wooteco.subway.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import wooteco.subway.exception.ClientException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SectionTest {

    @Test
    @DisplayName("구간 예외 null-  아무 것도 입력되지 않은 경우")
    void checkNull() {
        assertThatThrownBy(() -> new Section(1L, null, null, 0))
                .isInstanceOf(ClientException.class)
                .hasMessageContaining("지하철 노선 Id와 상행, 하행 역을 입력해주세요.");
    }

    @Test
    @DisplayName("구간 예외 - 0이 입력된 경우")
    void checkZero() {
        assertThatThrownBy(() -> new Section(1L, 0L, 0L, 1))
                .isInstanceOf(ClientException.class)
                .hasMessageContaining("지하철 노선 Id와 상행, 하행 역, 거리는 0 이상의 값이어야 합니다.");
    }
}
