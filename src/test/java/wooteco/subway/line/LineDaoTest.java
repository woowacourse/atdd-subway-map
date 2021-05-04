package wooteco.subway.line;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.exception.DuplicateException;

class LineDaoTest {

    private LineDao lineDao;

    @BeforeEach
    void setUp() {
        lineDao = new LineDao();
    }

    @Test
    @DisplayName("노선 한 개가 저장된다.")
    void save() {
        Line line = lineDao.save(new Line("2호선", "bg-green-600"));
        assertThat(line.getId()).isEqualTo(1L);
        assertThat(line.getName()).isEqualTo("2호선");
        assertThat(line.getColor()).isEqualTo("bg-green-600");
    }

    @Test
    @DisplayName("중복된 이름을 갖는 노선은 저장이 안된다.")
    void duplicateSaveValidate() {
        Line line = new Line("2호선", "bg-green-600");
        lineDao.save(line);

        assertThatThrownBy(() -> {
            lineDao.save(line);
        }).isInstanceOf(DuplicateException.class);
    }

    @Test
    @DisplayName("모든 노선 목록을 조회한다")
    void findAll() {
        Line line2 = new Line("2호선", "bg-green-600");
        Line line3 = new Line("3호선", "bg-orange-600");
        Line line4 = new Line("4호선", "bg-skyBlue-600");

        List<Line> lines = Arrays.asList(line2, line3, line4);

        lineDao.save(line2);
        lineDao.save(line3);
        lineDao.save(line4);

        List<Line> linesAll = lineDao.findAll();

        assertThat(linesAll).hasSize(3);
        assertThat(linesAll).isEqualTo(lines);
    }

}