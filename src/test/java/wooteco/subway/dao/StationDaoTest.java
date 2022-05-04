package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import wooteco.subway.domain.Station;

class StationDaoTest {

    @AfterEach
    void rollback() {
        StationDao.deleteAll();
    }

    @Test
    @DisplayName("지하철역을 등록할 수 있다.")
    void save() {
        // given
        final Station station = new Station("지하철역이름");

        // when
        final Station saveStation = StationDao.save(station);

        // then
        assertThat(station).isSameAs(saveStation);
    }

    @Test
    @DisplayName("지하철역 목록을 조회할 수 있다.")
    void findAll() {
        // given
        final Station station1 = new Station("지하철역이름");
        final Station station2 = new Station("새로운지하철역이름");
        final Station station3 = new Station("또다른지하철역이름");

        StationDao.save(station1);
        StationDao.save(station2);
        StationDao.save(station3);

        // when
        final List<Station> stations = StationDao.findAll();

        // then
        assertThat(stations).hasSize(3)
                .extracting("name")
                .contains("지하철역이름", "새로운지하철역이름", "또다른지하철역이름");
    }

    @Nested
    @DisplayName("지하철역 id를 가지고")
    class DeleteById {
        @Test
        @DisplayName("삭제할 수 있다.")
        void valid() {
            // given
            final Station station = new Station("지하철역이름");
            final Station saveStation = StationDao.save(station);

            // when & then
            assertDoesNotThrow(() -> StationDao.deleteById(saveStation.getId()));
        }

        @Test
        @DisplayName("id가 없는 경우 예외가 발생한다.")
        void invalidOfNumber() {
            // given
            final Station station = new Station("지하철역이름");
            final Station saveStation = StationDao.save(station);

            // when & then
            assertThatThrownBy(() -> LineDao.deleteById(saveStation.getId() + 1L))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }
}
