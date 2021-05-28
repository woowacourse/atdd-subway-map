package wooteco.subway.line;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.line.dto.LineRequest;
import wooteco.subway.line.dto.LineResponse;
import wooteco.subway.line.exception.LineError;
import wooteco.subway.line.exception.LineException;
import wooteco.subway.line.service.LineService;
import wooteco.subway.section.dto.SectionRequest;
import wooteco.subway.section.exception.SectionError;
import wooteco.subway.section.exception.SectionException;
import wooteco.subway.station.domain.Station;
import wooteco.subway.station.dao.StationDao;
import wooteco.subway.station.dto.StationResponse;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
@Sql({"/init-line.sql", "/init-station.sql"})
@SpringBootTest
class LineServiceTest {
    private static final LineRequest LINE_REQUEST = new LineRequest("2호선", "초록색", 1L, 2L, 3);

    @Autowired
    private StationDao stationDao;

    @Autowired
    private LineService lineService;

    private Station 잠실역;
    private Station 강남역;
    private Station 강변역;
    private List<StationResponse> stationResponses;

    @BeforeEach
    void setUp() {
        잠실역 = stationDao.save("잠실역");
        강남역 = stationDao.save("강남역");
        강변역 = stationDao.save("강변역");
        stationResponses = Stream.of(잠실역, 강남역)
                                 .map(StationResponse::new)
                                 .collect(Collectors.toList());
    }

    @Test
    @DisplayName("노선 정상 생성")
    void createLine() {
        LineResponse lineResponse = lineService.createLine(LINE_REQUEST);

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

    @Test
    @DisplayName("id로 노선 정보 조회")
    void findByName() {
        lineService.createLine(LINE_REQUEST);
        LineResponse lineResponse = lineService.findById(1L);

        LineResponse expected = new LineResponse(1L, "2호선", "초록색", stationResponses);
        assertThat(lineResponse).usingRecursiveComparison()
                                .isEqualTo(expected);

    }

    @Test
    @DisplayName("노선 정보 업데이트 요청 확인")
    void updateLine() {
        String newName = "3호선";
        String newColor = "파란색";

        LineResponse lineResponse = lineService.createLine(LINE_REQUEST);
        Long lineId = lineResponse.getId();

        LineRequest modifyRequest = new LineRequest(newName, newColor, 1L, 2L, 3);

        lineService.modifyLine(lineId, modifyRequest);

        LineResponse expected = new LineResponse(lineId, newName, newColor, stationResponses);

        assertThat(lineService.findById(lineId)).usingRecursiveComparison()
                                                .isEqualTo(expected);
    }

    @Test
    @DisplayName("존재하지 않는 업데이트 요청 에러 발생")
    void updateLineNotExist() {
        String newName = "3호선";
        String newColor = "파란색";

        Long notExistLindId = 3L;

        LineRequest modifyRequest = new LineRequest(newName, newColor, 1L, 2L, 3);

        assertThatThrownBy(() -> lineService.modifyLine(notExistLindId, modifyRequest)).isInstanceOf(LineException.class)
                                                                                       .hasMessage(LineError.NOT_EXIST_LINE_ID.getMessage());
    }

    @Test
    @DisplayName("구간 추가")
    void addSection() {
        LineResponse lineResponse = lineService.createLine(LINE_REQUEST);
        Long lineId = lineResponse.getId();

        lineService.addSection(lineId, new SectionRequest(2L, 3L, 5));

        LineResponse afterAddSectionLineResponse = lineService.findById(lineId);

        List<StationResponse> stationResponses = Stream.of(잠실역, 강남역, 강변역)
                                                       .map(StationResponse::new)
                                                       .collect(Collectors.toList());

        assertThat(afterAddSectionLineResponse.getStations()).containsExactlyElementsOf(stationResponses);
    }

    @Test
    @DisplayName("존재하지 않는 노선에 구간 추가시 에러 발생")
    void addSectionToNotExistLine() {
        assertThatThrownBy(() ->
                lineService.addSection(3L, new SectionRequest(2L, 3L, 5)))
                .isInstanceOf(LineException.class)
                .hasMessage(LineError.NOT_EXIST_LINE_ID.getMessage());
    }

    @Test
    @DisplayName("구간 삭제")
    void deleteSection() {
        LineResponse lineResponse = lineService.createLine(LINE_REQUEST);
        Long lineId = lineResponse.getId();

        lineService.addSection(lineId, new SectionRequest(2L, 3L, 5));

        lineService.deleteSection(lineId, 잠실역.getId());

        LineResponse afterAddSectionLineResponse = lineService.findById(lineId);

        List<StationResponse> stationResponses = Stream.of(강남역, 강변역)
                                                       .map(StationResponse::new)
                                                       .collect(Collectors.toList());

        assertThat(afterAddSectionLineResponse.getStations()).containsExactlyElementsOf(stationResponses);
    }

    @Test
    @DisplayName("최소 크기 구간에서 구간 삭제시 에러 발생")
    void deleteSectionAtMinSize() {
        LineResponse lineResponse = lineService.createLine(LINE_REQUEST);
        Long lineId = lineResponse.getId();

        assertThatThrownBy(() ->
                lineService.deleteSection(lineId, 잠실역.getId()))
                .isInstanceOf(SectionException.class)
                .hasMessage(SectionError.CANNOT_DELETE_SECTION_SIZE_LESS_THAN_TWO.getMessage());
    }


    @Test
    @DisplayName("존재하지 않는 노선에 구간 삭제시 에러 발생")
    void deleteSectionToNotExistLine() {
        assertThatThrownBy(() ->
                lineService.deleteSection(3L, 잠실역.getId()))
                .isInstanceOf(LineException.class)
                .hasMessage(LineError.NOT_EXIST_LINE_ID.getMessage());
    }
}
