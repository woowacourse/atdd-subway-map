package wooteco.subway.section.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.section.domain.Section;
import wooteco.subway.section.repository.SectionDao;
import wooteco.subway.station.domain.Station;
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
    private final Long lineId = 1L;

    @BeforeEach
    void setUp() {
        SectionDao sectionDao = new SectionDao(jdbcTemplate);
        sectionService = new SectionService(sectionDao);

        String lineQuery = "INSERT INTO line(color, name) VALUES(?, ?)";
        jdbcTemplate.update(lineQuery, "bg-red-600", "신분당선");

        String stationQuery = "INSERT INTO station(name) VALUES(?)";
        jdbcTemplate.update(stationQuery, "잠실역");
        jdbcTemplate.update(stationQuery, "짐실새내역");
        jdbcTemplate.update(stationQuery, "몽촌토성역");
        jdbcTemplate.update(stationQuery, "한성백제역");

        // 잠실역 (1) - 5 - 잠실새내역 (2) - 4 - 한성백제역 (4) - 7 - 몽촌토성역 (3)
        String sectionQuery = "INSERT INTO section(line_id, up_station_id, down_station_id, distance) VALUES(?, ?, ?, ?)";
        jdbcTemplate.update(sectionQuery, lineId, 2L, 4L, 4);
        jdbcTemplate.update(sectionQuery, lineId, 1L, 2L, 5);
        jdbcTemplate.update(sectionQuery, lineId, 4L, 3L, 7);
    }

    @DisplayName("구간이 하나만 존재할 때 UnavailableSectionDeleteException 반환한다")
    @Test
    void deleteSection_onlyOneSection_throwException() {
        String query = "DELETE FROM section WHERE line_id = ? AND (up_station_id = ? OR down_station_id = ?)";
        jdbcTemplate.update(query, lineId, 4L, 4L);

        assertThatThrownBy(() -> sectionService.delete(lineId, 1L))
                .isInstanceOf(UnavailableSectionDeleteException.class);
    }

    @DisplayName("stationId가 구간들 내에 존재하지 않을 때, NoSuchStationException 반환한다")
    @Test
    void deleteSection_stationDoesNotExistInSections_throwException() {
        assertThatThrownBy(() -> sectionService.delete(lineId, 5L))
                .isInstanceOf(NoSuchStationException.class);
    }

    @DisplayName("종점역 삭제할때, 삭제되는 역과 연결되어있던 역이 종점이 된다")
    @Test
    void deleteSection_endStation_newEndStation() {
        sectionService.delete(lineId, 3L);

        List<Section> sections = getSections();
        List<Section> expectedSections = Arrays.asList(
                new Section(lineId, new Station(2L), new Station(4L), 4),
                new Section(lineId, new Station(1L), new Station(2L), 5)
        );

        assertThat(sections).hasSameSizeAs(expectedSections).containsAll(expectedSections);
    }

    @DisplayName("종점역이 아닌 역을 삭제할때, 삭제되는 역과 연결되어 있던 두 역을 통합하여 새로운 구간을 형성한다")
    @Test
    void deleteSection_nonEndStation_combineTwoStations() {
        sectionService.delete(lineId, 4L);

        List<Section> sections = getSections();
        List<Section> expectedSections = Arrays.asList(
                new Section(lineId, new Station(1L), new Station(2L), 5),
                new Section(lineId, new Station(2L), new Station(3L), 11)
        );

        assertThat(sections).hasSameSizeAs(expectedSections).containsAll(expectedSections);
    }

    @DisplayName("구간을 저장할 때 이미 존재하는 구간이면 DuplicateSectionException을 반환한다")
    @Test
    void save_existingSection_throwException() {
        assertThatThrownBy(() -> sectionService.save(lineId, 2L, 4L, 5)).isInstanceOf(DuplicateSectionException.class);

        assertThatThrownBy(() -> sectionService.save(lineId, 2L, 4L, 5)).isInstanceOf(DuplicateSectionException.class);

        assertThatThrownBy(() -> sectionService.save(lineId, 4L, 2L, 3)).isInstanceOf(DuplicateSectionException.class);

        assertThatThrownBy(() -> sectionService.save(lineId, 4L, 1L, 5)).isInstanceOf(DuplicateSectionException.class);
    }

    @DisplayName("구간을 저장할 때 상행선 하행선 둘다 존재하지 않으면 NoSuchStationException을 반환한다")
    @Test
    void save_nonExistingSection_throwException() {
        assertThatThrownBy(() -> sectionService.save(lineId, 5L, 6L, 7)).isInstanceOf(NoSuchStationException.class);
    }

    @DisplayName("구간을 저장할 때 새로운 종점역을 만드는 구간 삽입이면 해당 구간을 단순 추가해준다")
    @Test
    void save_newEndStation_addSection() {
        String stationQuery = "INSERT INTO station(name) VALUES(?)";
        jdbcTemplate.update(stationQuery, "강남역");

        sectionService.save(lineId, 3L, 5L, 14);

        List<Section> sections = getSections();
        List<Section> expectedSections = Arrays.asList(
                new Section(lineId, new Station(1L), new Station(2L), 5),
                new Section(lineId, new Station(2L), new Station(4L), 4),
                new Section(lineId, new Station(4L), new Station(3L), 7),
                new Section(lineId, new Station(3L), new Station(5L), 14)
        );

        assertThat(sections).hasSameSizeAs(expectedSections).containsAll(expectedSections);
    }

    @DisplayName("구간을 저장할 때 중간에 낀 구간에 대한 수정 상황이면 기존 구간을 업데이트 한다")
    @Test
    void save_nonNewEndStation_updateExistingSection() {
        String stationQuery = "INSERT INTO station(name) VALUES(?)";
        jdbcTemplate.update(stationQuery, "강남역");

        sectionService.save(lineId, 2L, 5L, 1);
        // 잠실역 (1) - 5 - 잠실새내역 (2) - 1 - 강남역 (5) - 3 - 한성백제역 (4) - 7 - 몽촌토성역 (3)

        List<Section> sections = getSections();
        List<Section> expectedSections = Arrays.asList(
                new Section(lineId, new Station(1L), new Station(2L), 5),
                new Section(lineId, new Station(2L), new Station(5L), 1),
                new Section(lineId, new Station(5L), new Station(4L), 3),
                new Section(lineId, new Station(4L), new Station(3L), 7)
        );

        assertThat(sections).hasSameSizeAs(expectedSections).containsAll(expectedSections);

        // 잠실역 (1) - 5 - 잠실새내역 (2) - 1 - 강남역 (5) - 3 - 한성백제역 (4) - 7 - 몽촌토성역 (3)
        jdbcTemplate.update(stationQuery, "해운대역");

        sectionService.save(lineId, 6L, 4L, 1);

        // 잠실역 (1) - 5 - 잠실새내역 (2) - 1 - 강남역 (5) - 2 - 해운대역 (6) - 1 - 한성백제역 (4) - 7 - 몽촌토성역 (3)
        sections = getSections();
        expectedSections = Arrays.asList(
                new Section(lineId, new Station(1L), new Station(2L), 5),
                new Section(lineId, new Station(2L), new Station(5L), 1),
                new Section(lineId, new Station(5L), new Station(6L), 2),
                new Section(lineId, new Station(6L), new Station(4L), 1),
                new Section(lineId, new Station(4L), new Station(3L), 7)
        );

        assertThat(sections).hasSameSizeAs(expectedSections).containsAll(expectedSections);
    }

    // 잠실역 (1) - 5 - 잠실새내역 (2) - 4 - 한성백제역 (4) - 7 - 몽촌토성역 (3)
    @DisplayName("구간을 저장할 때 요청된 구간의 길이가 영향을 받는 구간의 길이 보다 길거나 같으면 IllegalSectionDistanceException을 반환한다")
    @Test
    void save_requestedSectionLongerOrEqualToExistingSection() {
        String stationQuery = "INSERT INTO station(name) VALUES(?)";
        jdbcTemplate.update(stationQuery, "강남역");

        assertThatThrownBy(() -> sectionService.save(lineId, 2L, 5L, 5)).isInstanceOf(IllegalSectionDistanceException.class);
    }

    private List<Section> getSections() {
        String query = "SELECT line_id, up_station_id, down_station_id, distance FROM section WHERE line_id = ?";
        return jdbcTemplate.query(
                query,
                (resultSet, rowNum) -> new Section(
                        resultSet.getLong("line_id"),
                        new Station(resultSet.getLong("up_station_id")),
                        new Station(resultSet.getLong("down_station_id")),
                        resultSet.getInt("distance")
                ),
                1
        );
    }
}
