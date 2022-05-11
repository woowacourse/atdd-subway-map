package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static wooteco.subway.test.TestFixture.낙성대역;
import static wooteco.subway.test.TestFixture.봉천역;
import static wooteco.subway.test.TestFixture.사당역;
import static wooteco.subway.test.TestFixture.서울대입구역;
import static wooteco.subway.test.TestFixture.신림역;

import java.util.List;
import java.util.Optional;
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
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.dto.StationResponse;

@ExtendWith(MockitoExtension.class)
class LineServiceTest {

    @Mock
    private LineDao lineDao;

    @Mock
    private SectionDao sectionDao;

    @Mock
    private StationDao stationDao;

    @InjectMocks
    private LineService lineService;

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void save() {
        // given
        LineRequest testRequest = new LineRequest("test", "GREEN", 1L, 2L, 5);
        when(lineDao.findByName("test"))
                .thenReturn(Optional.empty());
        when(lineDao.save(any(Line.class)))
                .thenReturn(new Line(1L, testRequest.getName(), testRequest.getColor()));
        when(stationDao.findById(1L))
                .thenReturn(Optional.of(신림역));
        when(stationDao.findById(2L))
                .thenReturn(Optional.of(봉천역));
        // when
        LineResponse result = lineService.save(testRequest);
        // then
        List<StationResponse> stations = result.getStations();
        assertAll(
                () -> verify(sectionDao).save(anyLong(), any(Section.class)),
                () -> assertThat(result.getId()).isEqualTo(1),
                () -> assertThat(result.getName()).isEqualTo("test"),
                () -> assertThat(result.getColor()).isEqualTo("GREEN"),
                () -> assertThat(stations).hasSize(2),
                () -> assertThat(stations.get(0).getId()).isEqualTo(1L),
                () -> assertThat(stations.get(0).getName()).isEqualTo("신림역"),
                () -> assertThat(stations.get(1).getId()).isEqualTo(2L),
                () -> assertThat(stations.get(1).getName()).isEqualTo("봉천역")
        );
    }

