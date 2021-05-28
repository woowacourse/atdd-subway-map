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
import wooteco.subway.line.service.LineService;
import wooteco.subway.station.dao.StationDao;
import wooteco.subway.station.domain.Station;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@Transactional
@SpringBootTest
class LineServiceTest {
    private static final Station 잠실역 = new Station(1L, "잠실역");
    private static final Station 강남역 = new Station(2L, "강남역");
    private static final Station 강변역 = new Station(3L, "강변역");

    private static final String STATION_NAME = "2호선";
    private static final String LINE_COLOR = "초록색";

    private static final LineRequest LINE_REQUEST = new LineRequest(STATION_NAME, LINE_COLOR, 잠실역.getId(), 강남역.getId(), 3);

    @MockBean(name = "stationDao")
    private StationDao stationDao;

    @Autowired
    private LineService lineService;

    @BeforeEach
    void setUp() {
        given(stationDao.findById(1L)).willReturn(Optional.of(잠실역));
        given(stationDao.findById(2L)).willReturn(Optional.of(강남역));
        given(stationDao.findById(3L)).willReturn(Optional.of(강변역));
    }

    @Test
    @DisplayName("노선 정상 생성")
    void createLine() {
        LineResponse lineResponse = lineService.createLine(LINE_REQUEST);

        assertThat(lineResponse.getName()).isEqualTo(STATION_NAME);
        assertThat(lineResponse.getColor()).isEqualTo(LINE_COLOR);
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
        Long notExistLineId = 9999L;
        LineRequest lineRequest = new LineRequest("2호선", "초록색", 잠실역.getId(), notExistLineId, 3);
        assertThatThrownBy(() -> lineService.createLine(lineRequest)).isInstanceOf(LineException.class)
                .hasMessage(LineError.NOT_EXIST_STATION_ON_LINE_REQUEST.getMessage());
    }

    @Test
    @DisplayName("id로 노선 정보 조회")
    void findByName() {
        LineResponse lineResponse = lineService.createLine(LINE_REQUEST);

        LineResponse foundResponse = lineService.findById(lineResponse.getId());
        assertThat(foundResponse.getName()).isEqualTo(STATION_NAME);
        assertThat(foundResponse.getColor()).isEqualTo(LINE_COLOR);
    }

    @Test
    @DisplayName("노선 정보 업데이트 요청 확인")
    void updateLine() {
        String newName = "3호선";
        String newColor = "파란색";

        LineResponse lineResponse = lineService.createLine(LINE_REQUEST);
        Long lineId = lineResponse.getId();

        LineRequest modifyRequest = new LineRequest(newName, newColor);

        lineService.modifyLine(lineId, modifyRequest);

        LineResponse modifiedLine = lineService.findById(lineId);
        assertThat(modifiedLine.getName()).isEqualTo(newName);
        assertThat(modifiedLine.getColor()).isEqualTo(newColor);
    }

    @Test
    @DisplayName("존재하지 않는 노선 업데이트 요청 에러 발생")
    void updateLineNotExist() {
        String newName = "3호선";
        String newColor = "파란색";

        Long notExistLindId = 9999L;

        LineRequest modifyRequest = new LineRequest(newName, newColor);

        assertThatThrownBy(() -> lineService.modifyLine(notExistLindId, modifyRequest)).isInstanceOf(LineException.class)
                .hasMessage(LineError.NOT_EXIST_LINE_ID.getMessage());
    }
}
