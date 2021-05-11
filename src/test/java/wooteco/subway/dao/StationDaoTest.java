package wooteco.subway.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.domain.station.Station;
import wooteco.subway.exception.ExceptionStatus;
import wooteco.subway.exception.SubwayException;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@JdbcTest
class StationDaoTest {

    private StationDao stationDao;
    private long testStationId;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private DataSource dataSource;

    @BeforeEach
    void setUp() {
        stationDao = new StationDao(dataSource);
        String schemaQuery = "create table if not exists STATION ( id bigint auto_increment not null, nam varchar(255) " +
                "not null unique, primary key(id))";
        jdbcTemplate.execute(schemaQuery);
        testStationId = stationDao.save(new Station("testStation"));
    }

    @Nested
    @DisplayName("save 메서드는")
    class Describe_save {

        @Nested
        @DisplayName("이름이 중복되지 않은 엔티티의 경우")
        class Context_with_unique_name {

            @DisplayName("역을 정상적으로 등록한다.")
            @Test
            void save() {
                Station station = new Station("testStation2");

                stationDao.save(station);
                List<Station> stations = stationDao.findAll();

                assertThat(stations).hasSize(2);
            }
        }

        @Nested
        @DisplayName("이름이 중복된 경우")
        class Context_with_duplicated_name {

            @DisplayName("노선 등록에 실패한다.")
            @Test
            void cannotSave() {
                Station station = new Station("testStation2");
                stationDao.save(station);

                assertThatCode(() -> stationDao.save(station))
                        .isInstanceOf(SubwayException.class)
                        .hasMessage(ExceptionStatus.DUPLICATED_NAME.getMessage());
            }
        }
    }

    @Nested
    @DisplayName("findById 메서드는")
    class Describe_findById {

        @Nested
        @DisplayName("id에 해당하는 엔티티가 존재하는 경우")
        class Context_with_valid_id {

            @DisplayName("역 조회에 성공한다.")
            @Test
            void findById() {
                Station station = stationDao.findById(testStationId)
                        .get();

                assertThat(station).isEqualTo(new Station(testStationId, "testStation"));
            }
        }

        @Nested
        @DisplayName("id에 해당하는 엔티티가 없는 경우")
        class Context_with_invalid_id {

            @DisplayName("역 조회에 실패한다.")
            @Test
            void cannotFindById() {
                Optional<Station> station = stationDao.findById(68954);

                assertThat(station).isEmpty();
            }
        }
    }

    @Nested
    @DisplayName("deleteById 메서드는")
    class Describe_deleteById {

        @Nested
        @DisplayName("id에 해당하는 엔티티가 존재하는 경우")
        class Context_with_valid_id {

            @DisplayName("역 삭제에 성공한다.")
            @Test
            void deleteById() {
                long id = stationDao.save(new Station("dummy"));
                int beforeLineCounts = stationDao.findAll().size();

                stationDao.deleteById(id);
                int afterLineCounts = stationDao.findAll().size();

                assertThat(beforeLineCounts - 1).isEqualTo(afterLineCounts);
            }
        }

        @Nested
        @DisplayName("id에 해당하는 엔티티가 존재하지 않는 경우")
        class Context_with_invalid_id {

            @DisplayName("노선 삭제에 실패한다.")
            @Test
            void cannotUpdate() {
                assertThatCode(() -> stationDao.deleteById(6874))
                        .isInstanceOf(SubwayException.class)
                        .hasMessage(ExceptionStatus.ID_NOT_FOUND.getMessage());
            }
        }
    }
}
