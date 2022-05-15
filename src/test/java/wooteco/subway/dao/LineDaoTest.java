package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static wooteco.subway.dao.DaoFixtures.분당선;
import static wooteco.subway.dao.DaoFixtures.호선2;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.domain.Line;

@JdbcTest
class LineDaoTest {

    private final LineDao lineDao;

    @Autowired
    LineDaoTest(JdbcTemplate jdbcTemplate) {
        this.lineDao = new LineDao(jdbcTemplate);
    }

    @Test
    @DisplayName("노선을 저장하면 auto increment 된 노선을 반환한다.")
    void save() {
        Line savedLine = lineDao.save(분당선);
        assertAll(
                () -> assertThat(savedLine.getId()).isInstanceOf(Long.class),
                () -> assertThat(savedLine.getName()).isEqualTo("분당선"),
                () -> assertThat(savedLine.getColor()).isEqualTo("노랑")
        );
    }

    @Test
    @DisplayName("이름에 따라 노선이 존재하는지 확인한다.")
    void existsByName() {
        lineDao.save(분당선);
        assertAll(
                () -> assertThat(lineDao.existsByName("분당선")).isTrue(),
                () -> assertThat(lineDao.existsByName("2호선")).isFalse()
        );
    }

    @Test
    @DisplayName("저장된 모든 노선을 반환한다.")
    void findAll() {
        lineDao.save(분당선);
        lineDao.save(호선2);
        List<Line> lines = lineDao.findAll();
        assertAll(
                () -> assertThat(lines.size()).isEqualTo(2),
                () -> {
                    Line firstLine = lines.get(0);
                    assertThat(firstLine.getName()).isEqualTo("분당선");
                    assertThat(firstLine.getColor()).isEqualTo("노랑");
                }
        );
    }

    @Test
    @DisplayName("Id에 따라 역이 존재하는지 확인한다.")
    void notExistsById() {
        Line line = lineDao.save(분당선);
        assertAll(
                () -> assertThat(lineDao.notExistsById(line.getId())).isFalse(),
                () -> assertThat(lineDao.notExistsById(line.getId() + 100)).isTrue()
        );
    }

    @Test
    @DisplayName("Id에 따라 노선을 반환한다.")
    void findById() {
        Line savedLine = lineDao.save(분당선);
        Line line = lineDao.findById(savedLine.getId());
        assertAll(
                () -> assertThat(line.getName()).isEqualTo("분당선"),
                () -> assertThat(line.getColor()).isEqualTo("노랑")
        );
    }

    @Test
    @DisplayName("Id에 따라 노선을 수정한다.")
    void updateLineById() {
        Line line = lineDao.save(분당선);
        int affectedQuery = lineDao.updateLineById(line.getId(), "분당선", "yellow");
        assertThat(affectedQuery).isEqualTo(1);
    }

    @Test
    @DisplayName("Id에 따라 노선을 삭제한다.")
    void deleteById() {
        Line line = lineDao.save(분당선);
        int affectedQuery = lineDao.deleteById(line.getId());
        assertThat(affectedQuery).isEqualTo(1);
    }
}
