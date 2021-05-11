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

    public List<Section> findAllByLineId(Long lineId) {
        String sql = "SELECT * FROM SECTION WHERE line_id = ?";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Long id = rs.getLong("id");
            Long upStationId = rs.getLong("up_station_id");
            Long downStationId = rs.getLong("down_station_id");
            Long distance = rs.getLong("distance");

            return new Section(id, lineId, upStationId, downStationId, distance);
        }, lineId);
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
                "WHERE id = ?";

        List<Object[]> argsOfSections = sections.stream().map(
                section -> {
                    Long upStationId = section.getUpStationId();
                    Long downStationId = section.getDownStationId();
                    Long distance = section.getDistance();
                    Long id = section.getId();

                    return new Object[]{upStationId, downStationId, distance, id};
                }
        ).collect(toList());

        jdbcTemplate.batchUpdate(sql, argsOfSections);
    }

    public void delete(List<Section> sections) {
        final String sql = "DELETE FROM SECTION WHERE id = ?";

        List<Object[]> argsOfSections = sections.stream().map(
                section -> {
                    Long id = section.getId();

                    return new Object[]{id};
                }
        ).collect(toList());

        jdbcTemplate.batchUpdate(sql, argsOfSections);
    }
}
