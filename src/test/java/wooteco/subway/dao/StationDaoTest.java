package wooteco.subway.dao;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.domain.Station;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Transactional
public class StationDaoTest {
    @Autowired
    private StationDao stationDao;

    @Test
    @DisplayName("Station을 정상적으로 생성하는 지 테스트")
    public void createStation() {
        Long id = stationDao.create("강남역");
        assertThat(stationDao.findById(id).isPresent()).isTrue();
    }

    @Test
    @DisplayName("모든 Station을 정상적으로 조회하는 지 테스트")
    public void findAll() {
        stationDao.create("강남역");
        stationDao.create("홍대역");
        assertThat(stationDao.findAll()).hasSize(2);
    }

    @Test
    @DisplayName("특정 Station을 정상적으로 조회하는 지 테스트")
    public void findById() {
        String name = "강남역";
        Long id = stationDao.create(name);
        Optional<Station> station = stationDao.findById(id);
        assertThat(station.get().getName()).isEqualTo(name);
    }

    @Test
    @DisplayName("특정 Station을 정상적으로 수정하는 지 테스트")
    public void edit() {
        Long id = stationDao.create("강냄역");
        String changedName = "강남역";
        int changedRowCount = stationDao.edit(id, changedName);
        Optional<Station> editedStation = stationDao.findById(id);
        assertThat(changedRowCount).isEqualTo(1);
        assertThat(editedStation.get().getName()).isEqualTo(changedName);
    }

    @Test
    @DisplayName("특정 Station을 정상적으로 삭제하는 지 테스트")
    public void deleteById() {
        Long id = stationDao.create("강남역");
        stationDao.deleteById(id);
        assertThat(stationDao.findById(id).isPresent()).isFalse();
    }
}
