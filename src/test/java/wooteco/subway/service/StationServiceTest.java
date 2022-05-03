package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;
import wooteco.subway.exception.DuplicatedStationException;

class StationServiceTest {

    @DisplayName("추가하려는 역의 이름이 이미 존재하면 예외를 발생시킨다.")
    @Test
    void createStation_exception() {
        Station station = StationService.save(new Station("서울역"));

        assertThatThrownBy(() -> StationService.save(new Station("서울역")))
            .isInstanceOf(DuplicatedStationException.class);

        StationDao.deleteById(station.getId());
    }

    @DisplayName("새로운 역을 추가할 수 있다.")
    @Test
    void createStation_success() {
        Station station = StationService.save(new Station("서울역"));

        assertThat(station.getName()).isEqualTo("서울역");

        StationDao.deleteById(station.getId());
    }

}