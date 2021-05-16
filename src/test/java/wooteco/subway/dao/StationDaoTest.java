package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.domain.Station;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Sql("classpath:initializeTable.sql")
class StationDaoTest {

    @Autowired
    private StationDao stationDao;

    @DisplayName("전체 역 반환 테스트")
    @Test
    public void checkAllStation() {
        // given
        Station station1 = new Station("상봉역");
        stationDao.save(station1);
        Station station2 = new Station("구로디지털단지역");
        stationDao.save(station2);

        // when
        List<Station> stations = stationDao.showAll();

        // then
        assertThat(stations.get(0).getName()).isEqualTo(station1.getName());
        assertThat(stations.get(1).getName()).isEqualTo(station2.getName());
    }

    @DisplayName("저장 테스트")
    @Test
    public void checkSave() {
        // given
        Station station1 = new Station("상봉역");

        // when
        stationDao.save(station1);

        // then
        List<Station> stations = stationDao.showAll();
        Station responseStation = stations.get(0);
        assertThat(responseStation.getName()).isEqualTo("상봉역");
    }

    @DisplayName("삭제 테스트")
    @Test
    public void delete() {
        // given
        Station station1 = new Station("상봉역");
        stationDao.save(station1);

        // when
        int deleteCount = stationDao.delete(1L);

        // then
        assertThat(deleteCount).isEqualTo(1);
    }

    @DisplayName("삭제 실패 테스트")
    @Test
    public void deleteVoidStation() {
        // given

        // when
        int deleteCount = stationDao.delete(999L);

        // then
        assertThat(deleteCount).isEqualTo(0);
    }
}