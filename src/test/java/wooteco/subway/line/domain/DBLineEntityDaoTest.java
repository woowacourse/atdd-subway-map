package wooteco.subway.line.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.test.context.TestConstructor;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@JdbcTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class DBLineEntityDaoTest {
    private JdbcTemplate jdbcTemplate;
    private LineDao lineDao;
    private Long id;
    private String name;
    private String color;
    private LineEntity lineEntity;

    public DBLineEntityDaoTest(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        lineDao = new DBLineDao(jdbcTemplate);
    }

    @BeforeEach
    void setUp() {
        name = "백기선";
        color = "bg-red-600";
        lineEntity = new LineEntity(name, color);
        String sql = "INSERT INTO LINE(name, color) VALUES(?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, lineEntity.name());
            ps.setString(2, lineEntity.color());
            return ps;
        }, keyHolder);
        id = keyHolder.getKey().longValue();
    }

    @Test
    @DisplayName("노선을 저장한다.")
    void save() {
        String savedName = "흑기선";
        String savedColor = "bg-red-600";
        LineEntity saveLineEntity = lineDao.save(new LineEntity(savedName, savedColor));

        assertThat(saveLineEntity.name()).isEqualTo(savedName);
        assertThat(saveLineEntity.color()).isEqualTo(savedColor);
    }

    @Test
    @DisplayName("모든 노선을 찾는다.")
    void findAll() {
        List<LineEntity> lineEntities = lineDao.findAll();

        assertThat(lineEntities).hasSize(1);
    }

    @Test
    @DisplayName("id로 노선을 찾는다.")
    void findById() {
        LineEntity findLineEntity = lineDao.findById(id).get();

        assertThat(findLineEntity.id()).isEqualTo(id);
        assertThat(findLineEntity.name()).isEqualTo(name);
        assertThat(findLineEntity.color()).isEqualTo(color);
    }

    @Test
    @DisplayName("존재하지 않는 id로 노선을 찾는다.")
    void findByNoId() {
        Optional<LineEntity> findLine = lineDao.findById(0L);

        assertThat(findLine).isEmpty();
    }

    @Test
    @DisplayName("name으로 노선을 찾는다.")
    void findByName() {
        LineEntity lineEntity = lineDao.findByName(name).get();

        assertThat(lineEntity.id()).isEqualTo(id);
        assertThat(lineEntity.name()).isEqualTo(name);
        assertThat(lineEntity.color()).isEqualTo(color);
    }

    @Test
    @DisplayName("존재하지 않는 name로 노선을 찾는다.")
    void findByNoName() {
        Optional<LineEntity> findLine = lineDao.findByName("마찌역");

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
        lineDao.update(id, updatedName, updatedColor);

        LineEntity lineEntity = lineDao.findById(id).get();

        assertThat(lineEntity.id()).isEqualTo(id);
        assertThat(lineEntity.name()).isEqualTo(updatedName);
        assertThat(lineEntity.color()).isEqualTo(updatedColor);
    }

    @Test
    void delete() {
        assertThatThrownBy(() -> lineDao.delete(0L))
                .isInstanceOf(IllegalStateException.class);
    }
}