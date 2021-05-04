package wooteco.subway.station;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import wooteco.subway.station.dao.StationDao;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class CollectionStationDaoTest {

    @Autowired
    private StationDao stationDao;

    @DisplayName("중복된 역은 허용하지 않는다.")
    @Test
    void duplicateStation() {
        Station station = new Station("옥수역");
        Station newStation = new Station("옥수역");
        stationDao.save(station);
        assertThatThrownBy(() -> {
            stationDao.save(newStation);
        }).isInstanceOf(IllegalArgumentException.class);
    }

}