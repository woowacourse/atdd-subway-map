package wooteco.subway.line.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.exception.NotFoundException;
import wooteco.subway.line.api.dto.LineDetailsResponse;
import wooteco.subway.line.api.dto.LineRequest;
import wooteco.subway.line.api.dto.LineResponse;
import wooteco.subway.line.dao.LineDao;
import wooteco.subway.line.model.Line;
import wooteco.subway.section.dao.SectionDao;
import wooteco.subway.station.dao.StationDao;
import wooteco.subway.station.model.Station;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
@Sql("/test.sql")
class LineServiceTest {

    @Autowired
    private SectionDao sectionDao;

    @Autowired
    private StationDao stationDao;

    @Autowired
    private LineDao lineDao;

    @Autowired
    private LineService lineService;

    @DisplayName("전체 노선 조회")
    @Test
    void findAll() {
        //given
        Long secondLineId = lineDao.save(new Line("2호선", "GREEN"));
        Long firstLineId = lineDao.save(new Line("1호선", "BLUE"));

        //when
        List<LineResponse> lineResponses = lineService.findAll();

        //then
        assertAll(
                () -> assertThat(lineResponses.get(0).getId()).isEqualTo(secondLineId),
                () -> assertThat(lineResponses.get(0).getName()).isEqualTo("2호선"),
                () -> assertThat(lineResponses.get(0).getColor()).isEqualTo("GREEN"),
                () -> assertThat(lineResponses.get(1).getId()).isEqualTo(firstLineId),
                () -> assertThat(lineResponses.get(1).getName()).isEqualTo("1호선"),
                () -> assertThat(lineResponses.get(1).getColor()).isEqualTo("BLUE")
        );
    }

    @DisplayName("노선 생성")
    @Test
    void createLine() {
        //given
        Long stationId1 = stationDao.save(new Station("강남역"));
        Long stationId2 = stationDao.save(new Station("잠실역"));
        LineRequest request = new LineRequest("2호선", "green",
                stationId1, stationId2, 10);

        //when
        LineDetailsResponse lineDetailsResponse = lineService.createLine(request);

        //then
        assertThat(lineDetailsResponse.getName()).isEqualTo("2호선");
        assertThat(lineDetailsResponse.getColor()).isEqualTo("green");
        assertThat(sectionDao.findSectionsByLineId(lineDetailsResponse.getId()))
                .isNotEmpty();
    }

    @DisplayName("단일 노선 조회")
    @Test
    void showLineById() {
        //given
        Long stationId1 = stationDao.save(new Station("강남역"));
        Long stationId2 = stationDao.save(new Station("잠실역"));
        Long lineId = lineDao.save(new Line("2호선", "GREEN"));
        sectionDao.save(lineId, new LineRequest("2호선","GREEN", stationId1, stationId2, 10));

        //when
        LineDetailsResponse lineDetailsResponse = lineService.showLineById(lineId);

        //then
        assertThat(lineDetailsResponse.getId()).isEqualTo(lineId);
        assertThat(lineDetailsResponse.getName()).isEqualTo("2호선");
        assertThat(lineDetailsResponse.getColor()).isEqualTo("GREEN");
    }

    @DisplayName("노선 삭제 기능")
    @Test
    void name() {
        //given
        Long stationId1 = stationDao.save(new Station("강남역"));
        Long stationId2 = stationDao.save(new Station("잠실역"));
        Long lineId = lineDao.save(new Line("2호선", "GREEN"));
        sectionDao.save(lineId, new LineRequest("2호선","GREEN", stationId1, stationId2, 10));
        //when
        lineService.deleteById(lineId);
        //then
        assertThatThrownBy(() -> lineDao.findLineById(lineId))
                .isInstanceOf(NotFoundException.class);
        assertThat(sectionDao.findSectionsByLineId(lineId)).isEmpty();
    }
}
