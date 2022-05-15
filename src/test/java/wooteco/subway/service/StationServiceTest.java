package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;

import javax.sql.DataSource;
import wooteco.subway.domain.station.StationRepository;
import wooteco.subway.repository.SubwayRepository;
import wooteco.subway.repository.dao.LineDao;
import wooteco.subway.repository.dao.SectionDao;
import wooteco.subway.repository.dao.StationDao;
import wooteco.subway.repository.exception.DuplicateStationNameException;
import wooteco.subway.service.dto.station.StationResponse;

@DisplayName("지하철역 Service")
@JdbcTest
class StationServiceTest {

    @Autowired
    private DataSource dataSource;
    private StationService stationService;

    @BeforeEach
    void setUp() {
        LineDao lineDao = new LineDao(dataSource);
        SectionDao sectionDao = new SectionDao(dataSource);
        StationDao stationDao = new StationDao(dataSource);
        StationRepository stationRepository = new SubwayRepository(lineDao, sectionDao, stationDao);
        this.stationService = new StationService(stationRepository);
    }

    @DisplayName("이름으로 지하철 역을 저장한다.")
    @ParameterizedTest
    @ValueSource(strings = {"강남역"})
    void create(String name) {
        StationResponse actual = stationService.create(name);
        assertAll(
                () -> assertThat(actual.getId()).isGreaterThan(0),
                () -> assertThat(actual.getName()).isEqualTo("강남역")
        );
    }

    @DisplayName("이미 존재하는 이름으로 지하철 역을 생성할 수 없다.")
    @ParameterizedTest
    @ValueSource(strings = {"강남역"})
    void createWithDuplicatedName(String name) {
        stationService.create(name);
        assertThatThrownBy(() -> stationService.create(name))
                .isInstanceOf(DuplicateStationNameException.class)
                .hasMessageContaining("해당 이름의 지하철역은 이미 존재합니다.");
    }

    @DisplayName("지하철 역 목록을 조회한다.")
    @ParameterizedTest
    @ValueSource(ints = {5})
    void findAll(int expected) {
        IntStream.rangeClosed(1, expected)
                .mapToObj(id -> "역" + id)
                .forEach(stationService::create);

        List<StationResponse> actual = stationService.findAll();
        assertThat(actual).hasSize(expected);
    }

    @DisplayName("지하철 역을 삭제한다.")
    @Test
    void delete() {
        Stream.of("강남역")
                .map(stationService::create)
                .map(StationResponse::getId)
                .forEach(stationService::remove);

        List<StationResponse> actual = stationService.findAll();
        assertThat(actual).isEmpty();
    }
}
