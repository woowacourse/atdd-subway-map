package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;

@SpringBootTest
@Transactional
class LineServiceTest {

    @Autowired
    private LineService lineService;

    @Autowired
    private LineDao lineDao;

    @Autowired
    private StationDao stationDao;

    @Test
    @DisplayName("노선을 생성한다.")
    void createLine() {
        // given
        Station gangnam = stationDao.save(new Station("강남역"));
        Station nowon = stationDao.save(new Station("노원역"));
        final LineRequest request = new LineRequest("7호선", "bg-red-600", gangnam.getId(), nowon.getId(), 10);

        // when
        final LineResponse response = lineService.save(request);

        // then
        assertThat(response.getName()).isEqualTo(request.getName());
    }

    @Test
    @DisplayName("모든 노선을 조회한다.")
    void showLines() {
        // given
        stationDao.save(new Station("강남역"));
        stationDao.save(new Station("노원역"));
        lineService.save(new LineRequest("1호선", "bg-red-600", 1L, 2L, 10));
        lineService.save(new LineRequest("2호선", "bg-blue-600", 2L, 1L, 10));

        // when
        List<LineResponse> responses = lineService.findAll();

        // then
        assertThat(responses).hasSize(2);
    }

    @Test
    @DisplayName("id에 해당하는 노선을 조회한다.")
    void findById() {
        // given
        String color = "bg-red-600";
        String name = "7호선";

        Station gangnam = stationDao.save(new Station("강남역"));
        Station nowon = stationDao.save(new Station("노원역"));
        LineResponse savedLine = lineService.save(new LineRequest(name, color, gangnam.getId(), nowon.getId(), 10));

        // when
        LineResponse response = lineService.findById(savedLine.getId());

        // then
        assertThat(response.getName()).isEqualTo(name);
        assertThat(response.getColor()).isEqualTo(color);
        assertThat(response.getStations()).hasSize(2);
    }

    @Test
    @DisplayName("id에 해당하는 노선 정보를 수정한다.")
    void updateById() {
        // given
        Line savedLine = lineDao.save(new Line("1호선", "bg-red-600"));

        final String name = "7호선";
        final String color = "bg-blue-600";
        final LineRequest request = new LineRequest(name, color, null, null, 0);

        // when
        lineService.updateById(savedLine.getId(), request);

        // then
        final Line updatedLine = lineDao.findById(savedLine.getId()).get();
        assertThat(updatedLine.getName()).isEqualTo(name);
        assertThat(updatedLine.getColor()).isEqualTo(color);
    }

    @Test
    @DisplayName("id에 해당하는 노선을 삭제한다.")
    void deleteById() {
        // given
        Line savedLine = lineDao.save(new Line("1호선", "bg-red-600"));

        // when
        lineService.deleteById(savedLine.getId());

        // then
        final List<Line> remainLines = lineDao.findAll();
        assertThat(remainLines).hasSize(0);
    }
}
