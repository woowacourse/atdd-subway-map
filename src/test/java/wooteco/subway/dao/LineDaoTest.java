package wooteco.subway.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.domain.Line;

import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@JdbcTest
public class LineDaoTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private LineDao lineDao;

    @BeforeEach
    void setUp() {
        lineDao = new LineDao(jdbcTemplate);
        jdbcTemplate.execute("DROP TABLE Line IF EXISTS");
        jdbcTemplate.execute(
                "create table Line(" +
                        "id bigint auto_increment not null," +
                        "name varchar(255) not null unique," +
                        "color varchar(20) not null," +
                        "primary key(id))"
        );

        lineDao.save(new Line("신분당선", "red"));
        lineDao.save(new Line("짱분당선", "blue"));
        lineDao.save(new Line("구분당선", "green"));
    }

    @Test
    @DisplayName("정상적으로 저장된 경우를 테스트한다.")
    void saveTest() {
        final Line newLine = lineDao.save(new Line("라쿤선", "black"));
        assertAll(
                () -> assertThat(newLine.getName()).isEqualTo("라쿤선"),
                () -> assertThat(newLine.getColor()).isEqualTo("black")
        );
    }

    @Test
    @DisplayName("정상적으로 전체 조회되는 경우를 테스트한다.")
    void findAllTest() {
        assertThat(lineDao.findAll()).hasSize(3);
    }

    @Test
    @DisplayName("정상적으로 특정 조회하는 경우를 테스트한다.")
    void findByIdTest() {
        final Line newLine = lineDao.save(new Line("라쿤선", "black"));
        final Line line = lineDao.findById(newLine.getId());
        assertAll(
                () -> assertThat(line.getName()).isEqualTo("라쿤선"),
                () -> assertThat(line.getColor()).isEqualTo("black")
        );
    }

    @Test
    @DisplayName("정상적으로 수정되는 경우를 테스트한다.")
    void updateTest() {
        final Line newLine = lineDao.save(new Line("짱구선", "white"));
        lineDao.update(newLine.getId(), new Line("38선", "rainbow"));
        Line line = lineDao.findById(newLine.getId());
        assertAll(
                () -> assertThat(line.getName()).isEqualTo("38선"),
                () -> assertThat(line.getColor()).isEqualTo("rainbow")
        );
    }

    @Test
    @DisplayName("정상적으로 제거되는 경우를 테스트한다.")
    void deleteTest() {
        final Line newLine = lineDao.save(new Line("짱구선", "white"));
        lineDao.deleteById(newLine.getId());
        assertThatThrownBy(() -> {
            lineDao.findById(newLine.getId());
        }).isInstanceOf(NoSuchElementException.class);
    }

}
