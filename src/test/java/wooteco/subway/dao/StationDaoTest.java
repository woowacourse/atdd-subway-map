package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import wooteco.subway.domain.Station;
import wooteco.subway.exception.StationDuplicateException;

@DisplayName("Station Dao를 통해서")
class StationDaoTest {

    private static final Station STATION_FIXTURE = new Station(1L, "선릉역");
    private static final Station STATION_FIXTURE2 = new Station(2L, "강남역");
    private static final Station STATION_FIXTURE3 = new Station(3L, "역삼역");

    @BeforeEach
    void setup() {
        StationDao.deleteAll();
    }

    @Nested
    @DisplayName("새로운 역을 저장할 때")
    class SaveTest {

        @Test
        @DisplayName("역 이름이 중복되지 않으면 저장할 수 있다.")
        void save_Success_If_Not_Exists() {
            StationDao.deleteAll();
            assertThatCode(() -> StationDao.save(STATION_FIXTURE))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("역 이름이 중복되면 예외가 발생한다.")
        void save_Fail_If_Exists() {
            StationDao.deleteAll();
            StationDao.save(STATION_FIXTURE);
            assertThatThrownBy(() -> StationDao.save(STATION_FIXTURE))
                    .isInstanceOf(StationDuplicateException.class)
                    .hasMessage("이미 존재하는 지하철역입니다.");
        }
    }


    @Test
    @DisplayName("전체 지하철 역을 조회할 수 있다")
    void findAll() {
        StationDao.save(STATION_FIXTURE);
        StationDao.save(STATION_FIXTURE2);
        StationDao.save(STATION_FIXTURE3);

        assertThat(StationDao.findAll()).isEqualTo(List.of(STATION_FIXTURE, STATION_FIXTURE2, STATION_FIXTURE3));
    }

    @Test
    @DisplayName("아이디로 지하철 역을 조회할 수 있다")
    void findById() {
        final Station station = StationDao.save(STATION_FIXTURE);
        final Station found = StationDao.findById(station.getId());

        assertThat(station).isEqualTo(found);
    }

    @Test
    @DisplayName("아이디로 지하철역을 삭제할 수 있다")
    void deleteById() {
        final Station station = StationDao.save(STATION_FIXTURE);
        final List<Station> stations = StationDao.findAll();
        StationDao.deleteById(station.getId());
        final List<Station> afterDelete = StationDao.findAll();

        assertThat(stations).isNotEmpty();
        assertThat(afterDelete).isEmpty();
    }
}
