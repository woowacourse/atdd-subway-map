package wooteco.subway.station;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class StationDaoTest {

    private static final Station STATION1 = new Station("정릉역");
    private static final Station STATION2 = new Station("선정릉역");

    @BeforeEach
    void setTest() {
        StationDao.clean();
        StationDao.save(STATION1);
        StationDao.save(STATION2);
    }

    @DisplayName("전체 역 반환 테스트")
    @Test
    public void checkAllStation() {
        //given

        //when
        List<Station> stations = StationDao.findAll();

        //then
        assertTrue(stations.containsAll(Arrays.asList(STATION1, STATION2)));
    }

    @DisplayName("저장 테스트")
    @Test
    public void checkSave() {
        //given
        Station station = new Station("상봉역");
        StationDao.save(station);

        //when
        List<Station> stations = StationDao.findAll();

        //then
        assertTrue(stations.containsAll(Arrays.asList(STATION1, STATION2, station)));
    }

}