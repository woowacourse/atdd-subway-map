package wooteco.subway.line;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Transactional
@Sql("/init-line.sql")
@SpringBootTest
class LineDaoTest {
    private static final String lineName1 = "2호선";
    private static final String lineName2 = "9호선";
    private static final String color1 = "초록색";
    private static final String color2 = "남색";

    @Autowired
    private LineDao lineDao;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate.update("insert into LINE(name, color) values (?, ?)", lineName1, color1);
    }

    @Test
    @DisplayName("이름으로 노선 검색")
    void findByName() {
        Optional<Line> findStation = lineDao.findByName(lineName1);
        assertTrue(findStation.isPresent());
    }

    @Test
    @DisplayName("존재하지 않는 노선 이름 검색")
    void findNoneExistLineByName() {
        Optional<Line> findStation = lineDao.findByName(lineName2);
        assertFalse(findStation.isPresent());
    }

    @Test
    @DisplayName("존재하지 않는 노선 Id 검색")
    void findNoneExistLineById() {
        Optional<Line> findStation = lineDao.findById(10L);
        assertFalse(findStation.isPresent());
    }

    @Test
    @DisplayName("모든 노선 검색")
    void findAll() {
        assertThat(lineDao.findAll()).hasSize(1);
    }

    @Test
    @DisplayName("노선 생성 저장 확인")
    void save() {
        lineDao.save(lineName2, color2);
        assertThat(lineDao.findAll()).hasSize(2);
    }

    @Test
    @DisplayName("노선 정보 수정")
    void update() {
        Long savedLineId = lineDao.save(lineName2, color2);
        lineDao.update(savedLineId, "3호선", "주황색");

        assertThat(lineDao.findById(savedLineId)
                          .get()).isEqualTo(new Line(savedLineId, "3호선", "주황색"));
    }

    @Test
    @DisplayName("노선 정보 삭제")
    void delete() {
        Long savedLineId = lineDao.save(lineName2, color2);

        assertTrue(lineDao.findById(savedLineId)
                          .isPresent());
        lineDao.delete(savedLineId);
        assertFalse(lineDao.findById(savedLineId)
                           .isPresent());
    }
}