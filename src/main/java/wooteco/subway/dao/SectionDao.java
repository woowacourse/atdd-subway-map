package wooteco.subway.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import javax.sql.DataSource;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Section;

@Repository
public class SectionDao {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert simpleJdbcInsert;

    public SectionDao(JdbcTemplate jdbcTemplate, DataSource dataSource) {
        this.jdbcTemplate = jdbcTemplate;
        this.simpleJdbcInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("section")
                .usingGeneratedKeyColumns("id");
    }

    private final RowMapper<Section> stationRowMapper = (resultSet, rowNum) -> {
        Section section = new Section(
                resultSet.getLong("id"),
                resultSet.getLong("line_id"),
                resultSet.getLong("up_station_id"),
                resultSet.getLong("down_station_id"),
                resultSet.getInt("distance")
        );
        return section;
    };

    public Section save(Section section) {
        SqlParameterSource parameterSource = new BeanPropertySqlParameterSource(section);
        long id = simpleJdbcInsert.executeAndReturnKey(parameterSource).longValue();
        return new Section(id, section.getLineId(), section.getUpStationId(), section.getDownStationId(),
                section.getDistance());
    }

    public List<Section> findByLineId(long lineId) {
        String sql = "select * from SECTION where line_id = (?)";
        return jdbcTemplate.query(sql, stationRowMapper, lineId);
    }

    public void update(List<Section> value) {
        final String sql = "update SECTION set up_station_id = (?), down_station_id = (?), distance = (?) "
                + "where id = (?)";
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Section section = value.get(i);
                ps.setLong(1, section.getUpStationId());
                ps.setLong(2, section.getDownStationId());
                ps.setInt(3, section.getDistance());
                ps.setLong(4, section.getId());
            }

            @Override
            public int getBatchSize() {
                return value.size();
            }
        });
    }

    public void deleteById(Long id) {
        final String sql = "delete from SECTION where id = (?)";
        jdbcTemplate.update(sql, id);
    }
}
