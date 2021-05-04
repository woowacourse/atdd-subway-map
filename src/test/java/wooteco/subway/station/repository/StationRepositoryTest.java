package wooteco.subway.station.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.station.domain.Station;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@Sql("classpath:tableinit.sql")
public class StationRepositoryTest {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    private StationRepository stationRepository;

    @BeforeEach
    void setUp() {
        stationRepository = new StationRepository(jdbcTemplate);
        String query = "INSERT INTO STATION(name) VALUES (?)";

        jdbcTemplate.update(query, "잠실역");
    }

    @DisplayName("역을 DB에 저장하면, 생성된 id값을 반환한다.")
    @Test
    void saveStation() {
        Station station = new Station("석촌역");

        Long id = stationRepository.save(station);
        assertThat(id).isEqualTo(2L);
    }

    @DisplayName("DB에 있는 역이름을 찾으면, true를 반환한다.")
    @Test
    void isExist() {
        assertThat(stationRepository.isExist(new Station("잠실역"))).isTrue();
        assertThat(stationRepository.isExist(new Station("석촌역"))).isFalse();
    }
}
