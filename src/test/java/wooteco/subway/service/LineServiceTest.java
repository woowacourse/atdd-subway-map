package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.dao.FakeLineDao;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;

public class LineServiceTest {

    private LineDao lineDao;
    private LineService lineService;

    @BeforeEach
    void setUp() {
        lineDao = new FakeLineDao();
        lineService = new LineService(lineDao);
    }

    @Test
    @DisplayName("지하철 노선을 저장할 수 있다.")
    void insertLine() {
        LineRequest lineRequest = new LineRequest("신분당선", "bg-red-600");
        lineService.insertLine(lineRequest);

        List<String> names = lineDao.findNames();

        assertThat(names).contains("신분당선");
    }

    @Test
    @DisplayName("이름이 중복된 지하철 노선은 저장할 수 없다.")
    void insertLineDuplicateColor() {
        LineRequest lineRequest1 = new LineRequest("신분당선", "bg-red-600");
        LineRequest lineRequest2 = new LineRequest("신분당선", "bg-red-500");
        lineService.insertLine(lineRequest1);

        assertThatThrownBy(() -> lineService.insertLine(lineRequest2))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(LineService.NAME_DUPLICATE_EXCEPTION_MESSAGE);
    }

    @Test
    @DisplayName("이름이 중복된 지하철 노선은 저장할 수 없다.")
    void insertLineDuplicateName() {
        LineRequest lineRequest1 = new LineRequest("신분당선", "bg-red-600");
        LineRequest lineRequest2 = new LineRequest("분당선", "bg-red-600");
        lineService.insertLine(lineRequest1);

        assertThatThrownBy(() -> lineService.insertLine(lineRequest2))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(LineService.COLOR_DUPLICATE_EXCEPTION_MESSAGE);
    }

    @Test
    @DisplayName("지하철 노선들을 조회할 수 있다.")
    void findLines() {
        LineRequest lineRequest1 = new LineRequest("신분당선", "bg-red-600");
        LineRequest lineRequest2 = new LineRequest("분당선", "bg-red-500");
        lineService.insertLine(lineRequest1);
        lineService.insertLine(lineRequest2);

        List<LineResponse> lines = lineService.findLines();
        List<String> lineNames = lines.stream()
                .map(LineResponse::getName)
                .collect(Collectors.toList());

        assertThat(lineNames).contains("신분당선", "분당선");
    }

    @Test
    @DisplayName("지하철 노선을 조회할 수 있다.")
    void findLine() {
        LineRequest lineRequest = new LineRequest("신분당선", "bg-red-600");
        LineResponse lineResponse = lineService.insertLine(lineRequest);

        LineResponse lineResponse1 = lineService.findLine(lineResponse.getId());

        assertThat(lineResponse1.getName()).isEqualTo("신분당선");
    }

    @Test
    @DisplayName("지하철 노선을 업데이트할 수 있다.")
    void updateLine() {
        LineRequest lineRequest = new LineRequest("신분당선", "bg-red-600");
        LineResponse lineResponse = lineService.insertLine(lineRequest);

        lineService.updateLine(lineResponse.getId(), new LineRequest("분당선", "bg-green-500"));

        Line line = lineDao.findById(lineResponse.getId());

        assertThat(line.getName()).isEqualTo("분당선");
    }

    @Test
    @DisplayName("지하철 노선을 지울 수 있다.")
    void deleteLine() {
        LineRequest lineRequest = new LineRequest("신분당선", "bg-red-600");
        LineResponse lineResponse = lineService.insertLine(lineRequest);

        lineService.deleteLine(lineResponse.getId());

        List<String> names = lineDao.findNames();
        assertThat(names).doesNotContain("신분당선");
    }
}
