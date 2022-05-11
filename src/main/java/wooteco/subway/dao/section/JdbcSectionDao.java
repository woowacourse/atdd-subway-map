package wooteco.subway.dao.section;

import java.sql.PreparedStatement;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Section;

@Repository
public class JdbcSectionDao implements SectionDao {

    private final RowMapper<Section> sectionRowMapper = (resultSet, rowNum) -> {
        return new Section(
                resultSet.getLong("id"),
                resultSet.getLong("line_id"),
                resultSet.getLong("up_station_id"),
                resultSet.getLong("down_station_id"),
                resultSet.getInt("distance")
        );
    };

    private final JdbcTemplate jdbcTemplate;

    public JdbcSectionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public long save(Section section) {
        final String sql = "insert into SECTION (line_id, up_station_id, down_station_id, distance) values (?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(sql, new String[]{"id"});
            preparedStatement.setLong(1, section.getLineId());
            preparedStatement.setLong(2, section.getUpStationId());
            preparedStatement.setLong(3, section.getDownStationId());
            preparedStatement.setInt(4, section.getDistance());
            return preparedStatement;
        }, keyHolder);

        return keyHolder.getKey().longValue();
    }

    @Override
    public List<Section> findAllByLineId(Long lineId) {
        final String sql = "select id, line_id, up_station_id, down_station_id, distance from SECTION where line_id = ?";
        return jdbcTemplate.query(sql, sectionRowMapper, lineId);
    }

    @Override
    public List<Section> findAll() {
        final String sql = "select id, line_id, up_station_id, down_station_id, distance from SECTION";
        return jdbcTemplate.query(sql, sectionRowMapper);
    }

    @Override
    public void update(Section section) {
        final String sql = "update SECTION set line_id = ?, up_station_id = ?, down_station_id = ?, "
                + "distance = ? where id = ?";
        jdbcTemplate.update(sql, section.getLineId(), section.getUpStationId(), section.getDownStationId(),
                section.getDistance(), section.getId());
    }

    @Override
    public void delete(Long id) {
        final String sql = "delete from SECTION where id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public boolean existSectionById(Long id) {
        final String sql = "select exists (select * from SECTION where id = ?)";
        return jdbcTemplate.queryForObject(sql, Boolean.class, id);
    }
}
