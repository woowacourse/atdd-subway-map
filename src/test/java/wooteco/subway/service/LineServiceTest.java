package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import wooteco.subway.controller.AcceptanceTest;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.StationResponse;
import wooteco.subway.exception.BadRequestLineException;

public class LineServiceTest extends AcceptanceTest {

    @Autowired
    private StationDao stationDao;
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

    @DisplayName("노선을 등록할 때 상행선, 하행선이 같은 id로 들어오면 예외를 발생시킨다.")
    @Test
    void saveDuplicateStationException() {
        assertThatThrownBy(() -> lineService.save(
                new LineRequest("신분당선", "red", 1L, 1L, 10)))
                .isInstanceOf(BadRequestLineException.class);
    }

    @DisplayName("노선을 등록할 때 이름, 색깔이 공백 또는 빈값이면 예외를 발생시킨다.")
    @ParameterizedTest
    @CsvSource(value = {"empty,red", "신분당선,empty"}, emptyValue = "empty")
    void saveNameIsBlankException(String lineName, @Nullable String color) {
        assertThatThrownBy(() -> lineService.save(
                new LineRequest(lineName, color, 1L, 1L, 10)))
                .isInstanceOf(BadRequestLineException.class);
    }

    @DisplayName("노선을 등록할 때 상행선과 하행선의 거리가 1 미만이면 예외를 발생시킨다.")
    @Test
    void saveDistanceLessThan1() {
        assertThatThrownBy(() -> lineService.save(
                new LineRequest("신분당선", "red", 1L, 1L, 0)))
                .isInstanceOf(BadRequestLineException.class);
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
}
