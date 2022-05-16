package wooteco.subway.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.station.Station;
import wooteco.subway.entity.StationEntity;
import wooteco.subway.exception.NotFoundException;

@SuppressWarnings("NonAsciiCharacters")
class StationRepositoryTest extends RepositoryTest {

    @Autowired
    private StationRepository repository;

    @Autowired
    private StationDao dao;

    @Test
    void findAllStations_메서드는_모든_지하철역들을_조회하여_도메인들의_리스트로_반환() {
        testFixtureManager.saveStations("강남역", "선릉역", "잠실역");

        List<Station> actual = repository.findAllStations();
        List<Station> expected = List.of(
                new Station(1L, "강남역"),
                new Station(2L, "선릉역"),
                new Station(3L, "잠실역"));

        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("findExistingStation 메서드는 id에 대응되는 지하철역을 조회")
    @Nested
    class FindByIdTest {

        @Test
        void id에_대응되는_지하철역이_존재하는_경우_도메인으로_반환() {
            testFixtureManager.saveStations("강남역", "선릉역");

            Station actual = repository.findExistingStation(1L);
            Station expected = new Station(1L, "강남역");

            assertThat(actual).isEqualTo(expected);
        }

        @Test
        void id에_대응되는_지하철역이_존재하지_않는_경우_예외_발생() {
            assertThatThrownBy(() -> repository.findExistingStation(1L))
                    .isInstanceOf(NotFoundException.class);
        }
    }

    @DisplayName("checkExistingStationName 메서드는 해당 이름의 지하철역의 존재 여부를 반환")
    @Nested
    class CheckExistingStationNameTest {

        @Test
        void 존재하는_지하철역의_이름인_경우_참_반환(){
            testFixtureManager.saveStations("강남역");

            boolean actual = repository.checkExistingStationName("강남역");

            assertThat(actual).isTrue();
        }

        @Test
        void 존재하지_않는_지하철역의_이름인_경우_거짓_반환(){
            boolean actual = repository.checkExistingStationName("없는 이름");

            assertThat(actual).isFalse();
        }
    }

    @Test
    void save_메서드는_지하철역_도메인을_받아_새로운_지하철역을_저장() {
        repository.save(new Station("강남역"));
        List<StationEntity> actual = dao.findAll();
        List<StationEntity> expected = List.of(new StationEntity(1L, "강남역"));

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void delete_메서드는_지하철역_도메인을_받아_해당_새로운_지하철역을_제거() {
        testFixtureManager.saveStations("강남역", "잠실역");

        repository.delete(new Station(1L, "강남역"));
        List<StationEntity> actual = dao.findAll();
        List<StationEntity> expected = List.of(new StationEntity(2L, "잠실역"));

        assertThat(actual).isEqualTo(expected);
    }
}
