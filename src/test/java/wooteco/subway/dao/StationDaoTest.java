package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.Test;
import wooteco.subway.domain.Station;

class StationDaoTest {

    @Test
    void save() {
        // given
        Station station = new Station("범고래");

        // when
        Station result = StationDao.save(station);

        // then
        assertThat(station).isEqualTo(result);
    }

    @Test
    void findAll() {
        // given
        Station station1 = StationDao.save(new Station("범고래"));
        Station station2 = StationDao.save(new Station("애쉬"));

        // when
        List<Station> stations = StationDao.findAll();

        // then
        assertThat(stations)
            .hasSize(2)
            .contains(station1, station2);
    }
}
