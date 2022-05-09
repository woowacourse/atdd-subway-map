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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

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

        given(sectionService.save(any()))
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

}
