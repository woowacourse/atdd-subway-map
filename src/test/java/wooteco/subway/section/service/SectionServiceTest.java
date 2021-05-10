package wooteco.subway.section.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.section.domain.Section;
import wooteco.subway.section.repository.SectionRepository;
import wooteco.subway.station.service.NoSuchStationException;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@JdbcTest
@Sql("classpath:tableInit.sql")
public class SectionServiceTest {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    private SectionService sectionService;

    @BeforeEach
    void setUp() {
        SectionRepository sectionRepository = new SectionRepository(jdbcTemplate);
        sectionService = new SectionService(sectionRepository);

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

    @DisplayName("구간이 하나만 존재할 때 UnavailableSectionDeleteException 반환한다")
    @Test
    void deleteSection_onlyOneSection_throwException() {
        String query = "DELETE FROM section WHERE line_id = ? AND (up_station_id = ? OR down_station_id = ?)";
        jdbcTemplate.update(query, 1L, 4L, 4L);

        assertThatThrownBy(() -> sectionService.deleteSection(1L, 1L))
                .isInstanceOf(UnavailableSectionDeleteException.class);
    }

    @DisplayName("stationId가 구간들 내에 존재하지 않을 때, NoSuchStationException 반환한다")
    @Test
    void deleteSection_stationDoesNotExistInSections_throwException() {
        assertThatThrownBy(() -> sectionService.deleteSection(1L, 5L))
                .isInstanceOf(NoSuchStationException.class);
    }

    @DisplayName("종점역 삭제할때, 삭제되는 역과 연결되어있던 역이 종점이 된다")
    @Test
    void deleteSection_endStation_newEndStation() {
        sectionService.deleteSection(1L, 3L);

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

    @DisplayName("종점역이 아닌 역을 삭제할때, 삭제되는 역과 연결되어 있던 두 역을 통합하여 새로운 구간을 형성한다")
    @Test
    void deleteSection_nonEndStation_combineTwoStations() {
        sectionService.deleteSection(1L, 4L);

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
                new Section(1L, 1L, 2L, 5),
                new Section(1L, 2L, 3L, 11)
        );

        assertThat(sections).hasSameSizeAs(expectedSections).containsAll(expectedSections);
    }
}
