package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;

class LineServiceTest {

    private final StationDao stationDao = new MockStationDao();
    private final LineDao lineDao = new MockLineDao();
    private final SectionDao sectionDao = new MockSectionDao();

    private final LineService lineService = new LineService(lineDao, stationDao, sectionDao);

    @DisplayName("노선을 저장한다")
    @Test
    void save() {
        // given
        stationDao.save(new Station(1L, "name1"));
        stationDao.save(new Station(2L, "name2"));
        LineRequest request = new LineRequest("name", "color", 1L, 2L, 10);

        // when
        LineResponse response = lineService.save(request);

        // then
        assertAll(
                () -> assertThat(response.getName()).isEqualTo("name"),
                () -> assertThat(response.getColor()).isEqualTo("color"),
                () -> assertThat(response.getStations()).hasSize(2)
        );
    }

    @DisplayName("모든 노선을 찾는다")
    @Test
    void findAll() {
        // given
        stationDao.save(new Station(1L, "name1"));
        stationDao.save(new Station(2L, "name2"));
        LineRequest request1 = new LineRequest("name", "color", 1L, 2L, 10);
        LineRequest request2 = new LineRequest("name", "color", 1L, 2L, 10);
        LineRequest request3 = new LineRequest("name", "color", 1L, 2L, 10);
        lineService.save(request1);
        lineService.save(request2);
        lineService.save(request3);

        // when
        List<LineResponse> responses = lineService.findAll();

        // then
        assertAll(
                () -> assertThat(responses).hasSize(3)
        );
    }

    @DisplayName("id에 맞는 노선을 찾는다")
    @Test
    void findById() {
        // given
        stationDao.save(new Station(1L, "name1"));
        stationDao.save(new Station(2L, "name2"));
        LineRequest request = new LineRequest("name", "color", 1L, 2L, 10);
        Long id = lineService.save(request).getId();

        // when
        LineResponse response = lineService.findById(id);

        // then
        assertAll(
                () -> assertThat(response.getName()).isEqualTo("name"),
                () -> assertThat(response.getColor()).isEqualTo("color"),
                () -> assertThat(response.getStations()).hasSize(2)
        );
    }

    @DisplayName("노선을 수정한다")
    @Test
    void update() {
        // given
        stationDao.save(new Station(1L, "name1"));
        stationDao.save(new Station(2L, "name2"));
        LineRequest request = new LineRequest("name", "color", 1L, 2L, 10);
        Long id = lineService.save(request).getId();

        // when
        lineService.update(id,
                new LineRequest("changeName", "changeColor", null, null, 0));

        // then
        Line line = lineDao.findById(id);
        assertAll(
                () -> assertThat(line.getName()).isEqualTo("changeName"),
                () -> assertThat(line.getColor()).isEqualTo("changeColor")
        );
    }

    @DisplayName("id에 맞는 노선을 제거한다")
    @Test
    void deleteById() {
        // given
        stationDao.save(new Station(1L, "name1"));
        stationDao.save(new Station(2L, "name2"));
        LineRequest request = new LineRequest("name", "color", 1L, 2L, 10);
        Long id = lineService.save(request).getId();

        // when
        lineService.deleteById(id);

        // then
        List<Line> lines = lineDao.findAll();
        assertThat(lines).isEmpty();
    }
}
