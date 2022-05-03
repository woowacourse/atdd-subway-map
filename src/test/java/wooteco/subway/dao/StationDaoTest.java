package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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

    @Test
    void validateDuplication() {
        // given
        Station station1 = new Station("범고래");
        Station station2 = new Station("범고래");

        // when
        StationDao.save(station1);

        // then
        assertThatThrownBy(() -> StationDao.save(station2))
            .hasMessage("중복된 이름이 존재합니다.")
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void delete() {
        // given
        Station station = StationDao.save(new Station("범고래"));

        // when
        StationDao.delete(station);
        List<Station> stations = StationDao.findAll();

        // then
        assertThat(stations)
            .hasSize(0)
            .doesNotContain(station);
    }
}
