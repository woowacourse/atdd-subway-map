package wooteco.subway.line;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@JdbcTest
@Rollback
class LineDaoTest {
    LineDao lineDao;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        lineDao = new LineDao(jdbcTemplate);
    }

    @Test
    @DisplayName("노선 추가")
    void insert() {
        // given
        Line line = lineDao.insert("bg-red-100", "코다선");

        assertThat(line.getColor()).isEqualTo("bg-red-100");
        assertThat(line.getName()).isEqualTo("코다선");
    }

    @Test
    @DisplayName("노선 목록 조회")
    void findAll() {
        //given
        lineDao.insert("bg-red-100", "거북선");
        lineDao.insert("bg-black-200", "원양어선");

        //when
        List<Line> lines = lineDao.findAll();

        //then
        assertThat(lines).hasSize(2);
    }

    @Test
    @DisplayName("id를 통한 노선 조회")
    void findById() {
        //given
        Line line = lineDao.insert("bg-red-100", "거북선");

        //when
        Line expectedLine = lineDao.findById(line.getId())
                .get();

        //then
        assertThat(expectedLine.getName()).isEqualTo("거북선");
        assertThat(expectedLine.getColor()).isEqualTo("bg-red-100");
    }

    @Test
    @DisplayName("노선 수정 테스트")
    void update() {
        //given
        Line line = lineDao.insert("bg-red-100", "거북선");

        //when
        lineDao.update(line.getId(), "bg-yellow-100", "크로플선");

        //then
        Line expectedLine = lineDao.findById(line.getId())
                .get();
        assertThat(expectedLine.getColor()).isEqualTo("bg-yellow-100");
        assertThat(expectedLine.getName()).isEqualTo("크로플선");
    }

    @Test
    @DisplayName("노선 수정 예외 테스트")
    void update_duplicate() {
        //given
        Line line = lineDao.insert("bg-red-100", "거북선");
        lineDao.insert("bg-yellow-100", "원양어선");

        //when - then
        assertThatThrownBy(() -> lineDao.update(line.getId(), "bg-yellow-100", "원양어선"))
                .isInstanceOf(DataAccessException.class);
    }

    @Test
    @DisplayName("노선 삭제 테스트")
    void delete() {
        //given
        lineDao.insert("bg-red-100", "거북선");
        Line line2 = lineDao.insert("bg-yellow-100", "원양어선");

        //when
        lineDao.delete(line2.getId());

        //then
        assertThat(lineDao.findAll()).hasSize(1);
    }
}