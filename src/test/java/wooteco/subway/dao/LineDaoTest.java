package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import wooteco.subway.domain.Line;

@JdbcTest
@Import(LineDao.class)
public class LineDaoTest {

    @Autowired
    private LineDao lineDao;

    @Test
    @DisplayName("노선 저장")
    void save() {
        Line line = new Line("선릉역", "blue");
        Line savedLine = lineDao.save(line);
        assertThat(savedLine.getId()).isNotNull();
        assertThat(savedLine.getName()).isEqualTo("1호선");
    }

    @Test
    @DisplayName("지하철 역 이름 중복 여부 조회")
    void duplicateName() {
        Line line = new Line("1호선", "blue");
        lineDao.save(line);
        assertThat(lineDao.existByNameAndColor("1호선", "blue")).isTrue();
    }

    @Test
    @DisplayName("id로 노선 조회")
    void findById() {
        Line line = lineDao.save(new Line("1호선", "blue"));
        Line findLine = lineDao.findById(line.getId()).get();
        assertThat(findLine.getId()).isNotNull();
        assertThat(findLine.getName()).isEqualTo("1호선");
    }

    @Test
    @DisplayName("노선 전체 조회")
    void findAll() {
        Line line1 = new Line("1호선", "blue");
        Line line2 = new Line("2호선", "red");
        lineDao.save(line1);
        lineDao.save(line2);
        List<Line> liens = lineDao.findAll();
        assertThat(liens).hasSize(2);
    }

    @Test
    @DisplayName("id로 노선 수정")
    void modifyById() {
        Line savedLine = lineDao.save(new Line("1호선", "blue"));
        lineDao.modifyById(savedLine.getId(), new Line("2호선", "red"));
        Line updateLine = lineDao.findById(savedLine.getId()).get();
        assertThat(updateLine.getName()).isEqualTo("2호선");
        assertThat(updateLine.getColor()).isEqualTo("red");
    }

    @Test
    @DisplayName("id로 노선 삭제")
    void deleteById() {
        Line savedLine = lineDao.save(new Line("1호선", "blue"));
        lineDao.deleteById(savedLine.getId());
        assertThat(lineDao.findAll()).hasSize(0);
    }

}
