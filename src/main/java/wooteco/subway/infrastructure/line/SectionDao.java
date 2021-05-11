package wooteco.subway.infrastructure.line;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.line.section.Section;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Repository
public class SectionDao {

    private final JdbcTemplate jdbcTemplate;

    public SectionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Section> save(List<Section> sections) {
        String sql = "INSERT INTO SECTION (line_id, up_station_id, down_station_id, distance) VALUES (?, ?, ? ,?)";

        List<Object[]> argsOfSections = sections.stream().map(
                section -> {
                    Long lineId = section.getLineId();
                    Long upStationId = section.getUpStationId();
                    Long downStationId = section.getDownStationId();
                    Long distance = section.getDistance();

                    return new Object[]{lineId, upStationId, downStationId, distance};
                }
        ).collect(toList());

        jdbcTemplate.batchUpdate(sql, argsOfSections);

        return sections;
    }

    public Section save(Section section) {
        String sql = "INSERT INTO SECTION (line_id, up_station_id, down_station_id, distance) VALUES (?, ?, ?, ?)";

        jdbcTemplate.update(sql, section.getLineId(), section.getUpStationId(), section.getDownStationId(), section.getDistance());

        return section;
    }

    public Section findById(Long id) {
        String sql = "SELECT * FROM SECTION WHERE id = ?";

        return jdbcTemplate.queryForObject(sql, createSection(), id);
    }

    static class Test {
        private final Long line_id;
        private final Long up_station_id;
        private final Long down_station_id;
        private final Long distance;

        public Test(Long line_id, Long up_station_id, Long down_station_id, Long distance) {
            this.line_id = line_id;
            this.up_station_id = up_station_id;
            this.down_station_id = down_station_id;
            this.distance = distance;
        }

    }

    public List<Section> findAllByLineId(Long lineId) {
        String sql = "SELECT * FROM SECTION WHERE line_id = ?";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Long id = rs.getLong("id");
            Long upStationId = rs.getLong("up_station_id");
            Long downStationId = rs.getLong("down_station_id");
            Long distance = rs.getLong("distance");

            return new Section(id, lineId, upStationId, downStationId, distance);
        });
    }

    public List<Section> findAll() {
        String sql = "SELECT * FROM SECTION";

        return jdbcTemplate.query(sql, createSection());
    }

    private RowMapper<Section> createSection() {
        return (rs, rowNum) -> {
            Long lineId = rs.getLong("line_id");
            Long upStationId = rs.getLong("up_station_id");
            Long downStationId = rs.getLong("down_station_id");
            Long distance = rs.getLong("distance");

            return new Section(lineId, upStationId, downStationId, distance);
        };
    }

    public void update(List<Section> sections) {
        final String sql = "UPDATE SECTION SET " +
                "up_station_id = ?, " +
                "down_station_id = ?, " +
                "distance = ? " +
                "WHERE id";
    }
}
