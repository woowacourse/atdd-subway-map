package wooteco.subway.line;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LineDaoTest {
    private String name;

    @BeforeEach
    void setUp() {
        name = "아마찌선";
        LineDao.save(new Line(1L, name, "bg-red-600"));
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
    @DisplayName("")
    void findAll() {
        assertThat(LineDao.findAll()).hasSize(1);
    }

    @Test
    void findByName() {
        assertThat(LineDao.findByName(name).get().name()).isEqualTo(name);
    }
}