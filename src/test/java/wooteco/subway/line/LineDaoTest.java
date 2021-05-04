package wooteco.subway.line;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class LineDaoTest {
    @Test
    @DisplayName("노선선 생성 저장 확인")
    void save() {
        Line line = new Line("2호선" , " 초록색");
        LineDao lineDao = new LineDao();
        lineDao.save(line);
        assertTrue(lineDao.findAll().contains(line));
    }

}