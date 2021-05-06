package wooteco.subway.line.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import wooteco.subway.line.api.dto.LineRequest;
import wooteco.subway.line.api.dto.LineResponse;
import wooteco.subway.line.dao.LineDao;
import wooteco.subway.line.model.Line;

@MockitoSettings
class LineServiceTest {

    @Mock
    private LineDao lineDao;

    @InjectMocks
    private LineService lineService;

    @DisplayName("전체 노선 조회")
    @Test
    void findAll() {
        //given
        when(lineDao.findAll())
            .thenReturn(Arrays.asList(new Line(1L, "2호선", "green"),
                new Line(2L, "3호선", "orange")));

        //when
        List<LineResponse> lineResponses = lineService.findAll();

        //then
        assertThat(lineResponses.get(0).getName()).isEqualTo("2호선");
        assertThat(lineResponses.get(1).getName()).isEqualTo("3호선");
    }

    @DisplayName("노선 생성")
    @Test
    void createLine() {
        //given
        when(lineDao.save(any()))
            .thenReturn(new Line(1L, "2호선", "green"));
        //when
        LineResponse lineResponse = lineService.createLine(new LineRequest("2호선", "green"));
        //then
        assertThat(lineResponse.getId()).isEqualTo(1L);
        assertThat(lineResponse.getName()).isEqualTo("2호선");
        assertThat(lineResponse.getColor()).isEqualTo("green");
    }

    @DisplayName("단일 노선 조회")
    @Test
    void showLineById() {
        //given
        when(lineDao.findLineById(1L))
            .thenReturn(new Line(1L, "2호선", "green"));
        //when
        LineResponse lineResponse = lineService.showLineById(1L);
        //then
        assertThat(lineResponse.getId()).isEqualTo(1L);
        assertThat(lineResponse.getName()).isEqualTo("2호선");
        assertThat(lineResponse.getColor()).isEqualTo("green");
    }
}
