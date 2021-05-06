package wooteco.subway.line;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;

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
        LineName lineName = new LineName("코다선");
        Line line = lineDao.insert("bg-red-100", lineName);

        assertThat(line.getColor()).isEqualTo("bg-red-100");
        assertThat(line.getName()).isEqualTo("코다선");
    }

    @Test
    @DisplayName("노선 목록 조회")
    void findAll() {
        //given
        LineName lineName1 = new LineName("거북선");
        LineName lineName2 = new LineName("원양어선");

        lineDao.insert("bg-red-100", lineName1);
        lineDao.insert("bg-black-200", lineName2);

        //when
        List<Line> lines = lineDao.findAll();

        //then
        assertThat(lines).hasSize(2);
    }

    @Test
    @DisplayName("id를 통한 노선 조회")
    void findById() {
        //given
        LineName lineName = new LineName("거북선");
        Line line = lineDao.insert("bg-red-100", lineName);

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
        LineName lineName = new LineName("거북선");
        Line line = lineDao.insert("bg-red-100", lineName);

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
        LineName lineName1 = new LineName("거북선");
        LineName lineName2 = new LineName("원양어선");
        Line line = lineDao.insert("bg-red-100", lineName1);
        lineDao.insert("bg-yellow-100", lineName2);

        //when - then
        assertThatThrownBy(() -> lineDao.update(line.getId(), "bg-yellow-100", "원양어선"))
                .isInstanceOf(DataAccessException.class);
    }

    @Test
    @DisplayName("노선 삭제 테스트")
    void delete() {
        //given
        LineName lineName1 = new LineName("거북선");
        LineName lineName2 = new LineName("원양어선");
        lineDao.insert("bg-red-100", lineName1);
        Line line2 = lineDao.insert("bg-yellow-100", lineName2);

        //when
        lineDao.delete(line2.getId());

        //then
        assertThat(lineDao.findAll()).hasSize(1);
    }
}