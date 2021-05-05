package wooteco.subway.line;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MemoryLineDaoTest {
    private String name;
    private Long id;
    private MemoryLineDao memoryLineDao = new MemoryLineDao();

    @BeforeEach
    void setUp() {
        name = "아마찌선";
        id = 1L;
        memoryLineDao.save(new Line(id, name, "bg-red-600"));
    }

    @AfterEach
    void clean() {
        memoryLineDao.clear();
    }

    @Test
    @DisplayName("노선을 저장한다.")
    void save() {
        assertThat(memoryLineDao.findByName(name).get().name()).isEqualTo(name);
    }

    @Test
    @DisplayName("노선을 저장한다.")
    void saveDuplicatedName() {
        assertThatThrownBy(() -> memoryLineDao.save(new Line(2L, name, "bg-red-600")));
    }

    @Test
    @DisplayName("전체 노선을 조회한다.")
    void findAll() {
        assertThat(memoryLineDao.findAll()).hasSize(1);
    }

    @Test
    @DisplayName("이름으로 단일 노선을 조회한다.")
    void findByName() {
        assertThat(memoryLineDao.findByName(name).get().name()).isEqualTo(name);
    }

    @Test
    @DisplayName("노선을 수정한다.")
    void updateLine() {
        String updateName = "흑기선";
        String updateColor = "bg-red-700";

        memoryLineDao.update(id, updateName, updateColor);
        Line findLine = memoryLineDao.findById(id).get();

        assertThat(findLine.name()).isEqualTo(updateName);
        assertThat(findLine.color()).isEqualTo(updateColor);
    }
}