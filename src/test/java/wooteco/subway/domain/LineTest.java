package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LineTest {

    @Test
    @DisplayName("이름과 색깔로 노선을 생성한다.")
    void create() {
        assertThat(new Line("2호선", "bg-red-600")).isNotNull();
    }
}
