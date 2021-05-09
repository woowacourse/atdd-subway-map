package wooteco.subway.line;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

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
        jdbcTemplate.execute("truncate table LINE");
        jdbcTemplate.update("insert into LINE(name, color) values (?, ?)", lineName1, color1);
    }

    @Test
    @DisplayName("이름으로 노선 검색")
    void findByName() {
        Optional<Line> findStation = lineDao.findByName(lineName1);
        assertThat(findStation.get().getName()).isEqualTo("2호선");
    }

    @Test
    @DisplayName("존재하지 않는 노선 검색")
    void findNoneExistLineById() {
        Optional<Line> findStation = lineDao.findByName(lineName2);
        assertThat(findStation.isPresent()).isFalse();
    }

    @Test
    @DisplayName("모든 노선 검색")
    void findAll() {
        assertThat(lineDao.findAll()).hasSize(1);
    }

    @Test
    @DisplayName("노선 생성 저장 확인")
    void save() {
        Line savedLine1 = new Line(1L, "2호선", "초록색");
        Line savedLine2 = lineDao.save(lineName2, color2);
        List<Line> lines = lineDao.findAll();

        assertThat(lines.size()).isEqualTo(2);
        assertThat(lines).containsExactlyInAnyOrderElementsOf(Arrays.asList(savedLine1, savedLine2));
    }

    @Test
    @DisplayName("노선 정보 수정")
    void update() {
        Line savedLine = lineDao.save(lineName2, color2);
        lineDao.update(savedLine.getId(), "3호선", "주황색");

        Line findLine = lineDao.findById(savedLine.getId()).get();
        assertThat("9호선").isEqualTo(findLine.getName());
        assertThat("3호선").isEqualTo(findLine.getName());
    }

    @Test
    @DisplayName("노선 정보 삭제")
    void delete() {
        Line savedLine = lineDao.save(lineName2, color2);
        assertThat(lineDao.findByName(savedLine.getName()).isPresent()).isTrue();

        lineDao.delete(savedLine.getId());
        assertThat(lineDao.findByName(savedLine.getName()).isPresent()).isFalse();
    }
}