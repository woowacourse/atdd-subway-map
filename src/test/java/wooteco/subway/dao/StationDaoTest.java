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
        StationDao.findAll().clear();
    }

    @DisplayName("지하철 역 저장 테스트")
    @Test
    void saveStation() {
        Station station = new Station("강남역");

        Station persistStation = StationDao.save(station);

        assertThat(persistStation.getId()).isNotNull();
        assertThat(persistStation.getName()).isEqualTo("강남역");
    }

    @DisplayName("중복된 이름의 지하철 역을 저장할 경우 예외가 발생한다.")
    @Test
    void saveDuplicateStation() {
        Station station = new Station("강남역");
        StationDao.save(station);

        assertThatThrownBy(() -> StationDao.save(station))
                .isInstanceOf(DuplicateKeyException.class);
    }

    @DisplayName("전체 역의 개수가 맞는지 확인한다.")
    @Test
    void findAllStation() {
        Station gangNam = new Station("강남역");
        Station jamSil = new Station("잠실역");

        StationDao.save(gangNam);
        StationDao.save(jamSil);

        assertThat(StationDao.findAll().size()).isEqualTo(2);
    }

    @DisplayName("특정 id를 가지는 역을 삭제한다.")
    @Test
    void deleteStation() {
        Station station = new Station("강남역");
        Station persistStation = StationDao.save(station);
        StationDao.deleteById(persistStation.getId());

        assertThat(StationDao.findAll()).isEmpty();
    }
}
