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
}
