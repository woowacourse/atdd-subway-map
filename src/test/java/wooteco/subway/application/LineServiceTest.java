package wooteco.subway.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doReturn;
import static wooteco.subway.Fixture.강남역;
import static wooteco.subway.Fixture.청계산입구역;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.dto.LineResponse;

@ExtendWith(MockitoExtension.class)
class LineServiceTest {

    @InjectMocks
    private LineService lineService;

    @Mock
    private LineDao lineDao;
    @Mock
    private StationDao stationDao;
    @Mock
    private SectionDao sectionDao;

    @Test
    @DisplayName("중복되지 않은 이름의 노선을 저장")
    void save() {
        //given
        final String name = "신분당선";
        final String color = "빨강이";
        final Line line = Line.initialCreateWithId(1L, name, color, 강남역, 청계산입구역, 1);
        given(lineDao.existsByName(name)).willReturn(false);
        given(lineDao.save(any())).willReturn(line);
        doReturn(강남역).when(stationDao).findById(1L);
        doReturn(청계산입구역).when(stationDao).findById(2L);

        //when
        final LineResponse lineResponse = lineService.save(name, color, 1L, 2L, 1);

        //then
        assertThat(lineResponse.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("중복된 이름의 노선을 저장 요청을 하면 예외 발생")
    void saveExistNameLine() {
        //given
        final String name = "신분당선";
        final String color = "빨강이";
        given(lineDao.existsByName(name)).willReturn(true);
        //then
        assertThatThrownBy(() -> lineService.save(name, color, 1L, 2L, 1))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void showLines() {
        //given
        final List<Line> lines = List.of(Line.initialCreateWithId(1L, "신분당선", "빨강이", 강남역, 청계산입구역, 1),
                Line.initialCreateWithId(2L, "2호선", "초록이", 강남역, 청계산입구역,2));
        given(lineDao.findAll()).willReturn(lines);
        //when
        final List<LineResponse> lineResponses = lineService.showLines();
        //then
        assertThat(lineResponses.size()).isEqualTo(2);
    }

    @Test
    void showLine() {
        //given
        final Line line = Line.createWithId(1L, "신분당선", "color", List.of(Section.createWithId(1L, 강남역, 청계산입구역, 5)));
        given(lineDao.findById(1L)).willReturn(line);
        //when
        final LineResponse lineResponse = lineService.showLine(1L);
        //then
        assertThat(lineResponse.getId()).isEqualTo(1L);
    }
}
