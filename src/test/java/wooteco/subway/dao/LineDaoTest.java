package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.domain.Line;

@JdbcTest
class LineDaoTest {

    private final LineDao lineDao;

    @Autowired
    private LineDaoTest(JdbcTemplate jdbcTemplate) {
        this.lineDao = new LineDao(jdbcTemplate);
    }

    @DisplayName("중복되는 노선 이름이 없을 때 성공적으로 저장되는지 테스트")
    @Test
    void save_success() {
        Line line = lineDao.save(new Line("testName", "black"));
        assertThat(lineDao.findAll().size()).isEqualTo(1);
    }

    @DisplayName("중복되는 노선 이름이 있을 때 예외 반환 테스트")
    @Test
    void save_fail() {
        Line line = lineDao.save(new Line("testName", "black"));
        assertThatThrownBy(() -> lineDao.save(new Line("testName", "white")))
                .isInstanceOf(DuplicateKeyException.class);
    }

    @DisplayName("존재하는 노선 id가 있으면 삭제되는지 테스트")
    @Test
    void deleteById_exist() {
        Line line = lineDao.save(new Line("testName", "black"));
        Long deleteId = lineDao.deleteById(line.getId());
        assertThatThrownBy(() -> lineDao.findById(deleteId))
                .isInstanceOf(EmptyResultDataAccessException.class);
    }

    @DisplayName("존재하는 노선 id가 없으면 삭제되지 않는지 테스트")
    @Test
    void deleteById_not_exist() {
        Line line = lineDao.save(new Line("testName", "black"));
        lineDao.deleteById(-1L);
        assertThat(lineDao.findAll().isEmpty()).isFalse();
    }

    @DisplayName("존재하는 노선 id가 있으면 결과값이 존재하는지 테스트")
    @Test
    void findById_exist() {
        Line line = lineDao.save(new Line("testName", "black"));
        Line result = lineDao.findById(line.getId());
        assertThat(result).isNotNull();
    }

    @DisplayName("존재하는 노선 id가 없으면 예외가 발생하는지 테스트")
    @Test
    void findById_not_exist() {
        Line line = lineDao.save(new Line("testName", "black"));
        assertThatThrownBy(() -> lineDao.findById(-1L))
                .isInstanceOf(EmptyResultDataAccessException.class);
    }

    @DisplayName("바뀐 이름이 중복될 때 예외가 발생하는지 테스트")
    @Test
    void changeLineName_duplicate() {
        Line line = lineDao.save(new Line("testName", "black"));
        Line line2 = lineDao.save(new Line("testName2", "black"));
        assertThatThrownBy(() -> lineDao.changeLineName(line.getId(), "testName2"))
                .isInstanceOf(DuplicateKeyException.class);
    }

    @DisplayName("id가 있고 바뀐 이름이 중복되지 않을 때 예외가 발생하지 않는지 테스트")
    @Test
    void changeLineName_success() {
        Line line = lineDao.save(new Line("testName", "black"));
        Line line2 = lineDao.save(new Line("testName2", "black"));

        lineDao.changeLineName(line.getId(), "testName3");

        assertThat(lineDao.findById(line.getId()).getName()).isEqualTo("testName3");
    }

    @DisplayName("원래 자신의 이름으로 바꿨을 때 예외가 발생하지 않는지 테스트")
    @Test
    void changeLineName_self_loop() {
        Line line = lineDao.save(new Line("testName", "black"));
        Line line2 = lineDao.save(new Line("testName2", "black"));
        lineDao.changeLineName(line.getId(), "testName");
        assertThat(line.getName()).isEqualTo("testName");
    }
}
