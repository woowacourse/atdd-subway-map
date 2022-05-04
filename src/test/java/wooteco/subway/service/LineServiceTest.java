package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;

class LineServiceTest {

    private LineService lineService;
    private LineDao fakeLineDao;

    @BeforeEach
    void setUp() {
        fakeLineDao = new FakeLineDao();
        lineService = new LineService(fakeLineDao);
    }

    @Test
    @DisplayName("노선을 생성한다.")
    void createLine() {
        // given
        final LineRequest request = new LineRequest("7호선", "bg-red-600", null, null, 0);

        // when
        final LineResponse response = lineService.create(request);

        // then
        assertThat(response.getName()).isEqualTo(request.getName());
    }
}