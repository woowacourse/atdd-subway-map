package wooteco.subway.admin.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import wooteco.subway.admin.domain.Station;

import static org.assertj.core.api.Assertions.assertThat;

@DataJdbcTest
public class StationRepositoryTest {
    @Autowired
    private StationRepository stationRepository;

    @DisplayName("역 추가 테스트")
    @Test
    void save() {
        Station station = new Station("강남역");

        Station persistStation = stationRepository.save(station);

        assertThat(persistStation.getId()).isNotNull();
    }
}
