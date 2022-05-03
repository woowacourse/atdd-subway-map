package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.*;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import wooteco.subway.domain.Station;

public class StationDaoTest {

    private StationDao stationDao;

    @BeforeEach
    void setUp() {
        stationDao = new StationDao();
    }

    @Test
    @DisplayName("Station 을 저장한다.")
    void save() {
        //given
        Station station = new Station("lajukang");

        //when
        Station actual = stationDao.save(station);

        //then
        assertThat(actual.getName()).isEqualTo(station.getName());
    }
}
