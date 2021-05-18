package wooteco.subway.line.infra;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.exception.DuplicatedNameException;
import wooteco.subway.exception.LineNotFoundException;
import wooteco.subway.exception.NoRowAffectedException;
import wooteco.subway.line.domain.Line;
import wooteco.subway.line.domain.repository.LineRepository;
import wooteco.subway.line.domain.section.Distance;
import wooteco.subway.line.domain.section.Section;
import wooteco.subway.line.domain.section.Sections;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class JdbcLineRepository implements LineRepository {

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Line> lineRowMapper = (resultSet, rowNum) ->
            new Line(
                    resultSet.getLong("id"),
                    resultSet.getString("name"),
                    resultSet.getString("color")
            );

    public JdbcLineRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Line save(final Line line) {
        try {
            String query = "INSERT INTO line(name, color) VALUES(?, ?)";
            KeyHolder keyHolder = new GeneratedKeyHolder();
            this.jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(query, new String[]{"id"});
                ps.setString(1, line.getName());
                ps.setString(2, line.getColor());
                return ps;
            }, keyHolder);
            return findById(keyHolder.getKey().longValue()).orElseThrow(LineNotFoundException::new);
        } catch (DuplicateKeyException e) {
            throw new DuplicatedNameException("이미 존재하는 지하철 노선 이름입니다.", e.getCause());
        }
    }

    @Override
    public Optional<Line> findById(final Long id) {
        try {
            String query = "SELECT * FROM line WHERE id = ?";
            return Optional.ofNullable(this.jdbcTemplate.queryForObject(query, lineRowMapper, id));
        } catch (DataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Line> findLineSectionById(Long id) {
        try {
            String query = "SELECT * FROM line AS l JOIN section AS s ON l.id = s.line_id WHERE l.id = ?";
            return Optional.ofNullable(this.jdbcTemplate.queryForObject(query, lineSectionMapper(), id));
        } catch (DataAccessException e) {
            return Optional.empty();
        }
    }

    private RowMapper<Line> lineSectionMapper() {
        return (resultSet, rowNum) -> {
            Long id = resultSet.getLong("id");
            String name = resultSet.getString("name");
            String color = resultSet.getString("color");
            List<Section> sectionList = new ArrayList<>();
            do {
                sectionList.add(new Section(
                        resultSet.getLong("id"),
                        id,
                        resultSet.getLong("up_station_id"),
                        resultSet.getLong("down_station_id"),
                        new Distance(resultSet.getInt("distance"))
                ));
            } while (resultSet.next());
            return new Line(id, name, color, new Sections(sectionList));
        };
    }

    @Override
    public List<Line> findAll() {
        String query = "SELECT * FROM line";
        return this.jdbcTemplate.query(query, lineRowMapper);
    }

    @Override
    public Optional<Line> findByName(final String name) {
        String query = "SELECT * FROM line WHERE name = ?";
        return Optional.ofNullable(jdbcTemplate.queryForObject(query, lineRowMapper, name));
    }

    @Override
    public void update(final Line line) {
        try {
            String query = "UPDATE line SET name = ?, color = ? WHERE id = ?";
            int updateRows = this.jdbcTemplate.update(query, line.getName(), line.getColor(), line.getId());
            if (updateRows < 1) {
                throw new NoRowAffectedException("수정된 지하철 노선이 없습니다.");
            }
        } catch (DuplicateKeyException e) {
            throw new DuplicatedNameException("이미 존재하는 지하철 노선 이름입니다.", e.getCause());
        }
    }

    @Override
    public void delete(final Long id) {
        String query = "DELETE FROM line WHERE id = ?";
        int affectedRows = this.jdbcTemplate.update(query, id);
        if (affectedRows < 1) {
            throw new NoRowAffectedException("삭제된 지하철 노선이 없습니다.");
        }
    }

    @Override
    public void deleteAll() {
        String query = "DELETE FROM line";
        this.jdbcTemplate.update(query);
    }
}
