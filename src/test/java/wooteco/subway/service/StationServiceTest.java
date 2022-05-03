package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;
import wooteco.subway.exception.DuplicatedStationNameException;

class StationServiceTest {

    @DisplayName("추가하려는 역의 이름이 이미 존재하면 예외를 발생시킨다.")
    @Test
    void createStation_exception() {
        StationDao.save(new Station("서울역"));

        assertThatThrownBy(() -> StationService.save(new Station("서울역")))
            .isInstanceOf(DuplicatedStationNameException.class);
    }

    @DisplayName("새로운 역을 추가할 수 있다.")
    @Test
    void createStation_success() {
        StationDao.save(new Station("서울역"));

        Station station = new Station("선릉역");

        assertThat(StationService.save(station)).isEqualTo(station);
    }

}