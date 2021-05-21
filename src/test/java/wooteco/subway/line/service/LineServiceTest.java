package wooteco.subway.line.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import wooteco.subway.line.controller.dto.LineNameColorResponse;
import wooteco.subway.line.controller.dto.LineRequest;
import wooteco.subway.line.controller.dto.LineResponse;
import wooteco.subway.line.controller.dto.SectionRequest;
import wooteco.subway.line.domain.Line;
import wooteco.subway.line.domain.repository.LineRepository;
import wooteco.subway.line.domain.section.Distance;
import wooteco.subway.line.domain.section.Section;
import wooteco.subway.line.domain.section.Sections;
import wooteco.subway.station.domain.Station;
import wooteco.subway.station.domain.Stations;
import wooteco.subway.station.service.StationService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class LineServiceTest {
    private List<Section> 생선된_구간들 = Collections.singletonList(new Section(1L, 1L, 2L, new Distance(10)));
    private LineRequest 노선_저장요청 = new LineRequest("1호선", "빨간색", 1L, 2L, 20);
    private LineRequest 노선_수정요청 = new LineRequest("222호선", "노란색", 1L, 2L, 20);
    private Line 노선_저장_후_응답 = new Line(1L, "1호선", "빨간색");
    private Line 노선_조회 = new Line(1L, "2호선", "파란색", new Sections(new ArrayList<>(생선된_구간들)));
    private Stations 지하철역들 = new Stations(Arrays.asList(new Station(1L, "강남역"), new Station(2L, "구의역")));

    @Mock
    LineRepository lineRepository = mock(LineRepository.class);

    @Mock
    StationService stationService = mock(StationService.class);

    @InjectMocks
    private LineService lineService;

    @Test
    void saveLine() {
        //given
        given(lineRepository.saveLine(any(Line.class))).willReturn(노선_저장_후_응답);

        //when
        LineNameColorResponse 저장_후_응답 = lineService.saveLine(노선_저장요청);

        //then
        assertThat(저장_후_응답.getName()).isEqualTo(노선_저장요청.getName());
        assertThat(저장_후_응답.getColor()).isEqualTo(노선_저장요청.getColor());

        then(lineRepository).should().saveLine(any(Line.class));
    }

    @Test
    void findAll() {
        //given
        given((lineRepository.findAll())).willReturn(new ArrayList<>());

        //when
        List<LineNameColorResponse> 라인_전체_조회 = lineService.findAll();

        //then
        assertThat(라인_전체_조회).isNotNull();
        then(lineRepository).should().findAll();
    }

    @Test
    void findById() {
        //given
        given(lineRepository.findLineSectionById(1L)).willReturn(노선_조회);
        given(stationService.findSortStationsByIds(any(List.class))).willReturn(지하철역들);

        //when
        LineResponse 조회한_노선 = lineService.findById(1L);

        //then
        assertThat(조회한_노선.getId()).isEqualTo(노선_조회.getId());
        assertThat(조회한_노선.getName()).isEqualTo(노선_조회.getName());
        assertThat(조회한_노선.getColor()).isEqualTo(노선_조회.getColor());
        then(lineRepository).should().findLineSectionById(1L);
    }

    @Test
    void delete() {
        //given
        willDoNothing().given(lineRepository).delete(1L);

        //when
        lineService.delete(1L);

        //then
        then(lineRepository).should().delete(1L);
    }

    @Test
    void update() {
        //given
        given(lineRepository.findLineSectionById(1L)).willReturn(노선_조회);
        willDoNothing().given(lineRepository).update(any(Line.class));

        //when
        lineService.update(1L, 노선_수정요청);

        //then
        then(lineRepository).should().findLineSectionById(1L);
        then(lineRepository).should().update(any(Line.class));
    }

    @Test
    void addSection() {
        //given
        willDoNothing().given(lineRepository).addSection(any(Section.class));

        //when
        lineService.addSection(1L, new SectionRequest(1L, 2L, 20));

        //then
        then(lineRepository).should().addSection(any(Section.class));
    }

    @Test
    void deleteSection() {
        //given
        willDoNothing().given(lineRepository).deleteSection(1L, 2L);

        //when
        lineService.deleteSection(1L, 2L);

        //then
        then(lineRepository).should().deleteSection(1L, 2L);
    }
}