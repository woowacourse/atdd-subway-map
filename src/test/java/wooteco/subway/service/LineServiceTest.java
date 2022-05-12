package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.dao.application.LineService;
import wooteco.subway.dao.jdbc.SectionJdbcDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.dto.StationResponse;

class LineServiceTest {

    private final StationDao stationDao = mock(StationDao.class);
    private final LineDao lineDao = mock(LineDao.class);
    private final SectionJdbcDao sectionDao = mock(SectionJdbcDao.class);

    private Station station1;
    private Station station2;
    private Station station3;
    private Line line1;
    private Line line2;
    private Section section1To2;
    private Section section2To3;

    @BeforeEach
    void setUp() {
        station1 = new Station(1L, "station1");
        station2 = new Station(2L, "station2");
        station3 = new Station(3L, "station3");
        line1 = new Line(1L, "line1", "color1");
        line2 = new Line(2L, "line2", "color2");
        section1To2 = new Section(1L, station1, station2, 10);
        section2To3 = new Section(2L, station2, station3, 10);
    }

    @DisplayName("노선을 성공적으로 등록한다.")
    @Test
    void registerLine() {
        //given
        LineService sut = new LineService(stationDao, lineDao, sectionDao);

        when(stationDao.findById(station1.getId())).thenReturn(Optional.of(station1));
        when(stationDao.findById(station2.getId())).thenReturn(Optional.of(station2));
        when(lineDao.save(any(Line.class))).thenReturn(line1);
        when(sectionDao.save(anyLong(), any(Section.class))).thenReturn(section1To2);

        LineRequest request = new LineRequest(line1.getName(), line1.getColor(), station1.getId(), station2.getId(),
                10);
        LineResponse expect = new LineResponse(line1.getId(), line1.getName(), line1.getColor(),
                List.of(StationResponse.from(station1), StationResponse.from(station2)));
        // when
        LineResponse actual = sut.createLine(request);

        // then
        assertThat(actual).isEqualTo(expect);
    }

    @DisplayName("단건의 지하철 노선을 조회한다.")
    @Test
    void findLine() {
        // given
        LineService sut = new LineService(stationDao, lineDao, sectionDao);

        when(lineDao.findById(line1.getId())).thenReturn(Optional.of(line1));
        when(sectionDao.findByLineId(line1.getId())).thenReturn(List.of(section1To2));

        // when
        LineResponse actual = sut.findLine(line1.getId());

        // then
        LineResponse expected = LineResponse.from(line1);
        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("노선 목록을 조회한다")
    @Test
    void findLines() {
        LineService sut = new LineService(stationDao, lineDao, sectionDao);

        when(lineDao.findAll()).thenReturn(List.of(line1, line2));
        when(sectionDao.findByLineId(line1.getId())).thenReturn(List.of(section1To2));
        when(sectionDao.findByLineId(line2.getId())).thenReturn(List.of(section2To3));

        // when
        List<LineResponse> lines = sut.findLines();

        // then
        assertThat(lines).containsOnly(LineResponse.from(line1), LineResponse.from(line2));
    }

    @DisplayName("구간을 추가한다.")
    @Test
    void addSection() {
        LineService sut = new LineService(stationDao, lineDao, sectionDao);

        when(lineDao.findById(line1.getId())).thenReturn(Optional.of(line1));
        when(sectionDao.findByLineId(line1.getId())).thenReturn(List.of(section1To2));

        SectionRequest sectionRequest = new SectionRequest(station2.getId(), station3.getId(),
                section2To3.getDistance());
        when(stationDao.findById(sectionRequest.getUpStationId())).thenReturn(Optional.of(station1));
        when(stationDao.findById(sectionRequest.getDownStationId())).thenReturn(Optional.of(station2));
        when(sectionDao.save(anyLong(), any(Section.class))).thenReturn(section2To3);

        // when
        sut.addSection(line1.getId(), sectionRequest);

        // then
        assertThat(line1.getSections().size()).isEqualTo(2);
    }

    @DisplayName("구간을 제거한다")
    @Test
    void removeSection() {

    }
}
