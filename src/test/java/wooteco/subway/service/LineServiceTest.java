package wooteco.subway.service;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;

public class LineServiceTest {

    private LineService lineService;

    @BeforeEach
    void setUp() {
        lineService = new LineService(new FakeLineDao());
    }

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        LineRequest lineRequest = new LineRequest("2호선", "red");
        LineResponse lineResponse = lineService.save(lineRequest);

        assertThat(lineResponse.getName()).isEqualTo(lineRequest.getName());
    }

    @DisplayName("중복된 지하철 노선을 생성한다.")
    @Test
    void createLineWithDuplicateName() {
        LineRequest lineRequest = new LineRequest("2호선", "red");
        lineService.save(lineRequest);

        assertThatThrownBy(() -> lineService.save(lineRequest)).isInstanceOf(IllegalArgumentException.class)
            .hasMessage("중복된 지하철 노선 이름입니다.");
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        LineRequest lineRequest1 = new LineRequest("2호선", "red");
        LineRequest lineRequest2 = new LineRequest("3호선", "red");
        lineService.save(lineRequest1);
        lineService.save(lineRequest2);

        assertThat(lineService.findAll()).hasSize(2);
    }

    @DisplayName("지하철 노선 조회한다.")
    @Test
    void getLine() {
        LineRequest lineRequest1 = new LineRequest("2호선", "red");
        LineResponse lineResponse = lineService.save(lineRequest1);

        assertThat(lineService.find(lineResponse.getId()).getName()).isEqualTo(lineRequest1.getName());
    }

    @DisplayName("존재하지 않는 지하철 노선 조회한다.")
    @Test
    void getLineNotExists() {
        assertThatThrownBy(() -> lineService.find(1L)).isInstanceOf(IllegalArgumentException.class)
            .hasMessage("존재하지 않는 지하철 노선 id입니다.");
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        LineRequest lineRequest = new LineRequest("2호선", "red");
        LineResponse lineResponse = lineService.save(lineRequest);

        LineRequest lineRequest2 = new LineRequest("3호선", "red");
        lineService.update(lineResponse.getId(), lineRequest2);
        assertThat(lineService.find(lineResponse.getId()).getName()).isEqualTo(lineRequest2.getName());
    }

    @DisplayName("존재하지 않는 지하철 노선 수정한다.")
    @Test
    void updateLineNotExists() {
        LineRequest lineRequest = new LineRequest("2호선", "red");
        assertThatThrownBy(() -> lineService.update(1L, lineRequest)).isInstanceOf(IllegalArgumentException.class)
            .hasMessage("존재하지 않는 지하철 노선 id입니다.");
    }

    @DisplayName("지하철 노선을 삭제한다.")
    @Test
    void deleteLine() {
        LineRequest lineRequest = new LineRequest("2호선", "red");
        LineResponse lineResponse = lineService.save(lineRequest);

        lineService.delete(lineResponse.getId());
        assertThat(lineService.findAll()).hasSize(0);
    }

    @DisplayName("존재하지 않는 지하철 노선 삭제한다.")
    @Test
    void deleteLineNotExists() {
        assertThatThrownBy(() -> lineService.delete(1L)).isInstanceOf(IllegalArgumentException.class)
            .hasMessage("존재하지 않는 지하철 노선 id입니다.");
    }
}
