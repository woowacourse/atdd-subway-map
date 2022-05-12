package wooteco.subway.dao.jdbc;

import static java.util.stream.Collectors.toList;

import java.sql.PreparedStatement;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.Section;

@Repository
public class JdbcSectionDao implements SectionDao {

    private static final RowMapper<Section> SECTION_ROW_MAPPER = (resultSet, rowNum) -> new Section(
            resultSet.getLong("id"),
            resultSet.getLong("line_id"),
            resultSet.getLong("up_station_id"),
            resultSet.getLong("down_station_id"),
            resultSet.getInt("distance")
    );

    private final JdbcTemplate jdbcTemplate;

    public JdbcSectionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Section save(Section section) {
        String sql = "INSERT INTO `section` (line_id, up_station_id, down_station_id, distance) VALUES(?, ?, ?, ?)";

        final KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            final PreparedStatement statement = connection.prepareStatement(sql, new String[]{"id"});
            statement.setLong(1, section.getLineId());
            statement.setLong(2, section.getUpStationId());
            statement.setLong(3, section.getDownStationId());
            statement.setInt(4, section.getDistance());
            return statement;
        }, keyHolder);

        final long id = keyHolder.getKey().longValue();

        return new Section(id, section.getLineId(), section.getUpStationId(), section.getDownStationId(), section.getDistance());
    }

    @Override
    public void saveAll(List<Section> sections) {
        final String sql = "INSERT INTO `section` (line_id, up_station_id, down_station_id, distance) VALUES (?, ?, ?, ?)";
        final List<Object[]> parameters = generateParameters(sections);
        jdbcTemplate.batchUpdate(sql, parameters);
    }

    @Override
    public List<Section> findByLineId(Long id) {
        String sql = "SELECT * FROM `section` WHERE line_id = ?";

        return jdbcTemplate.query(sql, SECTION_ROW_MAPPER, id);
    }

    @Override
    public List<Section> findAll() {
        final String sql = "SELECT * FROM `section`";

        return jdbcTemplate.query(sql, SECTION_ROW_MAPPER);
    }


    @Override
    public int deleteById(Long id) {
        String sql = "DELETE FROM `section` WHERE id = ?";

        return jdbcTemplate.update(sql, id);
    }

    @Override
    public int deleteByLineId(Long lineId) {
        String sql = "DELETE FROM `section` WHERE line_id = ?";

        return jdbcTemplate.update(sql, lineId);
    }

    private List<Object[]> generateParameters(List<Section> sections) {
        return sections.stream()
                .map(section -> generateParameter(section))
                .collect(toList());
    }

    private Object[] generateParameter(Section section) {
        return new Object[]{
                section.getLineId(),
                section.getUpStationId(),
                section.getDownStationId(),
                section.getDistance()
        };
    }
}
