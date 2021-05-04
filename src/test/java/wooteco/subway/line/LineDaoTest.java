package wooteco.subway.line;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LineDaoTest {

    private LineDao lineDao;
    private Line line;

    @BeforeEach
    void setUp() {
        lineDao = new LineDao();
        line = new Line("2호선", " 초록색");
    }

    @Test
    @DisplayName("노선 생성 저장 확인")
    void save() {
        lineDao.save(line);
        assertTrue(lineDao.findAll()
                          .contains(line));
    }

    @Test
    @DisplayName("존재하는 노선 검색")
    void findExistLineById() {
        Line savedLine = lineDao.save(line);
        Line findLine = lineDao.findById(savedLine.getId());
        assertEquals(savedLine, findLine);
    }

    @Test
    @DisplayName("존재하지 않는 노선 검색")
    void findNoneExistLineById() {
        assertThatThrownBy(() -> lineDao.findById(2L))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("노선 정보 수정")
    void update() {
        Line savedLine = lineDao.save(line);
        lineDao.update(savedLine.getId(), "9호선", "남색");
        assertEquals(savedLine.getName(), "9호선");
        assertEquals(savedLine.getColor(), "남색");
    }
}