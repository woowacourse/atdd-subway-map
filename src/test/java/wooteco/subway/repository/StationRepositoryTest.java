package wooteco.subway.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.domain.Station;
import wooteco.subway.utils.exception.IdNotFoundException;
import wooteco.subway.utils.exception.NameDuplicatedException;

class StationRepositoryTest extends RepositoryTest {

    @DisplayName("역을 저장한다.")
    @Test
    void save() {
        Station station = new Station("신림역");
        Station savedStation = stationRepository.save(station);

        assertAll(
                () -> assertThat(savedStation.getId()).isNotNull(),
                () -> assertThat(savedStation.getName()).isEqualTo(station.getName())
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
        assertThat(stations).hasSize(2);
    }

    @DisplayName("역을 삭제한다")
    @Test
    void deleteById() {
        Station savedStation = stationRepository.save(new Station("신림역"));
        stationRepository.deleteById(savedStation.getId());

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
    void isNameExistsTrue() {
        stationRepository.save(new Station("신림역"));
        assertThat(stationRepository.isNameExists("신림역")).isTrue();
    }


    @DisplayName("이름으로 역 조회시 없다면 false를 반환한다.")
    @Test
    void isNameExistsFalse() {
        assertThat(stationRepository.isNameExists("신림역")).isFalse();
    }

    @DisplayName("id로 역을 조회한다.")
    @Test
    void findById() {
        Station savedStation = stationRepository.save(new Station("신림역"));
        assertThat(stationRepository.findById(savedStation.getId())).isNotNull();
    }

    @DisplayName("id로 역 조회시 존재하지 않는 id일 경우 에러를 발생한다")
    @Test
    void findIdNotFound() {
        assertThatThrownBy(() -> stationRepository.findById(1L))
                .isInstanceOf(IdNotFoundException.class);
    }
}
