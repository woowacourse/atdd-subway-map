package wooteco.subway.dao;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.domain.Line;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Transactional
public class LineDaoTest {
    @Autowired
    private LineDao lineDao;

    @Test
    @DisplayName("Line을 정상적으로 생성하는 지 테스트")
    public void createLine() {
        Long id = lineDao.create("1호선", "bg-light-gray");
        assertThat(lineDao.findById(id).isPresent()).isTrue();
    }

    @Test
    @DisplayName("모든 Line을 정상적으로 조회하는 지 테스트")
    public void findAll() {
        lineDao.create("1호선", "bg-light-gray");
        lineDao.create("2호선", "bg-light-yellow");
        assertThat(lineDao.findAll()).hasSize(2);
    }

    @Test
    @DisplayName("특정 Line을 정상적으로 조회하는 지 테스트")
    public void findById() {
        String name = "1호선";
        String color = "bg-light-gray";
        Long id = lineDao.create(name, color);
        Optional<Line> line = lineDao.findById(id);
        assertThat(line.get().getName()).isEqualTo(name);
        assertThat(line.get().getColor()).isEqualTo(color);
    }

    @Test
    @DisplayName("특정 Line을 정상적으로 수정하는 지 테스트")
    public void edit() {
        Long id = lineDao.create("1호선", "bg-light-gray");
        String changedName = "2호선";
        String changedColor = "bg-light-yellow";
        int changedRowCount = lineDao.edit(id, changedName, changedColor);
        Optional<Line> editedLine = lineDao.findById(id);
        assertThat(changedRowCount).isEqualTo(1);
        assertThat(editedLine.get().getName()).isEqualTo(changedName);
        assertThat(editedLine.get().getColor()).isEqualTo(changedColor);
    }

    @Test
    @DisplayName("특정 Line을 정상적으로 삭제하는 지 테스트")
    public void deleteById() {
        Long id = lineDao.create("1호선", "bg-light-gray");
        lineDao.deleteById(id);
        assertThat(lineDao.findById(id).isPresent()).isFalse();
    }
}
