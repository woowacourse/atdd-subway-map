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
}
