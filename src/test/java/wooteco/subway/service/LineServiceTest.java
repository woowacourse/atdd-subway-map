package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
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
import wooteco.subway.dto.StationResponse;

class LineServiceTest {

    private final StationDao stationDao = mock(StationDao.class);
    private final LineDao lineDao = mock(LineDao.class);
    private final SectionJdbcDao sectionDao = mock(SectionJdbcDao.class);

    @DisplayName("노선을 성공적으로 등록한다.")
    @Test
    void registerLine() {
        //given
        Station station1 = new Station(1L, "서울역");
        Station station2 = new Station(2L, "성수");
        when(stationDao.findById(anyLong())).thenReturn(Optional.of(station1));
        when(stationDao.findById(anyLong())).thenReturn(Optional.of(station2));

        Line line = new Line(1L, "1호선", "파란색");
        when(lineDao.save(any(Line.class))).thenReturn(line);

        Section section = new Section(1L, station1, station2, 10);
        when(sectionDao.save(anyLong(), any(Section.class))).thenReturn(section);

        LineService sut = new LineService(stationDao, lineDao, sectionDao);
        LineRequest request = new LineRequest("1호선", "파란색", 1L, 2L, 10);
        LineResponse expect = new LineResponse(1L, "1호선", "파란색",
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
        Station station1 = new Station(1L, "서울역");
        Station station2 = new Station(2L, "성수");
        Line line = new Line(1L, "1호선", "파란색");
        Section section = new Section(1L, station1, station2, 10);

        when(lineDao.findById(line.getId())).thenReturn(Optional.of(line));
        when(sectionDao.findByLineId(line.getId())).thenReturn(List.of(section));

        LineResponse expected = LineResponse.of(line, section);

        // when
        LineResponse actual = sut.findLine(line.getId());

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("노선 목록을 조회한다")
    @Test
    void findLines() {
        LineService sut = new LineService(stationDao, lineDao, sectionDao);
        Station station1 = new Station(1L, "서울역");
        Station station2 = new Station(2L, "성수");
        Line line1 = new Line(1L, "1호선", "파란색");
        Section section1 = new Section(1L, station1, station2, 10);

        Station station3 = new Station(3L, "홍대입구");
        Line line2 = new Line(2L, "2호선", "초록색");
        Section section2 = new Section(2L, station2, station3, 10);

        when(lineDao.findAll()).thenReturn(List.of(line1, line2));
        when(sectionDao.findByLineId(1L)).thenReturn(List.of(section1));
        when(sectionDao.findByLineId(2L)).thenReturn(List.of(section2));

        // when
        List<LineResponse> lines = sut.findLines();

        // then
        assertThat(lines).containsExactly(LineResponse.of(line1, section1), LineResponse.of(line2, section2));
    }
}
