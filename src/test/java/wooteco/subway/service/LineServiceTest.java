package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.stream.Collectors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import wooteco.subway.controller.AcceptanceTest;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.StationResponse;

public class LineServiceTest extends AcceptanceTest {

    @Autowired
    private StationDao stationDao;
    @Autowired
    private LineService lineService;

    @DisplayName("노선과 구간을 생성하고 LineResponse를 반환한다.")
    @Test
    void create() {
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
}
