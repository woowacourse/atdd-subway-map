package wooteco.subway.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.domain.Line;

import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@JdbcTest
@Sql("classpath:line.sql")
public class LineDaoTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private LineDao lineDao;

    @BeforeEach
    void setUp() {
        lineDao = new LineDao(jdbcTemplate);

        lineDao.save(new Line("신분당선", "bg-red-600"));
        lineDao.save(new Line("짱분당선", "bg-blue-600"));
        lineDao.save(new Line("구분당선", "bg-green-600"));
    }

    @Test
    @DisplayName("정상적으로 저장된 경우를 테스트한다.")
    void saveTest() {
        final Line newLine = lineDao.save(new Line("라쿤선", "bg-black-600"));
        assertAll(
                () -> assertThat(newLine.getName()).isEqualTo("라쿤선"),
                () -> assertThat(newLine.getColor()).isEqualTo("bg-black-600")
        );
    }

    @Test
    @DisplayName("중복된 이름을 저장한 경우 예외를 발생시킨다")
    void saveDuplicateTest() {
        assertThatThrownBy(() -> {
            lineDao.save(new Line("신분당선", "bg-red-600"));
        }).isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("정상적으로 전체 조회되는 경우를 테스트한다.")
    void findAllTest() {
        assertThat(lineDao.findAll()).hasSize(3);
    }

    @Test
    @DisplayName("존재하지 않는 id를 조회하는 경우 예외를 발생시킨다.")
    void findExceptionTest() {
        assertThatThrownBy(() -> lineDao.findById(9999999L))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    @DisplayName("정상적으로 특정 조회하는 경우를 테스트한다.")
    void findByIdTest() {
        final Line newLine = lineDao.save(new Line("라쿤선", "bg-black-600"));
        final Line line = lineDao.findById(newLine.getId());
        assertAll(
                () -> assertThat(line.getName()).isEqualTo("라쿤선"),
                () -> assertThat(line.getColor()).isEqualTo("bg-black-600")
        );
    }

    @Test
    @DisplayName("정상적으로 수정되는 경우를 테스트한다.")
    void updateTest() {
        final Line newLine = lineDao.save(new Line("짱구선", "bg-white-600"));
        lineDao.update(newLine.getId(), new Line("38선", "bg-rainbow-600"));
        Line line = lineDao.findById(newLine.getId());
        assertAll(
                () -> assertThat(line.getName()).isEqualTo("38선"),
                () -> assertThat(line.getColor()).isEqualTo("bg-rainbow-600")
        );
    }

    @Test
    @DisplayName("정상적으로 제거되는 경우를 테스트한다.")
    void deleteTest() {
        final Line newLine = lineDao.save(new Line("짱구선", "bg-white-600"));
        lineDao.deleteById(newLine.getId());
        assertThatThrownBy(() -> {
            lineDao.findById(newLine.getId());
        }).isInstanceOf(NoSuchElementException.class);
    }
}
