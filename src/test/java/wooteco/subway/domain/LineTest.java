package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LineTest {

    @Test
    @DisplayName("중복된 지하철 노선 이름 검증")
    void validateDuplicateName() {
        // given
        Line line1 = new Line("1호선", "bg-blue-100");
        Line line2 = new Line("1호선", "bg-red-300");
        Line line3 = new Line("3호선", "bg-orange-500");

        // when
        boolean duplicate = line1.isDuplicate(line2);
        boolean notDuplicate = line1.isDuplicate(line3);

        // then
        assertThat(duplicate).isTrue();
        assertThat(notDuplicate).isFalse();
    }

    @Test
    @DisplayName("중복된 지하철 노선 색상 검증")
    void validateDuplicateColor() {
        // given
        Line line1 = new Line("1호선", "bg-blue-100");
        Line line2 = new Line("2호선", "bg-blue-100");
        Line line3 = new Line("3호선", "bg-orange-500");

        // when
        boolean duplicate = line1.isDuplicate(line2);
        boolean notDuplicate = line1.isDuplicate(line3);

        // then
        assertThat(duplicate).isTrue();
        assertThat(notDuplicate).isFalse();
    }
}