package wooteco.subway.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dto.LineRequest;

import java.util.NoSuchElementException;

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

    @Test
    @DisplayName("노선 조회")
    void findLine() {
        var lineRequest = new LineRequest("7호선", "녹색", 0L, 0L, 0);
        var lineResponse = LineService.createLine(lineRequest);
        var findLineResponse = LineService.findLineInfos(lineResponse.getId());

        Assertions.assertThat(findLineResponse.getName()).isEqualTo("7호선");
    }

    @Test
    @DisplayName("노선 조회 실패")
    void findLineFail() {
        Assertions.assertThatThrownBy(() -> LineService.findLineInfos(1000000L))
                .isInstanceOf(NoSuchElementException.class);
    }
}