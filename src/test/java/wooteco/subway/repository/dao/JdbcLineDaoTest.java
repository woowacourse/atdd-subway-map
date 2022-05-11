package wooteco.subway.repository.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import wooteco.subway.domain.Line;
import wooteco.subway.repository.entity.LineEntity;

@JdbcTest
class JdbcLineDaoTest {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;
    private LineDao lineDao;

    @BeforeEach
    void setUp() {
        lineDao = new JdbcLineDao(jdbcTemplate);
    }

    @DisplayName("노선을 저장하고 id로 노선을 찾는다.")
    @Test
    void saveAndFindById() {
        Line line = Line.createWithoutId("2호선", "bg-green-600", null);
        LineEntity savedLineEntity = lineDao.save(new LineEntity(line));

        LineEntity lineEntity = lineDao.findById(savedLineEntity.getId()).get();

        assertAll(
                () -> assertThat(lineEntity.getName()).isEqualTo("2호선"),
                () -> assertThat(lineEntity.getColor()).isEqualTo("bg-green-600")
        );
    }

    @DisplayName("없는 id로 노선을 조회할 시 빈 옵셔널을 반환한다.")
    @Test
    void findByNotExistId() {
        Line line = Line.createWithoutId("2호선", "bg-green-600", null);
        LineEntity savedLineEntity = lineDao.save(new LineEntity(line));

        lineDao.deleteById(savedLineEntity.getId());

        assertThat(lineDao.findById(savedLineEntity.getId()).isEmpty()).isTrue();
    }

    @DisplayName("모든 노선을 조회한다.")
    @Test
    void findAll() {
        Line line1 = Line.createWithoutId("2호선", "bg-green-600", null);
        lineDao.save(new LineEntity(line1));
        Line line2 = Line.createWithoutId("신분당선", "bg-red-600", null);
        lineDao.save(new LineEntity(line2));

        assertThat(lineDao.findAll().size()).isEqualTo(2);
    }

    @DisplayName("이름으로 노선을 찾는다.")
    @Test
    void findByName() {
        Line line = Line.createWithoutId("2호선", "bg-green-600", null);
        LineEntity savedLineEntity = lineDao.save(new LineEntity(line));

        LineEntity lineEntity = lineDao.findByName(savedLineEntity.getName()).get();

        assertAll(
                () -> assertThat(lineEntity.getId()).isEqualTo(savedLineEntity.getId()),
                () -> assertThat(lineEntity.getColor()).isEqualTo("bg-green-600")
        );
    }

    @DisplayName("없는 이름으로 노선을 조회할 시 빈 옵셔널을 반환한다.")
    @Test
    void findByNotExistName() {
        Line line = Line.createWithoutId("2호선", "bg-green-600", null);
        LineEntity savedLineEntity = lineDao.save(new LineEntity(line));

        lineDao.deleteById(savedLineEntity.getId());

        assertThat(lineDao.findByName(savedLineEntity.getName()).isEmpty()).isTrue();
    }

    @DisplayName("id 로 노선을 삭제한다.")
    @Test
    void deleteById() {
        Line line = Line.createWithoutId("2호선", "bg-green-600", null);
        LineEntity savedLineEntity = lineDao.save(new LineEntity(line));

        lineDao.deleteById(savedLineEntity.getId());

        assertThat(lineDao.findAll().size()).isEqualTo(0);
    }

    @DisplayName("노선을 수정한다.")
    @Test
    void update() {
        Line line = Line.createWithoutId("2호선", "bg-green-600", null);
        LineEntity savedLineEntity = lineDao.save(new LineEntity(line));
        LineEntity newLineEntity = new LineEntity(savedLineEntity.getId(), "신분당선", "bg-red-600");

        lineDao.update(newLineEntity);
        LineEntity updatedLineEntity = lineDao.findById(savedLineEntity.getId()).get();

        assertAll(
                () -> assertThat(updatedLineEntity.getName()).isEqualTo(newLineEntity.getName()),
                () -> assertThat(updatedLineEntity.getColor()).isEqualTo(newLineEntity.getColor())
        );
    }
}