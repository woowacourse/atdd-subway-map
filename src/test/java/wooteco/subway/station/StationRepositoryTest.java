package wooteco.subway.station;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.exception.DuplicateStationNameException;
import wooteco.subway.station.domain.Station;
import wooteco.subway.station.repository.StationRepository;

@DisplayName("Station Repository")
@Sql("classpath:tableInit.sql")
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@SpringBootTest
class StationRepositoryTest {

    private final StationRepository stationRepository;

    public StationRepositoryTest(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    @DisplayName("save 메서드는 DB에 중복된 이름을 갖는 역이 없다면, DB에 역을 저장하고 저장된 역을 반환한다.")
    @Test
    void save() {
        Station station = stationRepository.save(new Station("잠실역"));
        assertThat(station.getId()).isEqualTo(1L);
        assertThat(station.getName()).isEqualTo("잠실역");
    }

    @DisplayName("save 메서드는 DB에 중복된 이름을 갖는 역이 있다면, 예외를 던진다.")
    @Test
    void duplicateSaveValidate() {
        Station station = new Station("잠실역");
        stationRepository.save(station);

        assertThatThrownBy(() -> {
            stationRepository.save(station);
        }).isInstanceOf(DuplicateStationNameException.class);
    }

    @DisplayName("findAll 메서드는 DB에 역이 하나라도 있다면, 모든 역을 리스트로 반환한다.")
    @Test
    void findAll() {
        Station station1 = stationRepository.save(new Station("잠실역"));
        Station station2 = stationRepository.save(new Station("잠실새내역"));

        List<Station> stations = stationRepository.findAll();

        assertThat(stations).hasSize(2);
        assertThat(stations).containsExactly(station1, station2);
    }

    @DisplayName("findAll 메서드는 DB에 역이 하나라도 없으면, 비어있는 역 리스트를 반환한다.")
    @Test
    void notExistLineFindException() {
        List<Station> stations = stationRepository.findAll();
        assertThat(stations).hasSize(0);
    }

    @DisplayName("delete 메서드는 DB에 삭제하려는 역이 있다면, 역을 삭제하고 1을 반환한다.")
    @Test
    void delete() {
        stationRepository.save(new Station("잠실역"));
        assertThat(stationRepository.delete(1L)).isEqualTo(1);
    }

    @DisplayName("delete 메서드는 DB에 삭제하려는 역이 없다면, 0을 반환한다.")
    @Test
    void notDelete() {
        stationRepository.save(new Station("잠실역"));
        assertThatCode(() -> stationRepository.delete(1L)).doesNotThrowAnyException();
        assertThat(stationRepository.findAll()).hasSize(0);
    }

}