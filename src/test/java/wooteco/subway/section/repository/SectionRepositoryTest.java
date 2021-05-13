package wooteco.subway.section.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.section.domain.Section;
import wooteco.subway.section.domain.Sections;
import wooteco.subway.station.domain.Station;
import wooteco.subway.station.domain.Stations;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@Sql("classpath:tableInit.sql")
public class SectionRepositoryTest {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    private SectionRepository sectionRepository;
    private long lineId = 1L;

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
        jdbcTemplate.update(sectionQuery, lineId, 2L, 4L, 4);
        jdbcTemplate.update(sectionQuery, lineId, 1L, 2L, 5);
        jdbcTemplate.update(sectionQuery, lineId, 4L, 3L, 7);
    }

    @DisplayName("종점인 역을 제거할 때 종점인 역과 연결되어있던 역을 종점으로 해서 구간을 변경한다")
    @Test
    void deleteSection_endStation() {
        sectionRepository.deleteByStationId(lineId, 3L);

        String query = "SELECT line_id, up_station_id, down_station_id, distance FROM section WHERE line_id = ?";
        List<Section> sections = jdbcTemplate.query(
                query,
                (resultSet, rowNum) -> new Section(
                        resultSet.getLong("line_id"),
                        new Station(resultSet.getLong("up_station_id")),
                        new Station(resultSet.getLong("down_station_id")),
                        resultSet.getInt("distance")
                ),
                1
        );
        List<Section> expectedSections = Arrays.asList(
                new Section(lineId, new Station(2L), new Station(4L), 4),
                new Section(lineId, new Station(1L), new Station(2L), 5)
        );

        assertThat(sections).hasSameSizeAs(expectedSections).containsAll(expectedSections);
    }

    @DisplayName("종점이 아닌 역을 제거할 때 해당 역과 관련된 두 역을 다 지운다")
    @Test
    void deleteSection_nonEndStation() {
        sectionRepository.deleteByStationId(1L, 4L);

        String query = "SELECT line_id, up_station_id, down_station_id, distance FROM section WHERE line_id = ?";
        List<Section> sections = jdbcTemplate.query(
                query,
                (resultSet, rowNum) -> new Section(
                        resultSet.getLong("line_id"),
                        new Station(resultSet.getLong("up_station_id")),
                        new Station(resultSet.getLong("down_station_id")),
                        resultSet.getInt("distance")
                ),
                1
        );
        List<Section> expectedSections = Arrays.asList(
                new Section(lineId, new Station(1L), new Station(2L), 5)
        );

        assertThat(sections).hasSameSizeAs(expectedSections).containsAll(expectedSections);
    }

    @DisplayName("상행선으로 등록되어 있는 역이면 true 아니면 false를 반환한다")
    @ParameterizedTest
    @CsvSource({"1,true", "2,true", "3,false", "4,true"})
    void isExistInUpStation(long stationId, boolean expectedResult) {
        assertThat(sectionRepository.doesExistInUpStation(lineId, stationId)).isEqualTo(expectedResult);
    }

    @DisplayName("상행선으로 등록되어 있는 역이면 true 아니면 false를 반환한다")
    @ParameterizedTest
    @CsvSource({"1,false", "2,true", "3,true", "4,true"})
    void isExistInDownStation(long stationId, boolean expectedResult) {
        assertThat(sectionRepository.doesExistInDownStation(lineId, stationId)).isEqualTo(expectedResult);
    }

    @DisplayName("역이 라인 내에 존재한다면 true 아니라면 false")
    @ParameterizedTest
    @CsvSource({"1,true", "2,true", "3,true", "4,true", "5,false"})
    void isStationExist(long stationId, boolean expectedResult) {
        assertThat(sectionRepository.doesStationExist(lineId, stationId)).isEqualTo(expectedResult);
    }

    @DisplayName("구간을 저장한다")
    @Test
    void save() {
        String stationQuery = "INSERT INTO station(name) VALUES(?)";
        jdbcTemplate.update(stationQuery, "해운대역");

        Section section = new Section(4L, lineId, new Station(3L), new Station(5L), 10);
        sectionRepository.save(section);

        Section sectionSaved = getSection(4L);
        assertThat(section).isEqualTo(sectionSaved);
    }

    @DisplayName("구간이 1개밖에 존재하지 않으면 삭제가 불가능하다")
    @Test
    void isUnableToDelete() {
        assertThat(sectionRepository.isUnableToDelete(lineId)).isFalse();

        String query = "DELETE FROM section WHERE line_id = ? AND (up_station_id = ? OR down_station_id = ?)";
        jdbcTemplate.update(query, lineId, 2L, 2L);

        assertThat(sectionRepository.isUnableToDelete(lineId)).isTrue();
    }

    @DisplayName("구간이 종점에 존재하는 역이면 true 아니라면 false")
    @ParameterizedTest
    @CsvSource({"1,true", "2,false", "3,true", "4,false"})
    void isEndStation(long stationId, boolean expectedResult) {
        assertThat(sectionRepository.isEndStation(lineId, stationId)).isEqualTo(expectedResult);
    }

    @DisplayName("해당역이 상행선으로 존재하는 구간과 하행선으로 존재하는 구간의 합을 반환한다")
    @Test
    void getNewSectionDistance() {
        assertThat(sectionRepository.getNewDistance(lineId, 2L)).isEqualTo(9);
    }

    @DisplayName("해당역이 하행역으로 존재하는 구간의 상행역 id를 반환한다")
    @Test
    void getNewUpStationId() {
        assertThat(sectionRepository.getNewUpStationId(lineId, 4L)).isEqualTo(2L);
    }

    @DisplayName("해당역이 상행역으로 존재하는 구간의 하행역 id를 반환한다")
    @Test
    void getNewDownStationId() {
        assertThat(sectionRepository.getNewDownStationId(lineId, 4L)).isEqualTo(3L);
    }

    @DisplayName("해당 라인에 존재하는 모든 상행역과 하행역 짝을 Map에 담아 반환한다")
    @Test
    void getAllUpAndDownStations() {
        Sections sections = sectionRepository.findAllSections(lineId);
        Stations expectedUpAndDownStations = new Stations(
                Arrays.asList(
                        new Station(1L, "잠실역"),
                        new Station(2L, "잠실새내역"),
                        new Station(4L, "한성백제역"),
                        new Station(3L, "몽촌토성역")
                )
        );

        assertThat(sections.getOrderedStations()).isEqualTo(expectedUpAndDownStations);
    }

    // 잠실역 (1) - 5 - 잠실새내역 (2) - 4 - 한성백제역 (4) - 7 - 몽촌토성역 (3)
    @DisplayName("주어진 구간의 상행선이 라인 노선 상 상행선으로 존재하면 해당 구간을 반환하고 반대의 경우에는 하행선으로 존재하는 구간을 반환한다")
    @Test
    void getExistingSectionByBaseStation() {
        Section sectionWithUpStationBase = new Section(lineId, new Station(4L), new Station(5L));
        Section expectedSection = new Section(3L, lineId, new Station(4L), new Station(3L), 7);
        assertThat(sectionRepository.findByBaseStation(sectionWithUpStationBase)).isEqualTo(expectedSection);

        Section sectionWithDownStationBase = new Section(lineId, new Station(5L), new Station(4L));
        expectedSection = new Section(1L, lineId, new Station(2L), new Station(4L), 4);
        assertThat(sectionRepository.findByBaseStation(sectionWithDownStationBase)).isEqualTo(expectedSection);
    }

    @DisplayName("주어진 구간 정보대로 해당 구간을 업데이트 한다")
    @Test
    void updateSection() {
        Section originalSection = new Section(1L, lineId, new Station(2L), new Station(4L), 4);
        assertThat(getSection(1L)).isEqualTo(originalSection);

        Section sectionForUpdate = new Section(1L, lineId, new Station(3L), new Station(1L), 12);
        sectionRepository.update(sectionForUpdate);

        Section section = getSection(1L);
        assertThat(section.getUpStation()).isEqualTo(sectionForUpdate.getUpStation());
        assertThat(section.getDownStationId()).isEqualTo(sectionForUpdate.getDownStationId());
        assertThat(section.getDistance()).isEqualTo(sectionForUpdate.getDistance());
    }

    private Section getSection(Long sectionId) {
        String query = "SELECT id, line_id, up_station_id, down_station_id, distance FROM section WHERE id = ?";
        return jdbcTemplate.queryForObject(
                query,
                (resultSet, rowNum) -> new Section(
                        resultSet.getLong("id"),
                        resultSet.getLong("line_id"),
                        new Station(resultSet.getLong("up_station_id")),
                        new Station(resultSet.getLong("down_station_id")),
                        resultSet.getInt("distance")
                ),
                sectionId
        );
    }
}
