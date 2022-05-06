package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

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

@ExtendWith(MockitoExtension.class)
class LineServiceTest {

    @Mock
    private JdbcLineDao JdbcLineDao;

    @InjectMocks
    private LineService lineService;

    @DisplayName("지하철 노선을 등록한다.")
    @Test
    void createLine() {
        doReturn(1L)
                .when(JdbcLineDao).save(any(Line.class));

        LineResponse lineResponse = lineService.createLine(new LineRequest("신분당선", "bg-red-600"));

        assertAll(
                () -> lineResponse.getId().equals(1L),
                () -> lineResponse.getName().equals("신분당선"),
                () -> lineResponse.getColor().equals("bg-red-600")
        );
    }

    @DisplayName("지하철 노선을 중복 등록하면, 예외가 발생한다.")
    @Test
    void createDuplicatedLine() {
        doThrow(new IllegalArgumentException("이미 등록된 지하철 노선입니다."))
                .when(JdbcLineDao).save(any(Line.class));

        assertThatThrownBy(() -> lineService.createLine(new LineRequest("신분당선", "bg-red-600")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미 등록된 지하철 노선입니다.");
    }

    @DisplayName("지하철 노선 전체 목록을 조회한다.")
    @Test
    void getLines() {
        doReturn(List.of(new Line("신분당선", "bg-red-600"), new Line("분당선", "bg-green-600")))
                .when(JdbcLineDao).findAll();

        List<LineResponse> lineResponses = lineService.getLines();

        assertThat(lineResponses.size()).isEqualTo(2);
    }

    @DisplayName("지하철 노선 하나를 조회한다.")
    @Test
    void getLine() {
        doReturn(new Line(1L, "신분당선", "bg-red-600"))
                .when(JdbcLineDao)
                .findById(1L);

        LineResponse lineResponse = lineService.getLine(1L);

        assertAll(
                () -> lineResponse.getId().equals(1L),
                () -> lineResponse.getName().equals("신분당선"),
                () -> lineResponse.getColor().equals("bg-red-600")
        );
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        doReturn(true)
                .when(JdbcLineDao)
                .updateById(anyLong(), any(Line.class));

        boolean isUpdated = lineService.updateLine(1L, new LineRequest("분당선", "bg-green-600"));
        assertThat(isUpdated).isTrue();
    }

    @DisplayName("지하철 노선을 삭제한다.")
    @Test
    void deleteLine() {
        doReturn(true)
                .when(JdbcLineDao)
                .deleteById(1L);

        boolean isDeleted = lineService.deleteLine(1L);
        assertThat(isDeleted).isTrue();
    }
}
