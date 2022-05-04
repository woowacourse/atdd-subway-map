package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.util.List;
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
class LineServiceTest {

    @InjectMocks
    private LineService lineService;

    @Mock
    private LineDao lineDao;

    @Test
    @DisplayName("노선을 생성한다.")
    void createLine() {
        // given
        final long id = 1L;
        final String name = "2호선";
        final String color = "bg-red-600";
        final Line savedLine = new Line(id, name, color);

        // mocking
        given(lineDao.save(any())).willReturn(id);
        given(lineDao.find(id)).willReturn(savedLine);

        // when
        final LineRequest request = new LineRequest(name, color);
        final LineResponse response = lineService.createLine(request);

        // then
        assertAll(() -> {
            assertThat(response.getName()).isEqualTo(request.getName());
            assertThat(response.getColor()).isEqualTo(request.getColor());
        });
    }

    @Test
    @DisplayName("모든 노선을 조회한다.")
    void showLines() {
        // given
        final List<Line> value = List.of(new Line("신분당선", "bg-red-600"),
                new Line("분당선", "bg-black-000"));

        // mocking
        given(lineDao.findAll()).willReturn(value);

        // when
        final List<LineResponse> responses = lineService.showLines();

        // then
        assertThat(responses).hasSize(2);
    }

    @Test
    @DisplayName("노선을 조회한다.")
    void showLine() {
        // given
        final long id = 1L;
        final String name = "2호선";
        final String color = "bg-red-600";

        // mocking
        given(lineDao.find(id)).willReturn(new Line(id, name, color));

        // when
        final LineResponse response = lineService.showLine(id);

        // then
        assertAll(() -> {
            assertThat(response.getName()).isEqualTo(name);
            assertThat(response.getColor()).isEqualTo(color);
        });
    }

    @Test
    @DisplayName("노선을 업데이트 한다.")
    void updateLine() {
        // given
        final long id = 1L;
        final String name = "2호선";
        final String color = "bg-red-600";

        // mocking
        given(lineDao.find(id)).willReturn(new Line(id, name, color));

        // when
        lineService.updateLine(id, new LineRequest(name, color));

        // then
        verify(lineDao).update(id, name, color);
    }

    @Test
    @DisplayName("노선을 삭제한다.")
    void deleteLine() {
        // given
        long id = 1L;
        final String name = "2호선";
        final String color = "bg-red-600";

        // mocking
        given(lineDao.find(id)).willReturn(new Line(id, name, color));

        // when
        lineService.deleteLine(id);

        // then
        verify(lineDao).delete(id);
    }
}
