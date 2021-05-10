package wooteco.subway.section.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.section.domain.Section;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@Sql("classpath:tableInit.sql")
public class SectionRepositoryTest {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    private SectionRepository sectionRepository;

    @BeforeEach
    void setUp() {
        sectionRepository = new SectionRepository(jdbcTemplate);
        String lineQuery = "INSERT INTO line(color, name) VALUES(?, ?)";
        jdbcTemplate.update(lineQuery, "bg-red-600", "신분당선");

        String stationQuery = "INSERT INTO station(name) VALUES(?)";
        jdbcTemplate.update(stationQuery, "잠실역");
        jdbcTemplate.update(stationQuery, "짐실새내역");
        jdbcTemplate.update(stationQuery, "몽촌토성역");
        jdbcTemplate.update(stationQuery, "한성백제역");

        // 잠실역 (1) - 5 - 잠실새내역 (2) - 4 - 한성백제역 (4) - 7 - 몽촌토성역 (3)
        String sectionQuery = "INSERT INTO section(line_id, up_station_id, down_station_id, distance) VALUES(?, ?, ?, ?)";
        jdbcTemplate.update(sectionQuery, 1L, 2L, 4L, 4);
        jdbcTemplate.update(sectionQuery, 1L, 1L, 2L, 5);
        jdbcTemplate.update(sectionQuery, 1L, 4L, 3L, 7);
    }

    @DisplayName("종점인 역을 제거할 때 종점인 역과 연결되어있던 역을 종점으로 해서 구간을 변경한다")
    @Test
    void deleteSection_endStation() {
        sectionRepository.deleteRelevantSections(1L, 3L);

        String query = "SELECT line_id, up_station_id, down_station_id, distance FROM section WHERE line_id = ?";
        List<Section> sections = jdbcTemplate.query(
                query,
                (resultSet, rowNum) -> new Section(
                        resultSet.getLong("line_id"),
                        resultSet.getLong("up_station_id"),
                        resultSet.getLong("down_station_id"),
                        resultSet.getInt("distance")
                ),
                1
        );
        List<Section> expectedSections = Arrays.asList(
                new Section(1L, 2L, 4L, 4),
                new Section(1L, 1L, 2L, 5)
        );

        assertThat(sections).hasSameSizeAs(expectedSections).containsAll(expectedSections);
    }

    @DisplayName("종점이 아닌 역을 제거할 때 해당 역과 관련된 두 역을 다 지운다")
    @Test
    void deleteSection_nonEndStation() {
        sectionRepository.deleteRelevantSections(1L, 4L);

        String query = "SELECT line_id, up_station_id, down_station_id, distance FROM section WHERE line_id = ?";
        List<Section> sections = jdbcTemplate.query(
                query,
                (resultSet, rowNum) -> new Section(
                        resultSet.getLong("line_id"),
                        resultSet.getLong("up_station_id"),
                        resultSet.getLong("down_station_id"),
                        resultSet.getInt("distance")
                ),
                1
        );
        List<Section> expectedSections = Arrays.asList(
                new Section(1L, 1L, 2L, 5)
        );

        assertThat(sections).hasSameSizeAs(expectedSections).containsAll(expectedSections);
    }
}
