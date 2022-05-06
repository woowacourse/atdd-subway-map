package wooteco.subway.dao;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.domain.Station;
import wooteco.subway.utils.exception.IdNotFoundException;
import wooteco.subway.utils.exception.NameDuplicatedException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@Transactional
@SpringBootTest
class StationRepositoryTest {

    @Autowired
    StationRepository stationRepository;

    @DisplayName("역을 저장한다.")
    @Test
    void save() {
        Station station = new Station("신림역");
        Station saveStation = stationRepository.save(station);

        assertAll(
                () -> assertThat(saveStation.getId()).isNotNull(),
                () -> assertThat(saveStation).isEqualTo(station)
        );
    }

    @DisplayName("역 저장 시 유니크 키 이름에 접근하면 에러가 발생한다.")
    @Test
    void saveUniqueException() {
        Station station = new Station("신림역");
        stationRepository.save(station);

        assertThatThrownBy(() -> stationRepository.save(station))
                .isInstanceOf(NameDuplicatedException.class);
    }

    @DisplayName("모든 역을 조회한다.")
    @Test
    void findAll() {
        Station station1 = new Station("신림역");
        stationRepository.save(station1);
        Station station2 = new Station("신대방역");
        stationRepository.save(station2);

        List<Station> stations = stationRepository.findAll();

        assertAll(
                () -> assertThat(stations).hasSize(2),
                () -> assertThat(stations).containsExactly(station1, station2)
        );
    }

    @DisplayName("역을 삭제한다")
    @Test
    void deleteById() {
        Station saveStation = stationRepository.save(new Station("신림역"));
        stationRepository.deleteById(saveStation.getId());

        assertThat(stationRepository.findAll()).isEmpty();
    }

    @DisplayName("역 삭제 시 존재하지 않는 id일 경우 에러를 발생한다")
    @Test
    void deleteIdNotFound() {
        assertThatThrownBy(() -> stationRepository.deleteById(1L))
                .isInstanceOf(IdNotFoundException.class);
    }

    @DisplayName("이름으로 역이 존재하는지 조회한다.")
    @Test
    void findByName() {
        stationRepository.save(new Station("신림역"));
        assertThat(stationRepository.findByName("신림역")).isNotNull();
    }


    @DisplayName("이름으로 역 조회시 없다면 Optional empty를 반환한다.")
    @Test
    void findByNameNoName() {
        assertThat(stationRepository.findByName("신림역").isEmpty()).isTrue();
    }

    @DisplayName("id로 역을 조회한다.")
    @Test
    void findById() {
        Station saveStation = stationRepository.save(new Station("신림역"));
        assertThat(stationRepository.findById(saveStation.getId())).isNotNull();
    }

    @DisplayName("id로 역 조회시 존재하지 않는 id일 경우 에러를 발생한다")
    @Test
    void findIdNotFound() {
        assertThatThrownBy(() -> stationRepository.findById(1L))
                .isInstanceOf(IdNotFoundException.class);
    }
}
