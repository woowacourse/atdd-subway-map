package wooteco.subway.station;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.NoSuchElementException;

@Repository
public class H2StationDao implements StationDao {
    private final JdbcTemplate jdbcTemplate;

    private static final RowMapper<Station> STATION_ROW_MAPPER = (rs, rowNum) -> {
        long id = rs.getLong("id");
        String name = rs.getString("name");
        return new Station(id, name);
    };

    public H2StationDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Station save(Station station) {
        String sql = "INSERT INTO STATION (NAME) VALUES (?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, station.getName().toString());
            return ps;
        }, keyHolder);
        long stationId = keyHolder.getKey().longValue();
        return new Station(stationId, station.getName());
    }

    @Override
    public List<Station> findAll() {
        String sql = "SELECT * " +
                "FROM STATION";

        return jdbcTemplate.query(sql,
                STATION_ROW_MAPPER
        );
    }

    @Override
    public Station findById(Long id) {
        String sql = "SELECT * " +
                "FROM STATION " +
                "WHERE ID = ?";

        List<Station> queryResult = jdbcTemplate.query(sql,
                STATION_ROW_MAPPER,
                id
        );

        if (queryResult.isEmpty()) {
            throw new NoSuchElementException(String.format("데이터베이스에 해당 ID의 역이 없습니다. ID : %d", id));
        }

        return queryResult.get(0);
    }

    @Override
    public boolean checkExistName(StationName name) {
        String sql = "SELECT COUNT(*) " +
                "FROM STATION " +
                "WHERE NAME = ?";

        int countOfName = jdbcTemplate.queryForObject(sql, Integer.class, name.toString());
        return countOfName > 0;
    }

    @Override
    public boolean checkExistId(Long id) {
        String sql = "SELECT COUNT(*) " +
                "FROM STATION " +
                "WHERE ID = ?";

        int countOfColor = jdbcTemplate.queryForObject(sql, Integer.class, id.toString());
        return countOfColor > 0;
    }

    @Override
    public void delete(Station station) {
        String sql = "DELETE " +
                "FROM STATION " +
                "WHERE ID = ?";

        jdbcTemplate.update(sql, station.getId());
    }

    @Override
    public void deleteAll() {
        String sql = "DELETE " +
                "FROM STATION";

        jdbcTemplate.update(sql);
    }
}
