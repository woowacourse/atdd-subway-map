package wooteco.subway.line;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyObject;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito.BDDMyOngoingStubbing;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import wooteco.subway.line.section.SectionRequest;
import wooteco.subway.line.section.SectionResponse;
import wooteco.subway.line.section.SectionService;

@ExtendWith(MockitoExtension.class)
class LineServiceTest {

    @InjectMocks
    private LineService lineService;

    @Mock
    private SectionService sectionService;

    @Mock
    private LineDao lineDao;

    @DisplayName("노선을 생성한다.")
    @Test
    void createLine() {
        final String name = "2호선";
        final String color = "black";
        final Long upStationId = 2L;
        final Long downStationId = 4L;
        final int distance = 10;
        final int extraFare = 1000;

        final Line line = new Line(name, color);
        final LineRequest lineRequest = spy(new LineRequest(name, color, upStationId, downStationId, distance, extraFare));
        given(lineDao.save(line)).willReturn(new Line(1L, name, color));
        given(sectionService.createSection(eq(1L), any(SectionRequest.class))).willReturn(new SectionResponse());
        final LineResponse createdLine = lineService.createLine(lineRequest);

        verify(lineRequest, times(1)).toEntity();
        verify(lineDao, times(1)).save(line);
        verify(sectionService, times(1)).createSection(eq(1L), any(SectionRequest.class));
        assertThat(createdLine.getId()).isEqualTo(1L);
    }
}