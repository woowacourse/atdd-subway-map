package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;

@SpringBootTest
class LineServiceTest extends ServiceTest {

    private final StationDao stationDao;
    private final LineService lineService;

    @Autowired
    public LineServiceTest(StationDao stationDao, LineService lineService) {
        this.stationDao = stationDao;
        this.lineService = lineService;
    }

    @DisplayName("노선을 저장한다.")
    @Test
    void save() {
        Station upStation = stationDao.save(new Station("강남역"));
        Station downStation = stationDao.save(new Station("선릉역"));

        LineRequest lineRequest = new LineRequest("2호선", "green", upStation.getId(), downStation.getId(), 10);

        LineResponse lineResponse = lineService.save(lineRequest);

        assertThat(lineResponse.getName()).isEqualTo(lineRequest.getName());
    }

    @DisplayName("같은 이름의 노선을 저장하는 경우 예외가 발생한다.")
    @Test
    void saveExistingName() {
        Station upStation = stationDao.save(new Station("강남역"));
        Station downStation = stationDao.save(new Station("선릉역"));

        LineRequest lineRequest = new LineRequest("2호선", "green", upStation.getId(), downStation.getId(), 10);

        lineService.save(lineRequest);

        assertThatThrownBy(() -> lineService.save(lineRequest))
                .isInstanceOf(DuplicateKeyException.class);
    }

    @DisplayName("모든 지하철 노선을 조회한다.")
    @Test
    void findAll() {
        Station upStation = stationDao.save(new Station("강남역"));
        Station downStation = stationDao.save(new Station("선릉역"));

        LineRequest lineRequest = new LineRequest("2호선", "green", upStation.getId(), downStation.getId(), 10);

        Station upStation2 = stationDao.save(new Station("교대역"));
        Station downStation2 = stationDao.save(new Station("수서역"));

        LineRequest lineRequest2 = new LineRequest("3호선", "orange", upStation2.getId(), downStation2.getId(), 10);

        lineService.save(lineRequest);
        lineService.save(lineRequest2);

        List<String> lineNames = lineService.findAll()
                .stream()
                .map(LineResponse::getName)
                .collect(Collectors.toList());

        assertThat(lineNames).containsExactly("2호선", "3호선");
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void findById() {
        Station upStation = stationDao.save(new Station("강남역"));
        Station downStation = stationDao.save(new Station("선릉역"));

        LineRequest lineRequest = new LineRequest("2호선", "green", upStation.getId(), downStation.getId(), 10);

        LineResponse lineResponse = lineService.save(lineRequest);

        LineResponse foundLine = lineService.findById(lineResponse.getId());

        assertThat(foundLine.getName()).isEqualTo(lineResponse.getName());
    }

    @DisplayName("존재하지 않는 지하철 노선을 조회할 경우 예외가 발생한다.")
    @Test
    void findNotExistingLine() {
        assertThatThrownBy(() -> lineService.findById(1L))
                .isInstanceOf(EmptyResultDataAccessException.class);
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void update() {
        Station upStation = stationDao.save(new Station("강남역"));
        Station downStation = stationDao.save(new Station("선릉역"));

        LineRequest lineRequest = new LineRequest("2호선", "green", upStation.getId(), downStation.getId(), 10);
        LineResponse lineResponse = lineService.save(lineRequest);

        lineService.update(lineResponse.getId(), new LineRequest("3호선", "orange"));

        assertThat(lineService.findById(lineResponse.getId()).getName()).isEqualTo("3호선");
    }

    @DisplayName("지하철 노선을 삭제한다.")
    @Test
    void deleteById() {
        Station upStation = stationDao.save(new Station("강남역"));
        Station downStation = stationDao.save(new Station("선릉역"));

        LineRequest lineRequest = new LineRequest("2호선", "green", upStation.getId(), downStation.getId(), 10);
        LineResponse lineResponse = lineService.save(lineRequest);

        lineService.deleteById(lineResponse.getId());

        assertThat(lineService.findAll().size()).isZero();
    }
}
