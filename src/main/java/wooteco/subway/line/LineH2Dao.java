package wooteco.subway.line;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class LineH2Dao implements LineRepository {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public LineH2Dao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Line save(Line line) {
        String query= "INSERT INTO LINE (name, color) values (?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        this.jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(query, new String[]{"id"});
            ps.setString(1, line.getName());
            ps.setString(2, line.getColor());
            return ps;
        }, keyHolder);

        return new Line(Objects.requireNonNull(keyHolder.getKey()).longValue(), line.getName(), line.getColor());
    }

    @Override
    public List<Line> findAll() {
        String query = "SELECT * FROM LINE";
        return jdbcTemplate.query(query, (rs, rn) -> {
            long id = rs.getLong("id");
            String name = rs.getString("name");
            String color = rs.getString("color");
            return new Line(id, name, color);
        });
    }

    @Override
    public Line findById(Long id) {
        return null;
    }

    @Override
    public Optional<Line> findByName(String name) {
        return Optional.empty();
    }

    @Override
    public Line update(Long id, Line newLine) {
        return null;
    }

    @Override
    public void delete(Long id) {

    }
}
