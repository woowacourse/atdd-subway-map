package wooteco.subway.line;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LineServiceTest {

    @InjectMocks
    private LineService lineService;

    @Mock
    private LineDao lineDao;

    @DisplayName("노선을 생성한다.")
    @Test
    void createLine() {
        final String name = "2호선";
        final String color = "black";
        final Line line = new Line(name, color);
        final LineRequest lineRequest = spy(new LineRequest(name, color));
        given(lineDao.save(line)).willReturn(new Line(1L, name, color));

        final LineResponse createdLine = lineService.createLine(lineRequest);

        verify(lineRequest, times(1)).toEntity();
        verify(lineDao, times(1)).save(line);
        assertThat(createdLine.getId()).isEqualTo(1L);
    }
}