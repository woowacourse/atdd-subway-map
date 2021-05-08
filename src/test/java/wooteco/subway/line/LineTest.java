package wooteco.subway.line;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.line.dto.request.LineUpdateRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("지하철 노선 도메인 테스트")
class LineTest {

    @DisplayName("같은 이름인지 비교")
    @Test
    void isSameName() {
        // given
        Line 분당선 = new Line(1L, "분당선", "bg-red-600");
        Line 신분당선 = new Line(2L, "신분당선", "bg-yellow-600");

        // when
        boolean sameName = 분당선.isSameName("분당선");
        boolean notSameName = 신분당선.isSameName("분당선");

        // then
        assertTrue(sameName);
        assertFalse(notSameName);
    }

    @DisplayName("같은 아이디인지 비교")
    @Test
    void isSameId() {
        // given
        Line 분당선 = new Line(1L, "분당선", "bg-red-600");
        Line 신분당선 = new Line(2L, "신분당선", "bg-yellow-600");

        // when
        boolean sameId = 분당선.isSameId(1L);
        boolean notSameId = 신분당선.isSameId(1L);

        // then
        assertTrue(sameId);
        assertFalse(notSameId);
    }

    @Test
    void update() {
        // given
        Line 분당선 = new Line(1L, "분당선", "bg-red-600");
        LineUpdateRequest 신분당선 = new LineUpdateRequest("신분당선", "bg-yellow-600");

        // when
        Line updatedLine = 분당선.update(신분당선);

        // then
        assertThat(updatedLine).usingRecursiveComparison()
                .isEqualTo(new Line(1L, "신분당선", "bg-yellow-600"));
    }
}