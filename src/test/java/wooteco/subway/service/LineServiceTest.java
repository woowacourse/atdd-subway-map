package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class LineServiceTest {

    @Autowired
    private StationDao stationDao;
    @Autowired
    private LineService lineService;

    @Test
    @DisplayName("노선을 저장할 수 있다.")
    void save() {
        // given
        Station station1 = new Station("새로운역");
        Station station2 = new Station("더새로운역");
        Long stationSaveId1 = stationDao.save(station1);
        Long stationSaveId2 = stationDao.save(station2);
        LineRequest request = new LineRequest("신분당선", "bg-red-600", stationSaveId1, stationSaveId2, 10);

        // when
        Long savedId = lineService.save(request);

        // then
        LineResponse response = lineService.findById(savedId);
        assertThat(response).extracting("name", "color")
                .contains("신분당선", "bg-red-600");
    }

    @Test
    @DisplayName("전체 노선을 조회할 수 있다.")
    void findAll() {
        // given
        Station station1 = new Station("새로운역");
        Station station2 = new Station("더새로운역");
        Long stationSaveId1 = stationDao.save(station1);
        Long stationSaveId2 = stationDao.save(station2);
        LineRequest request1 = new LineRequest("신분당선", "bg-red-600", stationSaveId1, stationSaveId2, 10);
        LineRequest request2 = new LineRequest("분당선", "bg-green-600", stationSaveId1, stationSaveId2, 10);
        lineService.save(request1);
        lineService.save(request2);

        // when
        List<LineResponse> responses = lineService.findAll();

        // then
        assertThat(responses).hasSize(2)
                .extracting("name", "color")
                .contains(
                        tuple("신분당선", "bg-red-600"),
                        tuple("분당선", "bg-green-600")
                );
    }

    @Test
    @DisplayName("기존 노선의 이름과 색상을 변경할 수 있다.")
    void updateById() {
        // given
        Station station1 = new Station("새로운역");
        Station station2 = new Station("더새로운역");
        Long stationSaveId1 = stationDao.save(station1);
        Long stationSaveId2 = stationDao.save(station2);
        LineRequest request = new LineRequest("신분당선", "bg-red-600", stationSaveId1, stationSaveId2, 10);
        Long saveId = lineService.save(request);

        // when
        LineRequest updateRequest = new LineRequest("다른분당선", "bg-red-600");
        Long updateId = lineService.updateByLine(saveId, updateRequest);

        // then
        LineResponse response = lineService.findById(updateId);
        assertThat(response).extracting("name", "color")
                .contains("다른분당선", "bg-red-600");
    }

    @Test
    @DisplayName("노선을 삭제할 수 있다.")
    void deleteById() {
        // given
        Station station1 = new Station("새로운역");
        Station station2 = new Station("더새로운역");
        Long stationSaveId1 = stationDao.save(station1);
        Long stationSaveId2 = stationDao.save(station2);
        LineRequest request = new LineRequest("신분당선", "bg-red-600", stationSaveId1, stationSaveId2, 10);
        Long saveId = lineService.save(request);

        // when & then
        assertDoesNotThrow(() -> lineService.deleteById(saveId));
    }
}
