package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;
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

    @Test
    @DisplayName("모든 노선을 조회한다.")
    void showLines() {
        // given
        fakeLineDao.save(new Line("1호선", "bg-red-600"));
        fakeLineDao.save(new Line("수인분당선", "bg-blue-600"));

        // when
        List<LineResponse> responses = lineService.findAll();

        // then
        assertThat(responses).hasSize(2);
    }

    @Test
    @DisplayName("id에 해당하는 노선을 조회한다.")
    void findById() {
        // given
        String name = "1호선";
        String color = "bg-red-600";
        Line savedLine = fakeLineDao.save(new Line(name, color));

        // when
        LineResponse response = lineService.findById(savedLine.getId());

        // then
        assertThat(response.getName()).isEqualTo(name);
        assertThat(response.getColor()).isEqualTo(color);
    }

    @Test
    @DisplayName("id에 해당하는 노선 정보를 수정한다.")
    void updateById() {
        // given
        Line savedLine = fakeLineDao.save(new Line("1호선", "bg-red-600"));

        final String name = "7호선";
        final String color = "bg-blue-600";
        final LineRequest request = new LineRequest(name, color, null, null, 0);

        // when
        lineService.updateById(savedLine.getId(), request);

        // then
        final Line updatedLine = fakeLineDao.findById(savedLine.getId());
        assertThat(updatedLine.getName()).isEqualTo(name);
        assertThat(updatedLine.getColor()).isEqualTo(color);
    }

    @Test
    @DisplayName("id에 해당하는 노선을 삭제한다.")
    void deleteById() {
        // given
        Line savedLine = fakeLineDao.save(new Line("1호선", "bg-red-600"));

        // when
        lineService.deleteById(savedLine.getId());

        // then
        final List<Line> remainLines = fakeLineDao.findAll();
        assertThat(remainLines).hasSize(0);
    }
}