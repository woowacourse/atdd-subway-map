package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import wooteco.subway.controller.AcceptanceTest;
import wooteco.subway.dao.entity.StationEntity;
import wooteco.subway.exception.NotFoundException;

class StationDaoTest extends AcceptanceTest {

    @Autowired
    private StationDao stationDao;

    @DisplayName("역 정보를 저장한다.")
    @Test
    void save() {
        StationEntity expected = new StationEntity("강남역");
        StationEntity actual = stationDao.save(expected);

        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("모든 역 정보를 조회한다.")
    @Test
    void findAll() {
        StationEntity station1 = new StationEntity("강남역");
        StationEntity station2 = new StationEntity("신논현역");
        stationDao.save(station1);
        stationDao.save(station2);

        List<StationEntity> actual = stationDao.findAll();

        assertThat(actual).containsExactly(station1, station2);
    }

    @DisplayName("역을 삭제한다.")
    @Test
    void delete() {
        StationEntity station = stationDao.save(new StationEntity("강남역"));

        stationDao.deleteById(station.getId());

        assertThatThrownBy(() -> stationDao.deleteById(station.getId()))
                .isInstanceOf(NotFoundException.class);
    }

    @DisplayName("역을 삭제할 때 id에 맞는 역이 없으면 예외를 발생시킨다.")
    @Test
    void deleteException() {
        assertThatThrownBy(() -> stationDao.deleteById(1L))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("1에 해당하는 지하철 역을 찾을 수 없습니다.");
    }
}
