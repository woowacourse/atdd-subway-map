package wooteco.subway.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

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

    @Test
    @DisplayName("노선 목록 조회")
    void findAllLine() {
        //given
        var lineRequest1 = new LineRequest("99호선", "검은색", 0L, 0L, 0);
        var lineRequest2 = new LineRequest("88호선", "흰색", 0L, 0L, 0);
        var lineResponse1 = LineService.createLine(lineRequest1);
        var lineResponse2 = LineService.createLine(lineRequest2);

        //when
        List<Long> ids = LineService.findAll().stream()
                .map(it -> it.getId())
                .collect(Collectors.toList());

        //then
        Assertions.assertThat(ids.contains(lineResponse1.getId())).isTrue();
        Assertions.assertThat(ids.contains(lineResponse2.getId())).isTrue();
    }
}