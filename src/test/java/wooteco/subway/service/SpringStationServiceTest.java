package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import javax.sql.DataSource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.TestConstructor.AutowireMode;
import wooteco.subway.domain.Station;
import wooteco.subway.exception.validation.StationNameDuplicateException;
import wooteco.subway.infra.dao.StationDao;
import wooteco.subway.service.dto.StationServiceRequest;

@JdbcTest
@TestConstructor(autowireMode = AutowireMode.ALL)
@DisplayName("지하철역 서비스")
class SpringStationServiceTest {

    private static final StationServiceRequest STATION_FIXTURE = new StationServiceRequest("선릉역");
    private static final StationServiceRequest STATION_FIXTURE2 = new StationServiceRequest("강남역");
    private static final StationServiceRequest STATION_FIXTURE3 = new StationServiceRequest("역삼역");

    private final StationService stationService;

    public SpringStationServiceTest(DataSource dataSource, JdbcTemplate jdbcTemplate,
                                    NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.stationService = new SpringStationService(
                new StationDao(jdbcTemplate, dataSource, namedParameterJdbcTemplate));
    }

    @Nested
    @DisplayName("새로운 역을 저장할 때")
    class SaveTest {

        @Test
        @DisplayName("역 이름이 중복되지 않으면 저장할 수 있다.")
        void saveSuccessIfNotExists() {
            assertThatCode(() -> stationService.save(STATION_FIXTURE))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("역 이름이 중복되면 예외가 발생한다.")
        void saveFailIfExists() {
            // given & when
            stationService.save(STATION_FIXTURE);

            // then
            assertThatThrownBy(() -> stationService.save(STATION_FIXTURE))
                    .isInstanceOf(StationNameDuplicateException.class)
                    .hasMessage("이미 존재하는 지하철역입니다. : " + STATION_FIXTURE.getName());
        }
    }

    @Test
    @DisplayName("전체 지하철 역을 조회할 수 있다")
    void findAll() {
        // given
        final List<String> expected = List.of(
                STATION_FIXTURE.getName(),
                STATION_FIXTURE2.getName(),
                STATION_FIXTURE3.getName()
        );

        // when
        stationService.save(STATION_FIXTURE);
        stationService.save(STATION_FIXTURE2);
        stationService.save(STATION_FIXTURE3);

        // then
        assertThat(stationService.findAll()).extracting("name").isEqualTo(expected);
    }

    @Test
    @DisplayName("아이디로 지하철역을 삭제할 수 있다")
    void deleteById() {
        // given
        final Station station = stationService.save(STATION_FIXTURE);
        final List<Station> stations = stationService.findAll();

        // when
        stationService.deleteById(station.getId());
        final List<Station> afterDelete = stationService.findAll();

        // then
        assertAll(
                () -> assertThat(stations).isNotEmpty(),
                () -> assertThat(afterDelete).isEmpty()
        );
    }
}
