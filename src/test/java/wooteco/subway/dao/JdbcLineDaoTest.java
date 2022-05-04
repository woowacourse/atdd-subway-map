package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.dao.DuplicateKeyException;
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

}
