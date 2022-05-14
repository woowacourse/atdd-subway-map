package wooteco.subway.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.exception.ClientException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LineTest {

    @Test
    @DisplayName("지하철 노선 - 공백 예와")
    void checkNull() {
        assertThatThrownBy(() -> new Line(1L, "", null))
                .isInstanceOf(ClientException.class)
                .hasMessageContaining("지하철 노선의 이름과 색을 모두 입력해주세요.");
    }
}