    @DisplayName("지하철 노선 생성 시 이름이 중복된다면 에러를 응답한다.")
    @Test
    void save_duplication_exception() {
        // given
        LineRequest testRequest = new LineRequest("2호선", "GREEN", 1L, 2L, 5);
        when(lineDao.findByName("2호선"))
                .thenReturn(Optional.of(new Line(1L, testRequest.getName(), testRequest.getColor())));
        // then
        assertThatThrownBy(() -> lineService.save(testRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("2호선 : 이름이 중복되는 지하철 노선이 존재합니다.");
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void findAll() {
        // given
        when(lineDao.findAll())
                .thenReturn(List.of(
                        new Line(1L, "test1", "GREEN"),
                        new Line(2L, "test2", "YELLOW")));
        // when
        List<LineResponse> result = lineService.findAll();
        // then
        assertAll(
                () -> assertThat(result).hasSize(2),
                () -> assertThat(result.get(0).getId()).isEqualTo(1),
                () -> assertThat(result.get(0).getName()).isEqualTo("test1"),
                () -> assertThat(result.get(0).getColor()).isEqualTo("GREEN"),
                () -> assertThat(result.get(1).getId()).isEqualTo(2),
                () -> assertThat(result.get(1).getName()).isEqualTo("test2"),
                () -> assertThat(result.get(1).getColor()).isEqualTo("YELLOW")
        );
    }

    @DisplayName("id를 이용해 지하철 노선을 조회한다.")
    @Test
    void findById() {
        // given
        when(lineDao.findById(1L))
                .thenReturn(Optional.of(new Line(1L, "test1", "GREEN")));
        when(sectionDao.findAllByLineId(1L))
                .thenReturn(List.of(
                        new Section(봉천역, 서울대입구역, 1),
                        new Section(서울대입구역, 낙성대역, 2),
                        new Section(낙성대역, 사당역, 10),
                        new Section(신림역, 봉천역, 5)));
        // when
        LineResponse result = lineService.findById(1L);
        // then
        assertAll(
                () -> assertThat(result.getId()).isEqualTo(1L),
                () -> assertThat(result.getName()).isEqualTo("test1"),
                () -> assertThat(result.getColor()).isEqualTo("GREEN"),
                () -> assertThat(result.getStations()).hasSize(5),
                () -> assertThat(result.getStations()).extracting("name").containsExactly(
                        신림역.getName(), 봉천역.getName(), 서울대입구역.getName(), 낙성대역.getName(), 사당역.getName()
                )
        );
    }

    @DisplayName("존재하지 않는 id를 이용해 지하철 노선을 조회할 경우 에러가 발생한다.")
    @Test
    void findById_noExistLine_exception() {
        // given
        when(lineDao.findById(1L))
                .thenReturn(Optional.empty());
        // then
        assertThatThrownBy(() -> lineService.findById(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("1 : 해당 ID의 지하철 노선이 존재하지 않습니다.");
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void delete() {
        // given
        when(lineDao.findById(1L))
                .thenReturn(Optional.of(new Line(1L, "test", "BLACK")));
        // when
        lineService.delete(1L);
        // then
        verify(lineDao).delete(any(Line.class));
    }

    @DisplayName("삭제 요청 시 ID에 해당하는 지하철역이 없다면 에러를 응답한다.")
    @Test
    void delete_noExistStation_exception() {
        // given
        when(lineDao.findById(1L))
                .thenReturn(Optional.empty());
        // then
        assertThatThrownBy(() -> lineService.delete(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("1 : 해당 ID의 지하철 노선이 존재하지 않습니다.");
    }

    @DisplayName("노선을 수정한다.")
    @Test
    void update() {
        // given
        LineRequest updateRequest = new LineRequest("2호선", "GREEN", 1L, 2L, 5);
        when(lineDao.findById(1L))
                .thenReturn(Optional.of(new Line(1L, "2호선", "GREEN")));
        when(lineDao.findByName("2호선"))
                .thenReturn(Optional.empty());
        // when
        lineService.update(1L, updateRequest);
        // then
        verify(lineDao).update(anyLong(), any(Line.class));
    }

    @DisplayName("존재하지 않는 ID의 노선을 수정한다.")
    @Test
    void updateLine_noExistLine_Exception() {
        // given
        LineRequest updateRequest = new LineRequest("2호선", "GREEN", 1L, 2L, 5);
        when(lineDao.findById(1L))
                .thenReturn(Optional.empty());
        // then
        assertThatThrownBy(() -> lineService.update(1L, updateRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("1 : 해당 ID의 지하철 노선이 존재하지 않습니다.");
    }

    @DisplayName("중복된 이름으로 노선을 수정한다.")
    @Test
    void updateLine_duplicateName_Exception() {
        // given
        LineRequest updateRequest = new LineRequest("2호선", "GREEN", 1L, 2L, 5);
        when(lineDao.findById(1L))
                .thenReturn(Optional.of(new Line(1L, "11호선", "GRAY")));
        when(lineDao.findByName("2호선"))
                .thenReturn(Optional.of(new Line(2L, "9호선", "BLUE")));
        // then
        assertThatThrownBy(() -> lineService.update(1L, updateRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("2호선 : 이름이 중복되는 지하철 노선이 존재합니다.");
    }

    @DisplayName("노선 중간에 구간을 추가한다.")
    @Test
    void addSection_middle() {
        // given
        SectionRequest sectionRequest = new SectionRequest(2L, 5L, 3);
        when(lineDao.findById(1L))
                .thenReturn(Optional.of(new Line(1L, "2호선", "GREEN")));
        when(sectionDao.findAllByLineId(1L))
                .thenReturn(List.of(
                        new Section(신림역, 봉천역, 5),
                        new Section(봉천역, 서울대입구역, 5),
                        new Section(서울대입구역, 낙성대역, 5)));
        when(stationDao.findById(2L))
                .thenReturn(Optional.of(봉천역));
        when(stationDao.findById(5L))
                .thenReturn(Optional.of(사당역));
        // when
        lineService.addSection(1L, sectionRequest);
        // then
        verify(sectionDao, times(1)).remove(any(Section.class));
        verify(sectionDao, times(2)).save(eq(1L), any(Section.class));
    }

    @DisplayName("노선 맨 앞 혹은 맨 뒤에 구간을 추가한다.")
    @Test
    void addSection_frontOrBack() {
        // given
        SectionRequest sectionRequest = new SectionRequest(4L, 5L, 3);
        when(lineDao.findById(1L))
                .thenReturn(Optional.of(new Line(1L, "2호선", "GREEN")));
        when(sectionDao.findAllByLineId(1L))
                .thenReturn(List.of(
                        new Section(신림역, 봉천역, 5),
                        new Section(봉천역, 서울대입구역, 5),
                        new Section(서울대입구역, 낙성대역, 5)));
        when(stationDao.findById(4L))
                .thenReturn(Optional.of(낙성대역));
        when(stationDao.findById(5L))
                .thenReturn(Optional.of(사당역));
        // when
        lineService.addSection(1L, sectionRequest);
        // then
        verify(sectionDao, times(0)).remove(any(Section.class));
        verify(sectionDao, times(1)).save(eq(1L), any(Section.class));
    }

    @DisplayName("노선에서 중간의 구간을 제거한다.")
    @Test
    void deleteSection_middle() {
        // given
        when(lineDao.findById(1L))
                .thenReturn(Optional.of(new Line(1L, "2호선", "GREEN")));
        when(stationDao.findById(2L))
                .thenReturn(Optional.of(봉천역));
        when(sectionDao.findAllByLineId(1L))
                .thenReturn(List.of(
                        new Section(신림역, 봉천역, 5),
                        new Section(봉천역, 서울대입구역, 5),
                        new Section(서울대입구역, 낙성대역, 5)));
        // when
        lineService.deleteSection(1L, 2L);
        // then
        verify(sectionDao, times(2)).remove(any(Section.class));
        verify(sectionDao, times(1)).save(anyLong(), any(Section.class));
    }

    @DisplayName("노선에서 맨 앞 혹은 맨 뒤 구간을 제거한다.")
    @Test
    void deleteSection_frontOrBack() {
        // given
        when(lineDao.findById(1L))
                .thenReturn(Optional.of(new Line(1L, "2호선", "GREEN")));
        when(stationDao.findById(1L))
                .thenReturn(Optional.of(신림역));
        when(sectionDao.findAllByLineId(1L))
                .thenReturn(List.of(
                        new Section(신림역, 봉천역, 5),
                        new Section(봉천역, 서울대입구역, 5),
                        new Section(서울대입구역, 낙성대역, 5)));
        // when
        lineService.deleteSection(1L, 1L);
        // then
        verify(sectionDao, times(1)).remove(any(Section.class));
        verify(sectionDao, times(0)).save(anyLong(), any(Section.class));
    }
}
