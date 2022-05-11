package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

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
import wooteco.subway.domain.Sections;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.SectionRequest;

@ExtendWith(MockitoExtension.class)
public class LineServiceTest {

    @InjectMocks
    private LineService lineService;

    @Mock
    private LineDao lineDao;

    @Mock
    private SectionDao sectionDao;

    @Mock
    private StationDao stationDao;

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // given
        LineRequest lineRequest = new LineRequest("test", "GREEN", 1L, 2L, 10);
        given(lineDao.findByName("test"))
            .willReturn(Optional.empty());
        given(lineDao.save(any(Line.class)))
            .willReturn(new Line(1L, lineRequest.getName(), lineRequest.getColor()));
        given(sectionDao.save(any()))
            .willReturn(new Section(1L, 1L, 1L, 2L, 10));
        given(stationDao.findById(any()))
            .willReturn(Optional.of(new Station(1L, "신설동역")));
        given(stationDao.findById(any()))
            .willReturn(Optional.of(new Station(2L, "성수역")));
        // when
        LineResponse lineResponse = lineService.createLine(lineRequest);
        // then
        assertThat(lineResponse.getId()).isEqualTo(1L);
        assertThat(lineResponse.getName()).isEqualTo("test");
        assertThat(lineResponse.getColor()).isEqualTo("GREEN");
    }

    @DisplayName("지하철 노선 생성 시 이름이 중복된다면 에러를 응답한다.")
    @Test
    void createLine_duplicate_name_exception() {
        // given
        LineRequest lineRequest = new LineRequest("test", "GREEN");
        given(lineDao.findByName("test"))
            .willReturn(Optional.of(new Line(1L, lineRequest.getName(), "NotGreen")));
        // then
        assertThatThrownBy(() -> lineService.createLine(lineRequest))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("중복되는 이름의 지하철 노선이 존재합니다.");
    }

    @DisplayName("지하철 노선 생성 시 색깔이 중복된다면 에러를 응답한다.")
    @Test
    void createLine_duplicate_color_exception() {
        // given
        LineRequest lineRequest = new LineRequest("test", "GREEN");
        given(lineDao.findByName("test"))
            .willReturn(Optional.empty());
        given(lineDao.findByColor("GREEN"))
            .willReturn(Optional.of(new Line(1L, "NotTest", lineRequest.getColor())));
        // then
        assertThatThrownBy(() -> lineService.createLine(lineRequest))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("중복되는 색깔의 지하철 노선이 존재합니다.");
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        given(lineDao.findAll())
            .willReturn(
                List.of(
                    new Line(1L, "test1", "GREEN"),
                    new Line(2L, "test2", "YELLOW")
                )
            );
        given(stationDao.findAll())
            .willReturn(
                List.of(
                    new Station(1L, "신설동역"),
                    new Station(2L, "용두역"),
                    new Station(3L, "성수역")
                )
            );
        given(sectionDao.findByLineId(1L))
            .willReturn(new Sections(
                List.of(new Section(1L, 2L, 10))));
        given(sectionDao.findByLineId(2L))
            .willReturn(new Sections(
                List.of(new Section(2L, 3L, 10))));
        // when
        List<LineResponse> responses = lineService.showLines();
        // then
        assertThat(responses.size()).isEqualTo(2);
        assertThat(responses.get(0).getId()).isEqualTo(1L);
        assertThat(responses.get(0).getName()).isEqualTo("test1");
        assertThat(responses.get(0).getColor()).isEqualTo("GREEN");
        assertThat(responses.get(1).getId()).isEqualTo(2L);
        assertThat(responses.get(1).getName()).isEqualTo("test2");
        assertThat(responses.get(1).getColor()).isEqualTo("YELLOW");
    }

    @DisplayName("id를 이용해 지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        given(lineDao.findById(1L))
            .willReturn(Optional.of(new Line(1L, "test1", "GREEN")));
        given(stationDao.findAll())
            .willReturn(
                List.of(
                    new Station(1L, "신설동역"),
                    new Station(2L, "용두역"),
                    new Station(3L, "성수역")
                )
            );
        given(sectionDao.findByLineId(1L))
            .willReturn(new Sections(
                List.of(new Section(1L, 2L, 10))));
        // when
        LineResponse response = lineService.showLine(1L);
        // then
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("test1");
        assertThat(response.getColor()).isEqualTo("GREEN");
    }

    @DisplayName("존재하지 않는 id를 이용해 지하철 노선을 조회할 경우 에러가 발생한다.")
    @Test
    void getLine_noExistLine_exception() {
        // given
        given(lineDao.findById(1L))
            .willReturn(Optional.empty());
        // then
        assertThatThrownBy(() -> lineService.showLine(1L))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("해당하는 ID의 지하철 노선이 존재하지 않습니다.");
    }

    @DisplayName("노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        LineRequest updateRequest = new LineRequest("9호선", "GREEN");
        Line originLine = new Line(1L, "11호선", "GRAY");
        Line updateLine = new Line(1L, updateRequest.getName(), updateRequest.getColor());
        given(lineDao.findById(1L))
            .willReturn(Optional.of(originLine));
        given(lineDao.findByName("9호선")).willReturn(Optional.empty());
        // when
        lineService.updateLine(1L, updateRequest);
        // then
        verify(lineDao).update(originLine, updateLine);
    }

    @DisplayName("존재하지 않는 ID의 노선을 수정한다면 예외가 발생한다.")
    @Test
    void updateLine_noExistLine_Exception() {
        // given
        LineRequest updateRequest = new LineRequest("9호선", "GREEN");
        given(lineDao.findById(1L))
            .willReturn(Optional.empty());
        // then
        assertThatThrownBy(() -> lineService.updateLine(1L, updateRequest))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("해당하는 ID의 지하철 노선이 존재하지 않습니다.");
    }

    @DisplayName("중복된 이름으로 노선을 수정한다.")
    @Test
    void updateLine_duplicate_name_exception() {
        // given
        LineRequest updateRequest = new LineRequest("9호선", "GREEN");
        given(lineDao.findById(1L))
            .willReturn(Optional.of(new Line(1L, "11호선", "GRAY")));
        given(lineDao.findByName("9호선"))
            .willReturn(Optional.of(new Line(2L, "9호선", "BLUE")));
        // then
        assertThatThrownBy(() -> lineService.updateLine(1L, updateRequest))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("중복되는 이름의 지하철 노선이 존재합니다.");
    }

    @DisplayName("중복된 색깔로 노선을 수정한다.")
    @Test
    void updateLine_duplicate_color_exception() {
        // given
        LineRequest updateRequest = new LineRequest("9호선", "GREEN");
        given(lineDao.findById(1L))
            .willReturn(Optional.of(new Line(1L, "11호선", "GRAY")));
        given(lineDao.findByColor("GREEN"))
            .willReturn(Optional.of(new Line(2L, "12호선", "GREEN")));
        // then
        assertThatThrownBy(() -> lineService.updateLine(1L, updateRequest))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("중복되는 색깔의 지하철 노선이 존재합니다.");
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        Line line = new Line(1L, "test", "BLACK");
        given(lineDao.findById(1L))
            .willReturn(Optional.of(line));
        // when
        lineService.deleteLine(1L);
        // then
        verify(lineDao).delete(line);
    }

    @DisplayName("삭제 요청 시 ID에 해당하는 지하철 노선이 없다면 에러를 응답한다.")
    @Test
    void deleteLine_noExistLine_exception() {
        // given
        given(lineDao.findById(1L))
            .willReturn(Optional.empty());
        // then
        assertThatThrownBy(() -> lineService.deleteLine(1L))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("해당하는 ID의 지하철 노선이 존재하지 않습니다.");
    }

    @DisplayName("기존에 존재하는 노선에 하행 종점 구간을 등록한다.")
    @Test
    void addSectionInLine_DownStation() {
        // given
        SectionRequest sectionRequest = new SectionRequest(2L, 3L, 10);
        given(sectionDao.findByLineId(1L))
            .willReturn(new Sections(
                List.of(new Section(1L, 2L, 10))
            ));
        lineService.addSection(1L, sectionRequest);
        // then
        verify(sectionDao).save(new Section(2L, 3L, 10));
    }

    @DisplayName("기존에 존재하는 노선에 상행 종점 구간을 등록한다.")
    @Test
    void addSectionInLine_UpStation() {
        // given
        SectionRequest sectionRequest = new SectionRequest(3L, 1L, 10);
        given(sectionDao.findByLineId(1L))
            .willReturn(new Sections(
                List.of(new Section(1L, 2L, 10))
            ));
        lineService.addSection(1L, sectionRequest);
        // then
        verify(sectionDao).save(new Section(3L, 1L, 10));
    }

    @DisplayName("기존에 존재하는 노선의 구간 사이에 새로운 구간을 등록한다.")
    @Test
    void addSectionInLine_way_point() {
        // given
        SectionRequest sectionRequest = new SectionRequest(1L, 3L, 5);
        given(sectionDao.findByLineId(1L))
            .willReturn(new Sections(
                List.of(new Section(1L, 1L, 1L, 2L, 10))
            ));
        lineService.addSection(1L, sectionRequest);
        // then
        verify(sectionDao).delete(1L);
        verify(sectionDao).save(new Section(1L, 3L, 5));
        verify(sectionDao).save(new Section(3L, 2L, 5));
    }

    @DisplayName("기존에 존재하는 노선의 구간 사이에 상행 기준으로 새로운 구간을 등록할 때, 길이가 크거나 같다면 예외를 발생한다.")
    @Test
    void addSectionInLine_up_way_point_exception() {
        // given
        SectionRequest sectionRequest = new SectionRequest(1L, 3L, 10);
        given(sectionDao.findByLineId(1L))
            .willReturn(new Sections(
                List.of(new Section(1L, 1L, 1L, 2L, 10))
            ));
        // then
        assertThatThrownBy(() -> lineService.addSection(1L, sectionRequest))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("[ERROR] 역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록할 수 없습니다.");
    }

    @DisplayName("기존에 존재하는 노선의 구간 사이에 하행 기준으로 새로운 구간을 등록할 때, 길이가 크거나 같다면 예외를 발생한다.")
    @Test
    void addSectionInLine_down_way_point_exception() {
        // given
        SectionRequest sectionRequest = new SectionRequest(3L, 2L, 10);
        given(sectionDao.findByLineId(1L))
            .willReturn(new Sections(
                List.of(new Section(1L, 1L, 1L, 2L, 10))
            ));
        // then
        assertThatThrownBy(() -> lineService.addSection(1L, sectionRequest))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("[ERROR] 역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록할 수 없습니다.");
    }

    @DisplayName("기존에 존재하는 노선의 구간 사이에 하행 기준으로 새로운 구간을 등록할 때 일치하는 상,하행 지하철 역이 없다면 예외를 발생한다.")
    @Test
    void addSectionInLine_no_exist_station_exception() {
        // given
        SectionRequest sectionRequest = new SectionRequest(3L, 4L, 10);
        given(sectionDao.findByLineId(1L))
            .willReturn(new Sections(
                List.of(new Section(1L, 1L, 1L, 2L, 10))
            ));
        // then
        assertThatThrownBy(() -> lineService.addSection(1L, sectionRequest))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("[ERROR] 상,하행 Station 모두 구간에 존재하지 않는다면 추가할 수 없습니다.");
    }

    @DisplayName("기존에 존재하는 노선의 구간 사이에 하행 기준으로 새로운 구간을 등록할 때 일치하는 상,하행 지하철 역이 없다면 예외를 발생한다.")
    @Test
    void addSectionInLine_exist_duplicate_station_exception() {
        // given
        SectionRequest sectionRequest = new SectionRequest(1L, 3L, 10);
        given(sectionDao.findByLineId(1L))
            .willReturn(new Sections(
                List.of(new Section(1L, 1L, 1L, 2L, 10)
                    , new Section(2L, 1L, 2L, 3L, 10))
            ));
        // then
        assertThatThrownBy(() -> lineService.addSection(1L, sectionRequest))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("[ERROR] 상,하행 Station이 구간에 모두 포함된 경우 추가할 수 없습니다.");
    }
}
