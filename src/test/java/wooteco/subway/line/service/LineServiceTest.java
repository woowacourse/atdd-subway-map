package wooteco.subway.line.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
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
import wooteco.subway.section.model.Section;
import wooteco.subway.section.repository.SectionRepository;
import wooteco.subway.station.dao.StationDao;
import wooteco.subway.station.model.Station;

@Sql(value = "/truncate.sql")
@SpringBootTest
class LineServiceTest {

    @Autowired
    private SectionRepository sectionRepository;

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
        Line secondLine= lineDao.save(new Line("2호선", "GREEN"));
        Line firstLine = lineDao.save(new Line("1호선", "BLUE"));

        //when
        List<LineResponse> lineResponses = lineService.findAll();

        //then
        assertAll(
            () -> assertThat(lineResponses.get(0).getId()).isEqualTo(secondLine.getId()),
            () -> assertThat(lineResponses.get(0).getName()).isEqualTo(secondLine.getName()),
            () -> assertThat(lineResponses.get(0).getColor()).isEqualTo(secondLine.getColor()),
            () -> assertThat(lineResponses.get(1).getId()).isEqualTo(firstLine.getId()),
            () -> assertThat(lineResponses.get(1).getName()).isEqualTo(firstLine.getName()),
            () -> assertThat(lineResponses.get(1).getColor()).isEqualTo(firstLine.getColor())
        );
    }

    @DisplayName("노선 생성")
    @Test
    void createLine() {
        //given
        Station station1 = stationDao.save(new Station("강남역"));
        Station station2 = stationDao.save(new Station("잠실역"));
        LineRequest request = new LineRequest("2호선", "green",
            station1.getId(), station2.getId(), 10);

        //when
        LineDetailsResponse lineDetailsResponse = lineService.createLine(request);

        //then
        assertThat(lineDetailsResponse.getName()).isEqualTo("2호선");
        assertThat(lineDetailsResponse.getColor()).isEqualTo("green");
        assertThat(sectionRepository.findSectionsByLineId(lineDetailsResponse.getId()))
            .isNotEmpty();
    }

    @DisplayName("단일 노선 조회")
    @Test
    void showLineById() {
        //given
        Station station1 = stationDao.save(new Station("강남역"));
        Station station2 = stationDao.save(new Station("잠실역"));
        Line line = lineDao.save(new Line("2호선", "GREEN"));

        Section section = Section.builder()
            .line(line)
            .upStation(station1)
            .downStation(station2)
            .distance(10)
            .build();

        sectionRepository.save(section);

        //when
        LineDetailsResponse lineDetailsResponse = lineService.showLineById(line.getId());

        //then
        assertThat(lineDetailsResponse.getId()).isEqualTo(line.getId());
        assertThat(lineDetailsResponse.getName()).isEqualTo("2호선");
        assertThat(lineDetailsResponse.getColor()).isEqualTo("GREEN");
    }

    @DisplayName("노선 삭제 기능")
    @Test
    void name() {
        //given
        Station station1 = stationDao.save(new Station("강남역"));
        Station station2 = stationDao.save(new Station("잠실역"));
        Line line = lineDao.save(new Line("2호선", "GREEN"));

        Section section = Section.builder()
            .line(line)
            .upStation(station1)
            .downStation(station2)
            .distance(10)
            .build();

        sectionRepository.save(section);

        //when
        lineService.deleteById(line.getId());

        //then
        assertThatThrownBy(() -> sectionRepository.findSectionsByLineId(line.getId()))
            .isExactlyInstanceOf(NotFoundException.class);
    }
}
