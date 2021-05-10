package wooteco.subway.line.dao;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.line.domain.Line;
import wooteco.subway.line.dao.LineDao;
import wooteco.subway.line.dao.MemoryLineDao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MemoryLineDaoTest {
    private String name;
    private Long id;
    private LineDao lineDao = new MemoryLineDao();

    @BeforeEach
    void setUp() {
        name = "아마찌선";
        id = 1L;
        lineDao.save(new Line(id, name, "bg-red-600"));
    }

    @AfterEach
    void clean() {
        lineDao.clear();
    }

    @Test
    @DisplayName("노선을 저장한다.")
    void save() {
        assertThat(lineDao.findByName(name).get().name()).isEqualTo(name);
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
}