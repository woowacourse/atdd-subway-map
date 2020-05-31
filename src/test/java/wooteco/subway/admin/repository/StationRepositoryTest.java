package wooteco.subway.admin.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.relational.core.conversion.DbActionExecutionException;
import wooteco.subway.admin.domain.Station;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJdbcTest
public class StationRepositoryTest {
    @Autowired
    private StationRepository stationRepository;

    @Test
    void save() {
        String name = "강남역";
        Station station = new Station(name);

        Station stationPersist = stationRepository.save(station);

        assertThat(stationPersist.getId()).isNotNull();
    }

    @Test
    void saveAlreadyExists() {
        String name = "신촌역";
        stationRepository.save(new Station(name));
        assertThatThrownBy(() -> stationRepository.save(new Station(name)))
                .isInstanceOf(DbActionExecutionException.class);
    }

    @Test
    void findByNameNotExist() {
        Station stationNotExist = stationRepository.findByName("존재하지않는역");
        assertThat(stationNotExist).isNull();
    }
}
