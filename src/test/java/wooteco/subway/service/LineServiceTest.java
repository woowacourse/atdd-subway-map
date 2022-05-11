package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import wooteco.subway.controller.AcceptanceTest;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.StationResponse;

public class LineServiceTest extends AcceptanceTest {

    @Autowired
    private StationDao stationDao;
    @Autowired
    private LineDao lineDao;
    @Autowired
    private SectionDao sectionDao;
    @Autowired
    private LineService lineService;

    @DisplayName("노선을 등록하고 LineResponse를 반환한다.")
    @Test
    void save() {
        Station upStation = stationDao.save(new Station("동천역"));
        Station downStation = stationDao.save(new Station("판교역"));

        LineResponse lineResponse = lineService.save(
                new LineRequest("신분당선", "red", upStation.getId(), downStation.getId(), 10));

        assertAll(
                () -> assertThat(lineResponse.getName()).isEqualTo("신분당선"),
                () -> assertThat(lineResponse.getColor()).isEqualTo("red"),
                () -> assertThat(lineResponse.getStations()
                        .stream()
                        .map(StationResponse::getName)
                        .collect(Collectors.toList())).containsExactly("동천역", "판교역")
        );
    }

    @DisplayName("모든 노선을 조회한다.")
    @Test
    void findAll() {
        Station 강남 = stationDao.save(new Station("강남"));
        Station 양재 = stationDao.save(new Station("양재"));
        lineService.save(new LineRequest("신분당선", "red", 강남.getId(), 양재.getId(), 3));
        List<LineResponse> lineResponses = lineService.findAll();
        for (LineResponse lineResponse : lineResponses) {
            assertAll(
                    () -> assertThat(lineResponse.getName()).isEqualTo("신분당선"),
                    () -> assertThat(lineResponse.getColor()).isEqualTo("red"),
                    () -> assertThat(lineResponse.getStations()).hasSize(2)
            );
        }
    }

    @DisplayName("노선을 삭제한다.")
    @Test
    void delete() {
        Station 강남 = stationDao.save(new Station("강남"));
        Station 양재 = stationDao.save(new Station("양재"));
        Line line = lineDao.save(new Line("신분당선", "red"));
        sectionDao.save(new Section(강남.getId(), 양재.getId(), line.getId(), 3));

        lineService.delete(line.getId());

        assertAll(
                () -> assertThat(lineDao.findById(line.getId()).isEmpty()).isTrue(),
                () -> assertThat(sectionDao.findAllByLineId(line.getId())).hasSize(0)
        );
    }
}
