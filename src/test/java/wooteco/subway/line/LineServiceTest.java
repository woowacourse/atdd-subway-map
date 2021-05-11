package wooteco.subway.line;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.line.dto.LineRequest;
import wooteco.subway.line.dto.LineResponse;
import wooteco.subway.line.exception.LineError;
import wooteco.subway.line.exception.LineException;
import wooteco.subway.station.Station;
import wooteco.subway.station.StationDao;
import wooteco.subway.station.dto.StationResponse;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@Transactional
@Sql("/init-line.sql")
@SpringBootTest
class LineServiceTest {
    private static final Station JAMSIL_STATION = new Station(1L, "잠실역");
    private static final Station GANGNAM_STATION = new Station(2L, "강남역");
    private static final LineRequest LINE_REQUEST = new LineRequest("2호선", "초록색", 1L, 2L, 3);

    @MockBean(name = "stationDao")
    private StationDao stationDao;

    @Autowired
    private LineService lineService;

    @BeforeEach
    void setUp() {
        given(stationDao.findById(1L)).willReturn(Optional.of(JAMSIL_STATION));
        given(stationDao.findById(2L)).willReturn(Optional.of(GANGNAM_STATION));
    }

    @Test
    @DisplayName("노선 정상 생성")
    void createLine() {
        LineResponse lineResponse = lineService.createLine(LINE_REQUEST);
        List<StationResponse> stationResponses = Stream.of(JAMSIL_STATION, GANGNAM_STATION)
                                                       .map(StationResponse::new)
                                                       .collect(Collectors.toList());

        LineResponse expected = new LineResponse(1L, "2호선", "초록색", stationResponses);
        assertThat(lineResponse).usingRecursiveComparison()
                                .isEqualTo(expected);
    }

    @Test
    @DisplayName("노선 이름 중복 생성 실패")
    void createDuplicatedLine() {
        lineService.createLine(LINE_REQUEST);
        assertThatThrownBy(() -> lineService.createLine(LINE_REQUEST))
                .isInstanceOf(LineException.class)
                .hasMessage(LineError.ALREADY_EXIST_LINE_NAME.getMessage());
    }

    @Test
    @DisplayName("존재하지 않는 역을 경로로 가지는 노선 생성 실패")
    void createLineWithNotExistStation() {
        LineRequest lineRequest = new LineRequest("2호선", "초록색", 1L, 10L, 3);
        assertThatThrownBy(() -> lineService.createLine(lineRequest)).isInstanceOf(LineException.class)
                                                                     .hasMessage(LineError.NOT_EXIST_STATION_ON_LINE_REQUEST.getMessage());
    }
}