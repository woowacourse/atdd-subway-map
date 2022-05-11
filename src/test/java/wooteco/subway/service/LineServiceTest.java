package wooteco.subway.service;

import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.domain.Line;
import wooteco.subway.dto.request.LineRequest;
import wooteco.subway.dto.response.LineResponse;
import wooteco.subway.service.fakeDao.LineDaoImpl;

public class LineServiceTest {
    private final LineService lineService = new LineService(LineDaoImpl.getInstance());

    @BeforeEach
    void setUp() {
        final LineDaoImpl lineDao = LineDaoImpl.getInstance();
        final List<Line> lines = lineDao.findAll();
        lines.clear();
    }

    @Test
    @DisplayName("이미 존재하는 노선을 생성하려고 하면 에러를 발생한다.")
    void save_duplicate_station() {
        final LineRequest lineRequest1 = new LineRequest("신분당선", "bg-red-600");
        final LineRequest lineRequest2 = new LineRequest("신분당선", "bg-green-600");

        lineService.saveLine(lineRequest1);

        Assertions.assertThatThrownBy(() -> lineService.saveLine(lineRequest2))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("같은 이름의 노선이 존재합니다.");
    }

    @Test
    @DisplayName("존재하지 않는 노선을 접근하려고 하면 에러를 발생한다.")
    void not_exist_station() {
        final LineRequest lineRequest = new LineRequest("신분당선", "bg-red-600");

        final LineResponse lineResponse = lineService.saveLine(lineRequest);
        final Long invalidLineId = lineResponse.getId() + 1L;

        Assertions.assertThatThrownBy(() -> lineService.deleteLine(invalidLineId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("해당하는 노선이 존재하지 않습니다.");
    }
}
