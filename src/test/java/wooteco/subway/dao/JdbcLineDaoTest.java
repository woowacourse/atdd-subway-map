package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.domain.Line;

@JdbcTest
public class JdbcLineDaoTest {

    private final LineDao lineDao;

    @Autowired
    public JdbcLineDaoTest(JdbcTemplate jdbcTemplate) {
        this.lineDao = new JdbcLineDao(jdbcTemplate);
    }

    @DisplayName("노선을 저장한다")
    @Test
    void 노선_저장() {
        Line line = new Line("2호선", "bg-green-600");

        Line savedLine = lineDao.save(line);

        assertAll(
                () -> assertThat(savedLine.getName()).isEqualTo(line.getName()),
                () -> assertThat(savedLine.getColor()).isEqualTo(line.getColor())
        );
    }

    @DisplayName("중복된 노선을 저장할 경우 예외가 발생한다.")
    @Test
    void 중복된_노선_예외발생() {
        Line line = new Line("2호선", "bg-green-600");

        lineDao.save(line);

        assertThatThrownBy(() -> lineDao.save(line))
                .isInstanceOf(DuplicateKeyException.class);
    }

    @DisplayName("존재하지 않는 노선을 조회할 경우 예외가 발생한다.")
    @Test
    void 노선_조회_예외발생() {
        assertThatThrownBy(() -> lineDao.findById(0L))
                .isInstanceOf(EmptyResultDataAccessException.class);
    }

    @DisplayName("노선을 수정한다.")
    @Test
    void 노선_수정() {
        Line line = new Line("2호선", "bg-green-600");
        Line savedLine = lineDao.save(line);

        lineDao.update(savedLine.getId(), new Line("2호선", "bg-yellow-600"));
        Line updatedLine = lineDao.findById(savedLine.getId());

        assertAll(
                () -> assertThat(updatedLine.getName()).isEqualTo("2호선"),
                () -> assertThat(updatedLine.getColor()).isEqualTo("bg-yellow-600")
        );
    }

    @DisplayName("모든 노선을 조회한다.")
    @Test
    void 모든_노선_조회() {
        lineDao.save(new Line("2호선", "bg-green-600"));
        lineDao.save(new Line("1호선", "bg-darkblue-600"));
        lineDao.save(new Line("3호선", "bg-orange-600"));

        assertThat(lineDao.findAll().size()).isEqualTo(3);
    }

    @DisplayName("노선을 삭제한다.")
    @Test
    void 노선_삭제() {
        Line line = lineDao.save(new Line("4호선", "bg-skyblue-600"));

        lineDao.deleteById(line.getId());

        assertThat(lineDao.findAll().size()).isEqualTo(0);
    }
}
