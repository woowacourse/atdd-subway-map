package wooteco.subway.line.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import wooteco.subway.line.domain.LineDao;
import wooteco.subway.line.domain.LineEntity;
import wooteco.subway.line.domain.SectionDao;
import wooteco.subway.line.dto.LineRequest;
import wooteco.subway.line.dto.LineResponse;
import wooteco.subway.line.dto.SectionAddRequest;
import wooteco.subway.line.entity.SectionEntity;
import wooteco.subway.station.domain.Station;
import wooteco.subway.station.domain.StationDao;
import wooteco.subway.station.dto.StationResponse;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class LineServiceTest {
    @InjectMocks
    private LineService lineService;

    @Mock
    private LineDao lineDao;

    @Mock
    private SectionDao sectionDao;

    @Mock
    private StationDao stationDao;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("노선 정상 저장된다")
    void save() {
        //given
        when(lineDao.save(any(LineEntity.class))).thenReturn(new LineEntity(1L, "신분당선", "화이트"));
        when(sectionDao.save(any(SectionEntity.class))).thenReturn(new SectionEntity(1L, 1L, 1L, 2L, 10));
        when(stationDao.findById(1L)).thenReturn(Optional.of(new Station(1L, "아마찌역")));
        when(stationDao.findById(2L)).thenReturn(Optional.of(new Station(2L, "검프역")));

        //when
        LineResponse lineResponse = lineService.save(new LineRequest("신분당선", "화이트", 1L, 2L, 10));

        //then
        assertThat(lineResponse.getId()).isEqualTo(1L);
        assertThat(lineResponse.getStations()).hasSize(2);
        assertThat(lineResponse.getStations().get(0).getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("노선에 구간을 추가한다.")
    void addSection() {
        Long lineId = 1L;
        SectionAddRequest sectionAddRequest = new SectionAddRequest(1L, 2L, 10);
        when(sectionDao.save(sectionAddRequest.toEntity(lineId))).thenReturn(null);
        lineService.addSection(1L, sectionAddRequest);
    }

    @Test
    @DisplayName("노선에 포함된 구간을 찾는다")
    void findBy() {
        //given
        when(lineDao.findById(1L)).thenReturn(Optional.of(new LineEntity(1L, "신분당선", "화이트")));
        when(stationDao.findById(1L)).thenReturn(Optional.of(new Station(1L, "아마찌역")));
        when(stationDao.findById(2L)).thenReturn(Optional.of(new Station(2L, "검프역")));
        when(stationDao.findById(3L)).thenReturn(Optional.of(new Station(3L, "마찌역")));
        when(sectionDao.findByLineId(1L)).thenReturn(Arrays.asList(new SectionEntity(1L, 1L, 1L, 2L, 10),
                new SectionEntity(2L, 1L, 2L, 3L, 6)));

        //when
        LineResponse response = lineService.getLine(1L);

        //then
        assertThat(response.getName()).isEqualTo("신분당선");
        assertThat(stationResponsesToString(response.getStations())).containsExactly("아마찌역", "검프역", "마찌역" );
    }

    private List<String> stationResponsesToString(List<StationResponse> response) {
        return response.stream().map(StationResponse::getName).collect(Collectors.toList());
    }
}