package wooteco.subway.line;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LineDaoTest {
    private String name;
    private Long id;

    @BeforeEach
    void setUp() {
        name = "아마찌선";
        id = 1L;
        LineDao.save(new Line(id, name, "bg-red-600"));
    }

    @AfterEach
    void clean() {
        LineDao.clear();
    }

    @Test
    @DisplayName("노선을 저장한다.")
    void save() {
        assertThat(LineDao.findByName(name).get().name()).isEqualTo(name);
    }

    @Test
    @DisplayName("노선을 저장한다.")
    void saveDuplicatedName() {
        assertThatThrownBy(() -> LineDao.save(new Line(2L, name, "bg-red-600")));
    }

    @Test
    @DisplayName("전체 노선을 조회한다.")
    void findAll() {
        assertThat(LineDao.findAll()).hasSize(1);
    }

    @Test
    @DisplayName("이름으로 단일 노선을 조회한다.")
    void findByName() {
        assertThat(LineDao.findByName(name).get().name()).isEqualTo(name);
    }

    @Test
    @DisplayName("노선을 수정한다.")
    void updateLine() {
        String updateName = "흑기선";
        String updateColor = "bg-red-700";

        LineDao.update(id, updateName, updateColor);
        Line findLine = LineDao.findById(id).get();

        assertThat(findLine.name()).isEqualTo(updateName);
        assertThat(findLine.color()).isEqualTo(updateColor);
    }
}