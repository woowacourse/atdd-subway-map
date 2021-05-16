package wooteco.subway.line.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.test.context.TestConstructor;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@JdbcTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class JDBCLineDaoTest {
    private final JdbcTemplate jdbcTemplate;
    private final LineDao lineDao;
    private Long id;
    private String name;
    private String color;
    private Line line;

    public JDBCLineDaoTest(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        lineDao = new JDBCLineDao(jdbcTemplate);
    }

    @BeforeEach
    void setUp() {
        name = "백기선";
        color = "bg-red-600";
        line = new Line(name, color);
        String sql = "INSERT INTO LINE(name, color) VALUES(?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, line.name());
            ps.setString(2, line.color());
            return ps;
        }, keyHolder);
        id = keyHolder.getKey().longValue();
    }

    @Test
    @DisplayName("노선을 저장한다.")
    void save() {
        String savedName = "흑기선";
        String savedColor = "bg-black-601";
        Line saveLine = lineDao.save(new Line(savedName, savedColor));

        assertThat(saveLine.name()).isEqualTo(savedName);
        assertThat(saveLine.color()).isEqualTo(savedColor);
    }

    @Test
    @DisplayName("중복되는 노선이름을 저장할 시 예외가 발생한다.")
    void saveDuplicateNameException() {
        String savedName = "백기선";
        String savedColor = "bg-red-600";

        assertThatThrownBy(() -> lineDao.save(new Line(savedName, savedColor)))
                .isInstanceOf(DuplicateKeyException.class);
    }

    @Test
    @DisplayName("중복되는 노선 색깔을 저장할 시 예외가 발생한다.")
    void saveDuplicateColorException() {
        String savedName = "흑기선";
        String savedColor = "bg-red-600";

        assertThatThrownBy(() -> lineDao.save(new Line(savedName, savedColor)))
                .isInstanceOf(DuplicateKeyException.class);
    }

    @Test
    @DisplayName("모든 노선을 찾는다.")
    void findAll() {
        List<Line> lines = lineDao.findAll();

        assertThat(lines).hasSize(1);
    }

    @Test
    @DisplayName("id로 노선을 찾는다.")
    void findById() {
        Line findLine = lineDao.findById(id).get();

        assertThat(findLine.id()).isEqualTo(id);
        assertThat(findLine.name()).isEqualTo(name);
        assertThat(findLine.color()).isEqualTo(color);
    }

    @Test
    @DisplayName("존재하지 않는 id로 노선을 찾는다.")
    void findByNoId() {
        Optional<Line> findLine = lineDao.findById(0L);

        assertThat(findLine).isEmpty();
    }

    @Test
    @DisplayName("name으로 노선을 찾는다.")
    void findByName() {
        Line line = lineDao.findByName(name).get();

        assertThat(line.id()).isEqualTo(id);
        assertThat(line.name()).isEqualTo(name);
        assertThat(line.color()).isEqualTo(color);
    }

    @Test
    @DisplayName("존재하지 않는 name로 노선을 찾는다.")
    void findByNoName() {
        Optional<Line> findLine = lineDao.findByName("마찌역");

        assertThat(findLine).isEmpty();
    }

    @Test
    @DisplayName("DB를 밀면 예외가 발생한다.")
    void clear() {
        assertThatThrownBy(() -> lineDao.clear())
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    @DisplayName("노선을 수정한다.")
    void update() {
        String updatedName = "흑기선";
        String updatedColor = "bg-red-700";

        Line updateLine = new Line(id, updatedName, updatedColor);

        lineDao.update(updateLine);

        Line line = lineDao.findById(id).get();

        assertThat(line.id()).isEqualTo(id);
        assertThat(line.name()).isEqualTo(updatedName);
        assertThat(line.color()).isEqualTo(updatedColor);
    }

    @Test
    void delete() {
        assertThatCode(() -> lineDao.delete(0L))
                .doesNotThrowAnyException();
    }
}
