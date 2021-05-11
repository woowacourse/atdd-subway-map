package wooteco.subway.line;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import wooteco.subway.line.section.Section;
import wooteco.subway.line.section.SectionDao;
import wooteco.subway.line.section.SectionRequest;
import wooteco.subway.line.section.SectionResponse;
import wooteco.subway.station.StationService;

@ExtendWith(MockitoExtension.class)
class LineServiceTest {

    @InjectMocks
    private LineService lineService;

    @Mock
    private LineDao lineDao;

    @Mock
    private SectionDao sectionDao;

    @Mock
    private StationService stationService;

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

        final Section section = new Section(1L, upStationId, downStationId, distance);
        given(sectionDao.save(section)).willReturn(new Section(1L, 1L, upStationId, downStationId, distance));
        final LineResponse createdLine = lineService.createLine(lineRequest);

        verify(lineRequest, times(1)).toEntity();
        verify(lineDao, times(1)).save(line);
        verify(sectionDao, times(1)).save(section);
        assertThat(createdLine.getId()).isEqualTo(1L);
    }

    @DisplayName("구간을 등록한다.")
    @Test
    void addSection() {
        final Line line = new Line(1L, "2호선", "black");
        final Section sectionA = new Section(1L, 1L, 2L, 4L, 10);
        final Section sectionB = new Section(2L, 1L, 4L, 6L, 10);
        final List<Section> sectionGroup = Arrays.asList(sectionA, sectionB);

        final Long lineId = 1L;
        final SectionRequest sectionRequest = new SectionRequest(2L, 3L, 7);
        given(lineDao.findById(1L)).willReturn(Optional.of(line));
        given(sectionDao.findByLineId(1L)).willReturn(sectionGroup);


        final Section expectedSection = new Section(
            10L, lineId, sectionRequest.getUpStationId(), sectionRequest.getDownStationId(), sectionRequest.getDistance()
        );

        given(sectionDao.save(sectionRequest.toEntity(lineId))).willReturn(expectedSection);

        final SectionResponse sectionResponse = lineService.addSection(lineId, sectionRequest);
        assertThat(sectionResponse.getId()).isEqualTo(expectedSection.getId());

        verify(lineDao, times(1)).findById(1L);
        verify(sectionDao, times(1)).findByLineId(1L);
        verify(sectionDao, times(1)).update(any(Section.class));
        verify(sectionDao, times(1)).save(any(Section.class));
    }

    @DisplayName("구간을 제거한다.")
    @Test
    void deleteSection() {
        final Line line = new Line(1L, "2호선", "black");
        final Section sectionA = new Section(1L, 1L, 2L, 4L, 10);
        final Section sectionB = new Section(2L, 1L, 4L, 6L, 10);
        final List<Section> sectionGroup = Arrays.asList(sectionA, sectionB);

        final Long lineId = 1L;
        given(lineDao.findById(1L)).willReturn(Optional.of(line));
        given(sectionDao.findByLineId(1L)).willReturn(sectionGroup);

        lineService.deleteSection(lineId, 4L);
        verify(sectionDao, times(2)).deleteById(anyLong());
        verify(sectionDao, times(1))
            .save(new Section(1L, 2L, 6L, 20));
    }
}
