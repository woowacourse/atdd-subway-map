package wooteco.subway.line.domain;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MemoryLineEntityDaoTest {
    private String name;
    private Long id;
    private LineDao lineDao = new MemoryLineDao();

    @BeforeEach
    void setUp() {
        name = "아마찌선";
        id = 1L;
        lineDao.save(new LineEntity(id, name, "bg-red-600"));
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
        assertThatThrownBy(() -> lineDao.save(new LineEntity(2L, name, "bg-red-600")));
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

        lineDao.update(id, updateName, updateColor);
        LineEntity findLineEntity = lineDao.findById(id).get();

        assertThat(findLineEntity.name()).isEqualTo(updateName);
        assertThat(findLineEntity.color()).isEqualTo(updateColor);
    }
}