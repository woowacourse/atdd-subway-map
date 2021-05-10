package wooteco.subway.station.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import wooteco.subway.station.Station;

import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class StationServiceTest {

    @Autowired
    private StationService stationService;

    @BeforeEach
    void deleteLog() {
        stationService.removeAll();
    }

    @DisplayName("역 저장")
    @Test
    void save() throws Exception {
        Station save = 역_생성("잠실역");

        //then
        assertThat(save).usingRecursiveComparison().isEqualTo(new Station(save.getId(), "잠실역"));
    }

    private Station 역_생성(String name) {
        //given
        Station station = new Station(name);

        //when
        Station save = stationService.save(station);
        return save;
    }

    @DisplayName("역 조회 - 아이디")
    @Test
    void findById() throws Exception {
        Station save = 역_생성("잠실역");
        역_생성("강남역");

        //then
        assertThat(stationService.findStationById(save.getId())).usingRecursiveComparison().isEqualTo(new Station(save.getId(), "잠실역"));
    }

    @DisplayName("역 조회 - 이름")
    @Test
    void findByName() throws Exception {
        Station save = 역_생성("잠실역");
        역_생성("강남역");

        //then
        assertThat(stationService.findStationByName("잠실역").get()).usingRecursiveComparison().isEqualTo(new Station(save.getId(), "잠실역"));
    }

    @DisplayName("역 전체 조회")
    @Test
    void findAll() throws Exception {
        Station save1 = 역_생성("잠실역");
        Station save2 = 역_생성("강남역");

        //then
        assertThat(stationService.findAll()).usingRecursiveComparison()
                .isEqualTo(Arrays.asList(new Station(save1.getId(), "잠실역"), new Station(save2.getId(), "강남역")));
    }

    @DisplayName("역 삭제")
    @Test
    void remove() throws Exception {
        Station save1 = 역_생성("잠실역");
        Station save2 = 역_생성("강남역");

        //then
        stationService.remove(save1.getId());
        assertThat(stationService.findAll()).usingRecursiveComparison()
                .isEqualTo(Collections.singletonList(new Station(save2.getId(), "강남역")));
    }

    @DisplayName("역 전체 삭제")
    @Test
    void removeAll() throws Exception {
        역_생성("잠실역");
        역_생성("강남역");

        //then
        stationService.removeAll();
        assertThat(stationService.findAll()).hasSize(0);
    }
}
