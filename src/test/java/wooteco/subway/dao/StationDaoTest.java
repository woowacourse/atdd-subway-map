package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.domain.Station;

class StationDaoTest {

    @Test
    @DisplayName("역 이름이 중복되면 예외가 발생한다.")
    void save_inValidName() {
        // given
        final Station station = new Station("선릉");
        StationDao.save(station);

        // when
        final Station newStation = new Station("선릉");

        // then
        assertThatThrownBy(() -> StationDao.save(newStation))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("중복된 이름의 역은 저장할 수 없습니다.");
    }
}
