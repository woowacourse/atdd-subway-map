package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DuplicateKeyException;
import wooteco.subway.domain.Station;

class StationDaoTest {

    @AfterEach
    void cleanUp() {
        StationDao.deleteAll();
    }

    @DisplayName("지하철 역 저장 테스트")
    @Test
    void save_Station() {
        Station station = new Station("강남역");

        Station persistStation = StationDao.save(station);

        assertThat(persistStation.getId()).isNotNull();
        assertThat(persistStation.getName()).isEqualTo("강남역");
    }

    @DisplayName("중복된 이름의 지하철 역을 저장할 경우 예외가 발생한다.")
    @Test
    void save_Duplicated_Station() {
        Station station = new Station("강남역");
        StationDao.save(station);

        assertThatThrownBy(() -> StationDao.save(station))
                .isInstanceOf(DuplicateKeyException.class);
    }
}
