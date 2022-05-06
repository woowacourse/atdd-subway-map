package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;

@ExtendWith(MockitoExtension.class)
public class LineServiceTest {

    @InjectMocks
    private LineService lineService;

    @Mock
    private LineDao lineDao;

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // given
        LineRequest lineRequest = new LineRequest("test", "GREEN");
        given(lineDao.findByName("test"))
            .willReturn(Optional.empty());
        given(lineDao.save(any(Line.class)))
            .willReturn(new Line(1L, lineRequest.getName(), lineRequest.getColor()));
        // when
        LineResponse lineResponse = lineService.createLine(lineRequest);
        // then
        assertThat(lineResponse.getId()).isEqualTo(1L);
        assertThat(lineResponse.getName()).isEqualTo("test");
        assertThat(lineResponse.getColor()).isEqualTo("GREEN");
    }

    @DisplayName("지하철 노선 생성 시 이름이 중복된다면 에러를 응답한다.")
    @Test
    void createLine_duplicate_name_exception() {
        // given
        LineRequest lineRequest = new LineRequest("test", "GREEN");
        given(lineDao.findByName("test"))
            .willReturn(Optional.of(new Line(1L, lineRequest.getName(), "NotGreen")));
        // then
        assertThatThrownBy(() -> lineService.createLine(lineRequest))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("중복되는 이름의 지하철 노선이 존재합니다.");
    }

    @DisplayName("지하철 노선 생성 시 색깔이 중복된다면 에러를 응답한다.")
    @Test
    void createLine_duplicate_color_exception() {
        // given
        LineRequest lineRequest = new LineRequest("test", "GREEN");
        given(lineDao.findByName("test"))
            .willReturn(Optional.empty());
        given(lineDao.findByColor("GREEN"))
            .willReturn(Optional.of(new Line(1L, "NotTest", lineRequest.getColor())));
        // then
        assertThatThrownBy(() -> lineService.createLine(lineRequest))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("중복되는 색깔의 지하철 노선이 존재합니다.");
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        given(lineDao.findAll())
            .willReturn(List.of(new Line(1L, "test1", "GREEN"), new Line(2L, "test2", "YELLOW")));
        // when
        List<LineResponse> responses = lineService.showLines();
        // then
        assertThat(responses.size()).isEqualTo(2);
        assertThat(responses.get(0).getId()).isEqualTo(1L);
        assertThat(responses.get(0).getName()).isEqualTo("test1");
        assertThat(responses.get(0).getColor()).isEqualTo("GREEN");
        assertThat(responses.get(1).getId()).isEqualTo(2L);
        assertThat(responses.get(1).getName()).isEqualTo("test2");
        assertThat(responses.get(1).getColor()).isEqualTo("YELLOW");
    }

    @DisplayName("id를 이용해 지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        given(lineDao.findById(1L))
            .willReturn(Optional.of(new Line(1L, "test1", "GREEN")));
        // when
        LineResponse response = lineService.showLine(1L);
        // then
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("test1");
        assertThat(response.getColor()).isEqualTo("GREEN");
    }

    @DisplayName("존재하지 않는 id를 이용해 지하철 노선을 조회할 경우 에러가 발생한다.")
    @Test
    void getLine_noExistLine_exception() {
        // given
        given(lineDao.findById(1L))
            .willReturn(Optional.empty());
        // then
        assertThatThrownBy(() -> lineService.showLine(1L))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("해당하는 ID의 지하철 노선이 존재하지 않습니다.");
    }

    @DisplayName("노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        LineRequest updateRequest = new LineRequest("9호선", "GREEN");
        Line originLine = new Line(1L, "11호선", "GRAY");
        Line updateLine = new Line(1L, updateRequest.getName(), updateRequest.getColor());
        given(lineDao.findById(1L))
            .willReturn(Optional.of(originLine));
        given(lineDao.findByName("9호선")).willReturn(Optional.empty());
        // when
        lineService.updateLine(1L, updateRequest);
        // then
        verify(lineDao).update(originLine, updateLine);
    }

    @DisplayName("존재하지 않는 ID의 노선을 수정한다면 예외가 발생한다.")
    @Test
    void updateLine_noExistLine_Exception() throws Exception {
        // given
        LineRequest updateRequest = new LineRequest("9호선", "GREEN");
        given(lineDao.findById(1L))
            .willReturn(Optional.empty());
        // then
        assertThatThrownBy(() -> lineService.updateLine(1L, updateRequest))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("해당하는 ID의 지하철 노선이 존재하지 않습니다.");
    }

    @DisplayName("중복된 이름으로 노선을 수정한다.")
    @Test
    void updateLine_duplicate_name_exception() {
        // given
        LineRequest updateRequest = new LineRequest("9호선", "GREEN");
        given(lineDao.findById(1L))
            .willReturn(Optional.of(new Line(1L, "11호선", "GRAY")));
        given(lineDao.findByName("9호선"))
            .willReturn(Optional.of(new Line(2L, "9호선", "BLUE")));
        // then
        assertThatThrownBy(() -> lineService.updateLine(1L, updateRequest))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("중복되는 이름의 지하철 노선이 존재합니다.");
    }

    @DisplayName("중복된 색깔로 노선을 수정한다.")
    @Test
    void updateLine_duplicate_color_exception() throws Exception {
        // given
        LineRequest updateRequest = new LineRequest("9호선", "GREEN");
        given(lineDao.findById(1L))
            .willReturn(Optional.of(new Line(1L, "11호선", "GRAY")));
        given(lineDao.findByColor("GREEN"))
            .willReturn(Optional.of(new Line(2L, "12호선", "GREEN")));
        // then
        assertThatThrownBy(() -> lineService.updateLine(1L, updateRequest))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("중복되는 색깔의 지하철 노선이 존재합니다.");
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() throws Exception {
        // given
        Line line = new Line(1L, "test", "BLACK");
        given(lineDao.findById(1L))
            .willReturn(Optional.of(line));
        // when
        lineService.deleteLine(1L);
        // then
        verify(lineDao).delete(line);
    }

    @DisplayName("삭제 요청 시 ID에 해당하는 지하철 노선이 없다면 에러를 응답한다.")
    @Test
    void deleteLine_noExistLine_exception() throws Exception {
        // given
        given(lineDao.findById(1L))
            .willReturn(Optional.empty());
        // then
        assertThatThrownBy(() -> lineService.deleteLine(1L))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("해당하는 ID의 지하철 노선이 존재하지 않습니다.");
    }
}
