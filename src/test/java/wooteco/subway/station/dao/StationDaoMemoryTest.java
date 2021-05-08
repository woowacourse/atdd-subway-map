package wooteco.subway.station.dao;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.station.Station;

class StationDaoMemoryTest {

    private static final StationDaoMemory stationDaoMemory = new StationDaoMemory();
    private static final Station STATION1 = new Station("정릉역");
    private static final Station STATION2 = new Station("선정릉역");

    @BeforeEach
    void setTest() {
        stationDaoMemory.clean();
        stationDaoMemory.save(STATION1);
        stationDaoMemory.save(STATION2);
    }

    @DisplayName("전체 역 반환 테스트")
    @Test
    public void checkAllStation() {
        //given

        //when
        List<Station> stations = stationDaoMemory.showAll();

        //then
        assertTrue(stations.containsAll(Arrays.asList(STATION1, STATION2)));
    }

    @DisplayName("저장 테스트")
    @Test
    public void checkSave() {
        //given
        Station station = new Station("상봉역");
        stationDaoMemory.save(station);

        //when
        List<Station> stations = stationDaoMemory.showAll();

        //then
        assertTrue(stations.containsAll(Arrays.asList(STATION1, STATION2, station)));
    }

}