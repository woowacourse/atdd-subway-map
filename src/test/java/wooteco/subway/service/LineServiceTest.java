package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;

import javax.sql.DataSource;
import wooteco.subway.domain.line.LineRepository;
import wooteco.subway.repository.SubwayRepository;
import wooteco.subway.repository.dao.LineDao;
import wooteco.subway.repository.dao.SectionDao;
import wooteco.subway.repository.dao.StationDao;
import wooteco.subway.repository.exception.DuplicateLineColorException;
import wooteco.subway.repository.exception.DuplicateLineNameException;
import wooteco.subway.service.dto.line.LineRequest;
import wooteco.subway.service.dto.line.LineResponse;
import wooteco.subway.service.dto.line.LineUpdateRequest;
import wooteco.subway.service.dto.section.SectionRequest;
import wooteco.subway.service.dto.station.StationResponse;

@DisplayName("지하철노선 Service")
@JdbcTest
class LineServiceTest {

    @Autowired
    private DataSource dataSource;
    private LineService lineService;
    private Long upStationId;
    private Long middleStationId;
    private Long downStationId;

    @BeforeEach
    void setUp() {
        LineDao lineDao = new LineDao(dataSource);
        SectionDao sectionDao = new SectionDao(dataSource);
        StationDao stationDao = new StationDao(dataSource);

        LineRepository lineRepository = new SubwayRepository(lineDao, sectionDao, stationDao);
        this.lineService = new LineService(lineRepository);

        StationService stationService = new StationService(new SubwayRepository(lineDao, sectionDao, stationDao));
        this.upStationId = stationService.create("강남역").getId();
        this.middleStationId = stationService.create("역삼역").getId();
        this.downStationId = stationService.create("선릉역").getId();
    }

    private LineRequest createLineRequest(String name, String color) {
        return new LineRequest(name, color, upStationId, downStationId, 10);
    }

    @DisplayName("지하철노선을 저장한다.")
    @ParameterizedTest
    @CsvSource(value = {"2호선,red"})
    void create(String name, String color) {
        LineResponse actual = lineService.create(createLineRequest(name, color));
        assertAll(
                () -> assertThat(actual.getId()).isGreaterThan(0),
                () -> assertThat(actual.getName()).isEqualTo(name),
                () -> assertThat(actual.getColor()).isEqualTo(color)
        );
    }

    @DisplayName("이미 존재하는 이름으로 지하철노선을 생성할 수 없다.")
    @ParameterizedTest
    @ValueSource(strings = {"2호선"})
    void createWithDuplicatedName(String name) {
        LineRequest firstRequest = createLineRequest(name, "color1");
        LineRequest secondRequest = createLineRequest(name, "color2");

        lineService.create(firstRequest);
        assertThatThrownBy(() -> lineService.create(secondRequest))
                .isInstanceOf(DuplicateLineNameException.class)
                .hasMessageContaining("해당 이름의 지하철노선은 이미 존재합니다");
    }

    @DisplayName("이미 존재하는 색상으로 지하철노선을 생성할 수 없다.")
    @ParameterizedTest
    @ValueSource(strings = {"color"})
    void createWithDuplicatedColor(String color) {
        LineRequest firstRequest = createLineRequest("2호선", color);
        LineRequest secondRequest = createLineRequest("분당선", color);

        lineService.create(firstRequest);
        assertThatThrownBy(() -> lineService.create(secondRequest))
                .isInstanceOf(DuplicateLineColorException.class)
                .hasMessageContaining("해당 색상의 지하철노선은 이미 존재합니다");
    }

    @DisplayName("지하철노선 목록을 조회한다.")
    @ParameterizedTest
    @ValueSource(ints = {4})
    void findAll(int expected) {
        IntStream.rangeClosed(1, expected)
                .mapToObj(id -> createLineRequest("호선" + id, "color" + id))
                .forEach(lineService::create);

        List<LineResponse> actual = lineService.findAll();
        assertThat(actual).hasSize(expected);
    }

    @DisplayName("지하철노선을 조회한다.")
    @Test
    void findOne() {
        LineResponse expected = lineService.create(createLineRequest("1호선", "color1"));
        LineResponse actual = lineService.findOne(expected.getId());
        assertThat(actual).usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @DisplayName("지하철노선을 수정한다.")
    @ParameterizedTest
    @CsvSource(value = {"2호선,red"})
    void update(String name, String color) {
        Long lineId = lineService.create(createLineRequest("1호선", "color1")).getId();
        lineService.update(lineId, new LineUpdateRequest(name, color));

        LineResponse actual = lineService.findOne(lineId);
        assertAll(
                () -> assertThat(actual.getId()).isEqualTo(lineId),
                () -> assertThat(actual.getName()).isEqualTo(name),
                () -> assertThat(actual.getColor()).isEqualTo(color)
        );
    }

    @DisplayName("지하철노선을 삭제한다.")
    @Test
    void delete() {
        LineResponse lineResponse = lineService.create(createLineRequest("1호선", "color1"));
        lineService.delete(lineResponse.getId());

        List<LineResponse> actual = lineService.findAll();
        assertThat(actual).isEmpty();
    }

    @DisplayName("지하철노선에서 구간을 추가한다.")
    @Test
    void appendSection() {
        Long lineId = lineService.create(createLineRequest("1호선", "red")).getId();
        lineService.appendSection(lineId, new SectionRequest(upStationId, middleStationId, 5));

        LineResponse lineResponse = lineService.findOne(lineId);
        List<StationResponse> stationResponses = lineResponse.getStations();

        List<String> actual = stationResponses.stream()
                .map(StationResponse::getName)
                .collect(Collectors.toUnmodifiableList());
        assertThat(actual).containsExactly("강남역", "역삼역", "선릉역");
    }

    @DisplayName("지하철노선에서 역을 제거한다.")
    @Test
    void removeStation() {
        LineResponse lineResponse = lineService.create(createLineRequest("1호선", "red"));
        Long lineId = lineResponse.getId();
        List<StationResponse> expected = lineResponse.getStations();

        lineService.appendSection(lineId, new SectionRequest(upStationId, middleStationId, 5));
        lineService.removeStation(lineId, middleStationId);

        List<StationResponse> actual = lineService.findOne(lineId).getStations();
        assertThat(actual).usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(expected);
    }
}
