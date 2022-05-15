package wooteco.subway.dao;

import java.util.List;
import java.util.Optional;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import wooteco.subway.dto.StationResponse;

@Repository
public class StationDao {

    public static final RowMapper<StationResponse> ROW_MAPPER = (rs, rn) -> {
        long id = rs.getLong("id");
        String name = rs.getString("name");
        return new StationResponse(id, name);
    };

    private final JdbcTemplate jdbcTemplate;

    public StationDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<StationResponse> queryById(long id) {
        try {
            StationResponse response = jdbcTemplate
                .queryForObject("SELECT id, name FROM STATION WHERE id = ?", ROW_MAPPER, id);
            return Optional.of(response);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public List<StationResponse> queryAll() {
        return jdbcTemplate.query("SELECT id, name FROM STATION", ROW_MAPPER);
    }
}
