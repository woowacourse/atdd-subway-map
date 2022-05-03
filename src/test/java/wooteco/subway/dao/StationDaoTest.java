package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import wooteco.subway.domain.Station;

class StationDaoTest {

    @DisplayName("이름값을 받아 해당 이름값을 가진 역이 있는지 확인한다.")
    @ParameterizedTest
    @CsvSource({"서울역, true", "선릉역, false"})
    void exists(String name, boolean expected) {
        Station seoul = new Station("서울역");
        StationDao.save(seoul);
        Station newStation = new Station(name);

        boolean actual = StationDao.exists(newStation);

        assertThat(actual).isEqualTo(expected);
    }
}