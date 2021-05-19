package wooteco.subway.line.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import wooteco.subway.common.exception.AlreadyExistsException;
import wooteco.subway.common.exception.NotFoundException;
import wooteco.subway.line.domain.Line;
import wooteco.subway.line.domain.LineDao;
import wooteco.subway.line.domain.Section;
import wooteco.subway.line.domain.SectionDao;
import wooteco.subway.line.dto.LineRequest;
import wooteco.subway.line.dto.LineResponse;
import wooteco.subway.line.dto.SectionRequest;
import wooteco.subway.station.domain.Station;
import wooteco.subway.station.domain.StationDao;
import wooteco.subway.station.dto.StationResponse;

import java.util.Arrays;
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

    private Station station1;
    private Station station2;
    private Station station3;
    private Station station4;
    private Line line;
    private Section section1;
    private Section section2;
    private int distance1;
    private int distance2;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);

        station1 = new Station(1L, "1역");
        station2 = new Station(2L, "2역");
        station3 = new Station(3L, "3역");
        station4 = new Station(4L, "4역");
        line = new Line(1L, "백기선", "bg-red-600");
        distance1 = 10;
        distance2 = 5;
        section1 = new Section(1L, line, station1, station2, distance1);
        section2 = new Section(2L, line, station2, station3, distance2);
    }

    @Test
    @DisplayName("노선 정상 저장된다")
    void save() {
        //given
        when(lineDao.save(any(Line.class))).thenReturn(line);
        when(sectionDao.save(any(Section.class))).thenReturn(section1);
        when(stationDao.findById(1L)).thenReturn(Optional.of(station1));
        when(stationDao.findById(2L)).thenReturn(Optional.of(station2));

        //when
        LineResponse lineResponse = lineService.save(new LineRequest(line.name(), line.color(), station1.id(), station2.id(), distance1));

        //then
        assertThat(lineResponse.getId()).isEqualTo(line.id());
        assertThat(lineResponse.getStations()).hasSize(2);
        assertThat(lineResponse.getStations().get(0).getId()).isEqualTo(station1.id());
    }

    @Test
    @DisplayName("구건 저장 시 상행역과 하행역이 같으면 예와가 발생한다")
    void saveException() {
        //given
        when(lineDao.save(any(Line.class))).thenReturn(line);

        //then
        assertThatThrownBy(() -> lineService.save(new LineRequest(line.name(), line.color(), station1.id(), station1.id(), distance1)))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("노선에 포함된 구간을 찾는다")
    void findBy() {
        //given
        line.addSection(section1);
        line.addSection(section2);

        baseLine();

        when(lineDao.findById(line.id())).thenReturn(Optional.of(line));

        //when
        LineResponse response = lineService.findLine(line.id());


        //then
        assertThat(response.getName()).isEqualTo(line.name());
        assertThat(stationResponsesToString(response.getStations())).containsExactly(station1.name(), station2.name(), station3.name());
    }

    @Test
    @DisplayName("구간 등록시 상행역과 하행역이 이미 등록 되어있다면 에러가 발생한다. ")
    void registrationDuplicateException() {
        //given
        baseLine();
        line.addSection(section1);
        line.addSection(section2);

        //when

        //then
        assertThatThrownBy(() -> lineService.addSection(line.id(), new SectionRequest(section1.upStationId(), section1.downStationId(), distance1)))
                .isInstanceOf(AlreadyExistsException.class);
    }

    @Test
    @DisplayName("초기 구간 등록시 상행역과 하행역의 아이디가 없으면 예외가 발생한다. ")
    void registrationNotExistException() {
        //given
        Long stationId = null;
        baseLine();

        //when

        //then
        assertThatThrownBy(() -> lineService.save(new LineRequest(line.name(), line.color(), stationId, stationId, distance1)))
                .isInstanceOf(NotFoundException.class);
    }

    private void baseLine() {
        when(sectionDao.save(any(Section.class))).thenReturn(section1);
        when(lineDao.save(any(Line.class))).thenReturn(line);
        when(sectionDao.findByLineId(line.id())).thenReturn(Arrays.asList(section1, section2));

        when(lineDao.findById(line.id())).thenReturn(Optional.of(line));
        when(stationDao.findById(station1.id())).thenReturn(Optional.of(station1));
        when(stationDao.findById(station2.id())).thenReturn(Optional.of(station2));
        when(stationDao.findById(station3.id())).thenReturn(Optional.of(station3));
        when(stationDao.findById(station4.id())).thenReturn(Optional.of(station4));
    }

    private List<String> stationResponsesToString(List<StationResponse> response) {
        return response.stream()
                .map(StationResponse::getName)
                .collect(Collectors.toList());
    }
}
