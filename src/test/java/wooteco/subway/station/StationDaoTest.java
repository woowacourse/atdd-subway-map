package wooteco.subway.station;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.exception.DuplicateException;

@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql("classpath:tableInit.sql")
class StationDaoTest {

    private final StationDao stationDao;

    public StationDaoTest(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    @Test
    @DisplayName("역 한개가 저장된다.")
    void save() {
        Station station = stationDao.save(new Station("잠실역"));
        System.out.println(station.getId());
        assertThat(station.getId()).isEqualTo(1L);
        assertThat(station.getName()).isEqualTo("잠실역");
    }

    @Test
    @DisplayName("중복된 이름을 갖는 역은 저장이 안된다.")
    void duplicateSaveValidate() {
        Station station = new Station("잠실역");
        stationDao.save(station);

        assertThatThrownBy(() -> {
            stationDao.save(station);
        }).isInstanceOf(DuplicateException.class);
    }

    @Test
    @DisplayName("전체 가져오기 테스트")
    void findAll() {
        Station station1 = stationDao.save(new Station("잠실역"));
        Station station2 = stationDao.save(new Station("잠실새내역"));

        List<Station> stations = Arrays.asList(station1, station2);

        List<Station> stationsAll = stationDao.findAll();
        assertThat(stationsAll).hasSize(2);

        for (int i = 0; i < stationsAll.size(); i++) {
            assertThat(stationsAll.get(i).getId()).isEqualTo(stations.get(i).getId());
            assertThat(stationsAll.get(i).getName()).isEqualTo(stations.get(i).getName());
        }
    }

    @Test
    @DisplayName("id를 이용해 한개 삭제한다.")
    void delete() {
        stationDao.save(new Station("잠실역"));
        assertThatCode(() -> stationDao.delete(1L)).doesNotThrowAnyException();
        assertThat(stationDao.findAll()).hasSize(0);
    }
}