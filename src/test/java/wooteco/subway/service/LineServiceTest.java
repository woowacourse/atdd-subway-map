package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.doReturn;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import wooteco.subway.dao.JdbcLineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.StationResponse;

@ExtendWith(MockitoExtension.class)
class LineServiceTest {

    @Mock
    private JdbcLineDao jdbcLineDao;

    @Mock
    private SectionService sectionService;

    @InjectMocks
    private LineService lineService;

    @DisplayName("지하철 노선을 등록한다.")
    @Test
    void createLine() {
        doReturn(1L)
                .when(jdbcLineDao)
                .save("신분당선", "bg-red-600");

        doReturn(List.of(
                new StationResponse(1L, "강남역"),
                new StationResponse(2L, "사당역")
        ))
                .when(sectionService)
                .getStationsByLineId(1L);

        LineResponse lineResponse = lineService.createLine(new LineRequest("신분당선", "bg-red-600", 1L, 2L, 5));

        assertAll(
                () -> lineResponse.getId().equals(1L),
                () -> lineResponse.getName().equals("신분당선"),
                () -> lineResponse.getColor().equals("bg-red-600")
        );

        assertThat(lineResponse.getStations().size()).isEqualTo(2);
    }

    @DisplayName("지하철 노선 전체 목록을 조회한다.")
    @Test
    void getLines() {
        doReturn(List.of(new Line(1L, "신분당선", "bg-red-600"), new Line(2L, "분당선", "bg-green-600")))
                .when(jdbcLineDao).findAll();

        List<LineResponse> lineResponses = lineService.getLines();

        assertThat(lineResponses.size()).isEqualTo(2);
    }

    @DisplayName("지하철 노선 하나를 조회한다.")
    @Test
    void getLine() {
        doReturn(new Line(1L, "신분당선", "bg-red-600"))
                .when(jdbcLineDao)
                .findById(1L);

        doReturn(List.of(
                new StationResponse(1L, "강남역"),
                new StationResponse(2L, "양재역")
        ))
                .when(sectionService)
                .getStationsByLineId(1L);

        LineResponse lineResponse = lineService.getLine(1L);

        assertAll(
                () -> lineResponse.getId().equals(1L),
                () -> lineResponse.getName().equals("신분당선"),
                () -> lineResponse.getColor().equals("bg-red-600")
        );
        assertThat(lineResponse.getStations().size()).isEqualTo(2);
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        doReturn(true)
                .when(jdbcLineDao)
                .updateById(1L, "분당선", "bg-green-600");

        boolean isUpdated = lineService.updateLine(1L, new LineRequest("분당선", "bg-green-600"));
        assertThat(isUpdated).isTrue();
    }

    @DisplayName("지하철 노선을 삭제한다.")
    @Test
    void deleteLine() {
        doReturn(true)
                .when(jdbcLineDao)
                .deleteById(1L);

        boolean isDeleted = lineService.deleteLine(1L);
        assertThat(isDeleted).isTrue();
    }
}
