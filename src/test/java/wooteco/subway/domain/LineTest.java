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

    @Test
    @DisplayName("노선의 이름과 색상이 같으면 동등한 객체이다.")
    void duplicateName() {
        Line line1 = new Line("2호선", "blue");
        Line line2 = new Line("2호선", "blue");

        assertThat(line1).isEqualTo(line2);
    }
}
