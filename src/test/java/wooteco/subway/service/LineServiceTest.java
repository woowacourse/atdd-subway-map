package wooteco.subway.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dto.LineRequest;

class LineServiceTest {

    @Test
    @DisplayName("노선 생성")
    void saveLine() {
        LineService.createLine(new LineRequest("2호선", "초록색", 0L, 0L, 0));

        Assertions.assertThat(LineDao.existStationByName("2호선")).isTrue();
    }

    @Test
    @DisplayName("중복 노선 생성시 예외 발생")
    void duplicateLineName() {
        var lineRequest = new LineRequest("1호선", "파란색", 0L, 0L, 0);
        LineService.createLine(lineRequest);

        Assertions.assertThatThrownBy(() -> LineService.createLine(lineRequest))
                .isInstanceOf(IllegalArgumentException.class);
    }
}