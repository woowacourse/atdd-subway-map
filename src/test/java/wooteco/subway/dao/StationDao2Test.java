package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.domain.Station;

@SpringBootTest
@Transactional
class StationDao2Test {

    @Autowired
    private StationDao2 stationDao2;

    @Test
    @DisplayName("역을 저장하면 저장된 역 정보를 반환한다.")
    void save() {
        // given
        final String name = "선릉";
        final Station station = new Station(name);

        // when
        final Station savedStation = stationDao2.save(station);

        // then
        assertThat(savedStation.getName()).isEqualTo(name);
    }
}