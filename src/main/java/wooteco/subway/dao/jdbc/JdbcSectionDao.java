package wooteco.subway.dao.jdbc;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.Section;

import java.sql.PreparedStatement;

@Repository
public class JdbcSectionDao implements SectionDao {

    private final JdbcTemplate jdbcTemplate;

    public JdbcSectionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Section create(Section section) {
        String sql = "INSERT INTO section (line_id,  up_station_id, down_station_id, distance) values(?,?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(sql, new String[]{"id"});
            statement.setLong(1, section.getLineId());
            statement.setLong(2, section.getUpStationId());
            statement.setLong(3, section.getDownStationId());
            statement.setInt(4, section.getDistance());
            return statement;
        }, keyHolder);
        return new Section(keyHolder.getKey().longValue(), section.getLineId(), section.getUpStationId(), section.getDownStationId(), section.getDistance());
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM section WHERE id=?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public boolean existById(Long id) {
        String sql = "SELECT exists (SELECT * FROM section WHERE id=?)";
        int count = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return count != 0;
    }
}
