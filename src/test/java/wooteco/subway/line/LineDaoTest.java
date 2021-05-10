package wooteco.subway.line;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Arrays;
import java.util.List;

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
    @DisplayName("노선 생성 확인")
    public void createLine() {
        Line savedLine = lineDao.save(lineName2, color2);
        assertThat(savedLine.getName()).isEqualTo(lineName2);
        assertThat(savedLine.getColor()).isEqualTo(color2);
    }

    @Test
    @DisplayName("ID로 노선 검색")
    void findById() {
        Line findLine = lineDao.findById(1L).get();
        assertThat(findLine.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("이름으로 노선 검색")
    void findByName() {
        Line findLine = lineDao.findByName(lineName1).get();
        assertThat(findLine.getName()).isEqualTo(lineName1);
    }

    @Test
    @DisplayName("모든 노선 검색")
    void findAll() {
        Line savedLine = lineDao.save(lineName2, color2);
        Line savedLine2 = lineDao.findByName(lineName1).get();
        List<Line> lines = lineDao.findAll();

        assertThat(lines).containsExactlyInAnyOrderElementsOf(Arrays.asList(savedLine, savedLine2));
    }

    @Test
    @DisplayName("노선 정보 수정")
    void update() {
        Line findLine = lineDao.findByName(lineName1).get();
        lineDao.update(findLine.getId(), lineName2, color2);
        Line updatedLine = lineDao.findById(findLine.getId()).get();

        assertThat(lineName1).isEqualTo(findLine.getName());
        assertThat(lineName2).isEqualTo(updatedLine.getName());
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