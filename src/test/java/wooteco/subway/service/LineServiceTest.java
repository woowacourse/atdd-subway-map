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
import wooteco.subway.dao.jdbc.SectionJdbcDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.dao.application.LineService;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.LineRequestV2;
import wooteco.subway.dto.LineResponseV2;
import wooteco.subway.dto.StationResponse;

class LineServiceTest {

    @DisplayName("노선을 성공적으로 등록한다.")
    @Test
    void registerLine() {
        // given
        StationDao stationDao = mock(StationDao.class);
        LineDao lineDao = mock(LineDao.class);
        SectionJdbcDao sectionDao = mock(SectionJdbcDao.class);

        Station station1 = new Station(1L, "서울역");
        Station station2 = new Station(2L, "성수");
        when(stationDao.findById(anyLong())).thenReturn(Optional.of(station1));
        when(stationDao.findById(anyLong())).thenReturn(Optional.of(station2));

        Line line = new Line(1L, "1호선", "파란색");
        when(lineDao.save(any(Line.class))).thenReturn(line);

        Section section = new Section(1L, station1, station2, 10);
        when(sectionDao.save(anyLong(), any(Section.class))).thenReturn(section);

        LineService sut = new LineService(stationDao, lineDao, sectionDao);
        LineRequestV2 request = new LineRequestV2("1호선", "파란색", 1L, 2L, 10);
        LineResponseV2 expect = new LineResponseV2(1L, "1호선", "파란색",
                List.of(StationResponse.from(station1), StationResponse.from(station2)));
        // when
        LineResponseV2 actual = sut.createLine(request);

        // then
        assertThat(actual).isEqualTo(expect);
    }
}
