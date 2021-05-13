package wooteco.subway.line.dao;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.line.dao.LineDao;
import wooteco.subway.line.dao.MemoryLineDao;
import wooteco.subway.line.domain.Line;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MemoryLineDaoTest {
    private String name;
    private Long id;
    private LineDao lineDao = new MemoryLineDao();
    private Line save;

    @BeforeEach
    void setUp() {
        name = "아마찌선";
        id = 1L;
        save = lineDao.save(new Line(id, name, "bg-red-600"));
    }

    @AfterEach
    void clean() {
        lineDao.clear();
    }

    @Test
    @DisplayName("노선을 저장한다.")
    void save() {
        String saveName = "3호선";
        Line save = lineDao.save(new Line("3호선", "bg-red-600"));

        assertThat(lineDao.findByName(saveName).get()).isEqualTo(save);
    }

    @Test
    @DisplayName("노선을 저장한다.")
    void saveDuplicatedName() {
        assertThatThrownBy(() -> lineDao.save(new Line(2L, name, "bg-red-600")));
    }

    @Test
    @DisplayName("전체 노선을 조회한다.")
    void findAll() {
        assertThat(lineDao.findAll()).hasSize(1);
    }

    @Test
    @DisplayName("이름으로 단일 노선을 조회한다.")
    void findByName() {
        assertThat(lineDao.findByName(name).get().name()).isEqualTo(name);
    }

    @Test
    @DisplayName("노선을 수정한다.")
    void updateLine() {
        String updateName = "흑기선";
        String updateColor = "bg-red-700";

        lineDao.update(new Line(id, updateName, updateColor));
        Line findLine = lineDao.findById(id).get();

        assertThat(findLine).isEqualTo(save);
    }
}