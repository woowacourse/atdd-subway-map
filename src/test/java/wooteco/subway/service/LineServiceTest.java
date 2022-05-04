package wooteco.subway.service;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.dto.line.LineRequest;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import wooteco.subway.dto.line.LineResponse;

class LineServiceTest {

    @Test
    @DisplayName("노선 생성")
    void saveLine() {
        LineResponse lineResponse = LineService.createLine(new LineRequest("2호선", "테스트색20"));
        LineResponse lineInfos = LineService.findLineInfos(lineResponse.getId());
        assertThat(lineInfos.getName()).isEqualTo("2호선");
//        assertThat(LineDao.existLineByName("2호선")).isTrue();
    }

    @Test
    @DisplayName("중복 노선 생성시 예외 발생")
    void duplicateLineName() {
        var lineRequest = new LineRequest("1호선", "테스트색21");
        LineService.createLine(lineRequest);

        assertThatThrownBy(() -> LineService.createLine(lineRequest))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("노선 조회")
    void findLine() {
        var lineRequest = new LineRequest("7호선", "테스트색22");
        var lineResponse = LineService.createLine(lineRequest);
        var findLineResponse = LineService.findLineInfos(lineResponse.getId());

        assertThat(findLineResponse.getName()).isEqualTo("7호선");
    }

    @Test
    @DisplayName("노선 조회 실패")
    void findLineFail() {
        assertThatThrownBy(() -> LineService.findLineInfos(1000000L))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    @DisplayName("노선 목록 조회")
    void findAllLine() {
        //given
        var lineRequest1 = new LineRequest("99호선", "테스트색23");
        var lineRequest2 = new LineRequest("88호선", "테스트색24");
        var lineResponse1 = LineService.createLine(lineRequest1);
        var lineResponse2 = LineService.createLine(lineRequest2);

        //when
        List<Long> ids = LineService.findAll().stream()
                .map(it -> it.getId())
                .collect(Collectors.toList());

        //then
        assertThat(ids.contains(lineResponse1.getId())).isTrue();
        assertThat(ids.contains(lineResponse2.getId())).isTrue();
    }

    @Test
    @DisplayName("노선 업데이트 성공")
    void updateLine() {
        //given
        var lineRequest = new LineRequest("100호선", "테스트색25");
        var lineResponse = LineService.createLine(lineRequest);
        //when
        LineService.updateById(lineResponse.getId(), "101호선", "테스트색26");
        var lineInfos = LineService.findLineInfos(lineResponse.getId());
        //then
        assertThat(lineInfos.getName()).isEqualTo("101호선");
        assertThat(lineInfos.getColor()).isEqualTo("테스트색26");
    }

    @Test
    @DisplayName("노선 업데이트 실패")
    void failUpdateLine() {
        var lineRequest = new LineRequest("50호선", "테스트색27");
        var lineResponse = LineService.createLine(lineRequest);

        assertThatThrownBy(() -> LineService.updateById(lineResponse.getId(), "50호선", "테스트색27"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}