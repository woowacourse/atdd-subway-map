package wooteco.subway.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.exception.line.NoSuchLineException;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

public class LineServiceTest extends ServiceTest {

    @InjectMocks
    private LineService lineService;
    @Mock
    private SectionService sectionService;
    @Mock
    private StationService stationService;

    @DisplayName("line이 올바르게 저장되는지 확인한다")
    @Test
    void save() {
        //given
        LineRequest request = new LineRequest("2호선", "green", 1L, 2L, 10);
        Line line = new Line(1L, "2호선", "green");
        Section section = new Section(1L, 10, 1L, 1L, 2L);
        Station s1 = new Station(1L, "선릉역");
        Station s2 = new Station(2L, "강남역");

        given(lineDao.save(any()))
                .willReturn(line);
        given(sectionService.init(any()))
                .willReturn(section);
        given(stationService.findById(1L))
                .willReturn(s1);
        given(stationService.findById(2L))
                .willReturn(s2);

        //when
        LineResponse response = lineService.save(request);

        //then
        assertThat(response.getStations().size()).isEqualTo(2);
    }

    @DisplayName("line 목록을 조회한다.")
    @Test
    void findAll() {
        Line line1 = new Line(1L, "1호선", "blue");
        Line line2 = new Line(2L, "2호선", "green");
        Station station1 = new Station(1L, "왕십리역");
        Station station2 = new Station(2L, "시청역");
        Station station3 = new Station(3L, "잠실역");
        Station station4 = new Station(4L, "선릉역");

        given(lineDao.findAll())
                .willReturn(List.of(line1, line2));
        given(stationService.findAll())
                .willReturn(List.of(station1, station2, station3, station4));
        when(sectionService.getStationIds(1L))
                .thenReturn(Set.of(1L, 2L));
        when(sectionService.getStationIds(2L))
                .thenReturn(Set.of(2L, 3L, 4L));

        //when
        List<LineResponse> responses = lineService.findAll();

        //then
        assertThat(responses.size()).isEqualTo(2);
        assertThat(responses.get(0).getStations()).isNotEmpty();
    }

    @DisplayName("line id로 line 목록을 조회한다.")
    @Test
    void findById() {
        Line line1 = new Line(1L, "1호선", "blue");
        Station station1 = new Station(1L, "왕십리역");
        Station station2 = new Station(2L, "시청역");

        given(lineDao.findById(any()))
                .willReturn(Optional.of(line1));
        given(stationService.findAll())
                .willReturn(List.of(station1, station2));
        when(sectionService.getStationIds(1L))
                .thenReturn(Set.of(1L, 2L));

        //when
        LineResponse response = lineService.findById(1L);

        //then
        assertThat(response.getStations().size()).isEqualTo(2);
    }

    @DisplayName("존재하지 않는 line id로 line 목록을 조회한다.")
    @Test
    void findByIdThrowException() {
        given(lineDao.findById(any()))
                .willReturn(Optional.empty());

        //when & then
        assertThatThrownBy(() -> {
            lineService.findById(1L);
        }).isInstanceOf(NoSuchLineException.class);
    }
}
