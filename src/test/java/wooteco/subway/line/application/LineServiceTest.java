package wooteco.subway.line.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import wooteco.subway.line.domain.LineDao;
import wooteco.subway.line.domain.SectionDao;
import wooteco.subway.line.dto.LineRequest;
import wooteco.subway.line.dto.LineResponse;
import wooteco.subway.line.dto.SectionRequest;
import wooteco.subway.line.entity.LineEntity;
import wooteco.subway.line.entity.SectionEntity;
import wooteco.subway.station.domain.Station;
import wooteco.subway.station.domain.StationDao;
import wooteco.subway.station.dto.StationResponse;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
        when(lineDao.save(any(LineEntity.class))).thenReturn(new LineEntity(1L, "신분당선", "bg-red-600"));
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
    @DisplayName("구건 저장 시 상행역과 하행역이 같으면예와가 발생한다")
    void saveException() {
        //given
        when(lineDao.save(any(LineEntity.class))).thenReturn(new LineEntity(1L, "신분당선", "bg-red-600"));

        //then
        assertThatThrownBy(() -> lineService.save(new LineRequest("신분당선", "화이트", 1L, 1L, 10)))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("노선에 포함된 구간을 찾는다")
    void findBy() {
        //given
        baseLine();
        when(sectionDao.findByLineId(1L)).thenReturn(Arrays.asList(
                new SectionEntity(2L, 1L, 4L, 3L, 5),
                new SectionEntity(3L, 1L, 1L, 4L, 7)));

        //when
        LineResponse response = lineService.findLine(1L);

        //then
        assertThat(response.getName()).isEqualTo("신분당선");
        assertThat(stationResponsesToString(response.getStations())).containsExactly("아마찌역", "낙성대역", "마찌역");
    }

    @Test
    @DisplayName("구간을 제거한다. (상행 종점역)")
    void deleteByStationId() {
        //given
        Long lineId = 1L;
        Long targetStationId = 1L;

        baseLine();
        when(sectionDao.findByLineId(1L)).thenReturn(Arrays.asList(
                new SectionEntity(2L, lineId, 1L, 2L, 5),
                new SectionEntity(3L, lineId, 2L, 3L, 7)
        ));
        when(sectionDao.findByLineIdWithUpStationId(lineId, targetStationId))
                .thenReturn(Optional.of(new SectionEntity(2L, lineId, 1L, targetStationId, 5)));
        when(sectionDao.findByLineIdWithDownStationId(lineId, targetStationId))
                .thenReturn(Optional.of(new SectionEntity(2L, lineId, targetStationId, 3L, 7)));

        //when
        lineService.deleteSectionByStationId(lineId, targetStationId);

        when(sectionDao.findByLineId(1L)).thenReturn(Collections.singletonList(
                new SectionEntity(2L, lineId, 2L, 3L, 12)
        ));

        LineResponse response = lineService.findLine(lineId);

        //then
        assertThat(stationResponsesToString(response.getStations())).containsExactly("검프역", "마찌역");
    }

    @Test
    @DisplayName("구간을 제거한다. (중간)")
    void deleteMiddleStation() {
        //given
        Long lineId = 1L;
        Long targetStationId = 2L;

        baseLine();
        when(sectionDao.findByLineId(1L)).thenReturn(Arrays.asList(
                new SectionEntity(2L, lineId, 1L, 2L, 5),
                new SectionEntity(3L, lineId, 2L, 3L, 7)
        ));
        when(sectionDao.findByLineIdWithUpStationId(lineId, targetStationId))
                .thenReturn(Optional.of(new SectionEntity(2L, lineId, 1L, targetStationId, 5)));
        when(sectionDao.findByLineIdWithDownStationId(lineId, targetStationId))
                .thenReturn(Optional.of(new SectionEntity(2L, lineId, targetStationId, 3L, 7)));

        //when
        lineService.deleteSectionByStationId(lineId, targetStationId);

        when(sectionDao.findByLineId(1L)).thenReturn(Collections.singletonList(
                new SectionEntity(2L, lineId, 1L, 3L, 12)
        ));

        LineResponse response = lineService.findLine(lineId);

        //then
        assertThat(stationResponsesToString(response.getStations())).containsExactly("아마찌역", "마찌역");
    }

    @Test
    @DisplayName("구간 등록시 상행역과 하행역이 이미 등록 되어있다면 에러가 발생한다. ")
    void registrationDuplicateException() {
        //given
        baseLine();
        long lineId = 1L;
        long upStationId = 1L;
        long downStationId = 2L;
        int distance = 2;

        beforeSaveLineSection(lineId, upStationId, downStationId, distance);
        //when
        when(sectionDao.findByLineIdWithUpStationId(lineId, upStationId)).thenReturn(Optional.of(new SectionEntity()));
        when(sectionDao.findByLineIdWithDownStationId(lineId, downStationId)).thenReturn(Optional.of(new SectionEntity()));
        //then
        assertThatThrownBy(() -> lineService.addSection(lineId, new SectionRequest(upStationId, downStationId, distance)))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("상행 종점 등록 로직")
    void upwardEndPointRegistration() {
        //given
        baseLine();
        long lineId = 1L;
        long upStationId = 4L;
        long downStationId = 1L;
        int distance = 10;

        beforeSaveLineSection(lineId, upStationId, downStationId, distance);

        //when
        lineService.addSection(lineId, new SectionRequest(upStationId, downStationId, distance));
        when(sectionDao.findByLineId(1L)).thenReturn(Arrays.asList(
                new SectionEntity(2L, lineId, 1L, 2L, 5),
                new SectionEntity(3L, lineId, 2L, 3L, 7),
                new SectionEntity(4L, lineId, upStationId, downStationId, distance)
        ));
        LineResponse response = lineService.findLine(lineId);

        //then
        assertThat(response.getName()).isEqualTo("신분당선");
        assertThat(stationResponsesToString(response.getStations())).containsExactly("낙성대역", "아마찌역", "검프역", "마찌역");
    }

    @Test
    @DisplayName("하행 종점 등록 로직")
    void downwardEndPointRegistration() {
        //given
        baseLine();
        long lineId = 1L;
        long upStationId = 3L;
        long downStationId = 4L;
        int distance = 10;

        beforeSaveLineSection(lineId, upStationId, downStationId, distance);

        //when
        lineService.addSection(lineId, new SectionRequest(upStationId, downStationId, distance));

        when(sectionDao.findByLineId(1L)).thenReturn(Arrays.asList(
                new SectionEntity(2L, lineId, 1L, 2L, 5),
                new SectionEntity(3L, lineId, 2L, 3L, 7),
                new SectionEntity(4L, lineId, upStationId, downStationId, distance)
        ));

        LineResponse response = lineService.findLine(lineId);

        //then
        assertThat(response.getName()).isEqualTo("신분당선");
        assertThat(stationResponsesToString(response.getStations())).containsExactly("아마찌역", "검프역", "마찌역", "낙성대역");
    }

    @Test
    @DisplayName("갈래길 방지 상행역 추가 로직")
    void betweenUpwardRegistration() {
        //given
        baseLine();
        long lineId = 1L;
        long upStationId = 1L;
        long downStationId = 4L;
        int distance = 3;

        beforeSaveLineSection(lineId, upStationId, downStationId, distance);

        //when
        lineService.addSection(lineId, new SectionRequest(upStationId, downStationId, distance));
        when(sectionDao.findByLineId(1L)).thenReturn(Arrays.asList(
                new SectionEntity(2L, lineId, upStationId, downStationId, 5),
                new SectionEntity(4L, lineId, downStationId, 2L, distance),
                new SectionEntity(3L, lineId, 2L, 3L, 7 - distance)
        ));

        LineResponse response = lineService.findLine(lineId);

        //then
        assertThat(response.getName()).isEqualTo("신분당선");
        assertThat(stationResponsesToString(response.getStations())).containsExactly("아마찌역", "낙성대역", "검프역", "마찌역");
    }

    @Test
    @DisplayName("갈래길 방지 하행역 추가 로직")
    void betweenDownwardRegistration() {
        //given
        baseLine();
        long lineId = 1L;
        long upStationId = 4L;
        long downStationId = 3L;
        int distance = 3;

        beforeSaveLineSection(lineId, upStationId, downStationId, distance);

        //when
        lineService.addSection(lineId, new SectionRequest(upStationId, downStationId, distance));
        when(sectionDao.findByLineId(1L)).thenReturn(Arrays.asList(
                new SectionEntity(2L, lineId, 1L, 2L, 5),
                new SectionEntity(4L, lineId, 2L, upStationId, 7 - distance),
                new SectionEntity(3L, lineId, upStationId, downStationId, distance)
        ));

        LineResponse response = lineService.findLine(lineId);

        //then
        assertThat(response.getName()).isEqualTo("신분당선");
        assertThat(stationResponsesToString(response.getStations())).containsExactly("아마찌역", "검프역", "낙성대역", "마찌역");
    }

    private void baseLine() {
        when(lineDao.findById(1L)).thenReturn(Optional.of(new LineEntity(1L, "신분당선", "bg-red-600")));
        when(stationDao.findById(1L)).thenReturn(Optional.of(new Station(1L, "아마찌역")));
        when(stationDao.findById(2L)).thenReturn(Optional.of(new Station(2L, "검프역")));
        when(stationDao.findById(3L)).thenReturn(Optional.of(new Station(3L, "마찌역")));
        when(stationDao.findById(4L)).thenReturn(Optional.of(new Station(4L, "낙성대역")));
    }

    private void beforeSaveLineSection(long lineId, long upStationId, long downStationId, int distance) {
        when(sectionDao.save(any(SectionEntity.class))).thenReturn(new SectionEntity(lineId, upStationId, downStationId, distance));
        when(sectionDao.findByLineId(1L)).thenReturn(Arrays.asList(
                new SectionEntity(2L, lineId, 1L, 2L, 5),
                new SectionEntity(3L, lineId, 2L, 3L, 7)
        ));
    }

    private List<String> stationResponsesToString(List<StationResponse> response) {
        return response.stream()
                .map(StationResponse::getName)
                .collect(Collectors.toList());
    }
}
