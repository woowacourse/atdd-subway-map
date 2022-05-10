package wooteco.subway.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@Transactional
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class LineServiceTest {

    private final LineDao lineDao;
    private final StationDao stationDao;
    private final LineService lineService;

    LineServiceTest(LineDao lineDao, StationDao stationDao, LineService lineService) {
        this.lineDao = lineDao;
        this.stationDao = stationDao;
        this.lineService = lineService;
    }

    @Test
    @DisplayName("새로운 노선을 만든다.")
    void create() {
        stationDao.save("강남역");
        stationDao.save("선릉역");
        LineRequest lineRequest = new LineRequest("2호선", "green", 1L, 2L, 10);

        LineResponse actual = lineService.create(lineRequest);

        assertThat(actual.getName()).isEqualTo("2호선");
        assertThat(actual.getStations()).hasSize(2);
    }

    @Test
    @DisplayName("노선 목록을 반환한다.")
    void findAll() {
        Station upStation = stationDao.save("강남역");
        Station downStation = stationDao.save("선릉역");
        LineRequest lineRequest = new LineRequest("2호선", "green", upStation.getId(), downStation.getId(), 10);
        lineService.create(lineRequest);

        List<LineResponse> actual = lineService.findAll();

        assertThat(actual).hasSize(1);
    }

    @Test
    @DisplayName("노선을 수정한다.")
    void update() {
        stationDao.save("강남역");
        stationDao.save("선릉역");
        Line line = lineDao.save(new LineRequest("2호선", "green", 1L, 2L, 10));
        LineRequest updateRequest = new LineRequest("3호선", "green");

        lineService.update(line.getId(), updateRequest);

        Line actual = lineDao.findById(line.getId()).get();
        assertThat(actual.getName()).isEqualTo("3호선");
        assertThat(actual.getColor()).isEqualTo("green");
    }

}
