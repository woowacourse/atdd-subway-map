package wooteco.subway.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.line.LineCreateRequest;
import wooteco.subway.dto.line.LineRequest;
import wooteco.subway.dto.line.LineResponse;
import wooteco.subway.dto.station.StationResponse;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.BDDMockito.given;

class LineServiceTest {
    LineService lineService;
    SectionService sectionService;
    LineDao lineDao;
    StationDao stationDao;

    @BeforeEach
    public void setUp() {
        sectionService = Mockito.mock(SectionService.class);
        stationDao = Mockito.mock(StationDao.class);
        lineDao = Mockito.mock(LineDao.class);
        lineService = new LineService(sectionService, lineDao, stationDao);
    }

    @Test
    @DisplayName("지하철 노선 이름이 중복되지 않는다면 등록할 수 있다.")
    void save() {
        LineCreateRequest lineCreateRequest = new LineCreateRequest("name", "red", 1L, 2L, 3);
        given(lineDao.isExistName("name")).willReturn(false);
        given(lineDao.save("name", "red")).willReturn(new Line(1L, "name", "red"));

        assertThat(lineService.save(lineCreateRequest)).isEqualTo(1L);
    }

    @Test
    @DisplayName("지하철 노선 이름이 중복된다면 등록할 수 없다.")
    void saveDuplicate() {
        LineCreateRequest lineCreateRequest = new LineCreateRequest("name", "red", 1L, 2L, 3);
        given(lineDao.isExistName("name")).willReturn(true);
        given(lineDao.save("name", "red")).willReturn(new Line(1L, "name", "red"));

        assertThatThrownBy(() -> lineService.save(lineCreateRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("지하철 노선 이름이 중복될 수 없습니다.");
    }

    @Test
    @DisplayName("지하철 노선 목록을 조회할 수 있다.")
    void findAll() {
        given(lineDao.findAll()).willReturn(List.of(new Line(1L, "name", "red"), new Line(2L, "name2", "blue")));
        given(stationDao.findByLineId(1L)).willReturn(List.of(new Station(1L, "name1"), new Station(2L, "name2")));
        given(stationDao.findByLineId(2L)).willReturn(List.of(new Station(3L, "name3"), new Station(4L, "name4")));

        List<LineResponse> lineResponse = lineService.findAll();

        List<Long> ids = lineResponse.stream()
                .map(LineResponse::getId)
                .collect(Collectors.toList());

        List<String> lineNames = lineResponse.stream()
                .map(LineResponse::getName)
                .collect(Collectors.toList());

        List<String> colors = lineResponse.stream()
                .map(LineResponse::getColor)
                .collect(Collectors.toList());

        List<String> stationNames = lineResponse.stream()
                .flatMap(s -> s.getStations().stream())
                .map(StationResponse::getName)
                .collect(Collectors.toList());


        assertThat(ids).containsOnly(1L, 2L);
        assertThat(lineNames).containsOnly("name", "name2");
        assertThat(colors).containsOnly("red", "blue");
        assertThat(stationNames).containsOnly("name1", "name2", "name3", "name4");
    }

    @Test
    @DisplayName("지하철 노선을 조회할 수 있다.")
    void findById() {
        given(lineDao.findById(1L)).willReturn(new Line(1L, "name", "red"));
        given(stationDao.findByLineId(1L)).willReturn(List.of(new Station(1L, "name1"), new Station(2L, "name2")));

        LineResponse response = lineService.findById(1L);

        List<Long> stationIds = response.getStations().stream()
                .map(StationResponse::getId)
                .collect(Collectors.toList());
        List<String> stationNames = response.getStations().stream()
                .map(StationResponse::getName)
                .collect(Collectors.toList());

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("name");
        assertThat(response.getColor()).isEqualTo("red");
        assertThat(stationIds).containsOnly(1L, 2L);
        assertThat(stationNames).containsOnly("name1", "name2");
    }


    @Test
    @DisplayName("지하철 노선을 삭제할 수 있다.")
    void deleteById() {
        assertDoesNotThrow(() -> lineService.deleteById(1L));
    }

    @Test
    @DisplayName("지하철 노선을 수정할 수 있다.")
    void update() {
        LineRequest lineRequest = new LineRequest("name2", "blue");

        given(lineDao.isExistNameWithoutItself(1L, "name")).willReturn(false);

        assertDoesNotThrow(() -> lineService.update(1L, lineRequest));
    }

    @Test
    @DisplayName("지하철 노선 이름이 중복된다면 수정할 수 없다.")
    void updateDuplicate() {
        LineRequest lineRequest = new LineRequest("name", "blue");

        given(lineDao.isExistNameWithoutItself(1L, "name")).willReturn(true);

        assertThatThrownBy(() -> lineService.update(1L, lineRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("지하철 노선 이름이 중복될 수 없습니다.");
    }
}
