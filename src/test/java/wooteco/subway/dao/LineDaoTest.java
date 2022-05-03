package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.domain.Line;

class LineDaoTest {
    @BeforeEach
    public void setUp() {
        delete_all();
    }

    @DisplayName("중복되는 노선 이름이 없을 때 성공적으로 저장되는지 테스트")
    @Test
    void save_success() {
        Line line = LineDao.save(new Line("testName", "black"));
        assertThat(LineDao.findAll().size()).isEqualTo(1);
    }

    @DisplayName("중복되는 노선 이름이 있을 때 예외 반환 테스트")
    @Test
    void save_fail() {
        Line line = LineDao.save(new Line("testName", "black"));
        assertThatThrownBy(() -> LineDao.save(new Line("testName", "white")))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("존재하는 노선 id가 있으면 삭제되는지 테스트")
    @Test
    void deleteById_exist() {
        Line line = LineDao.save(new Line("testName", "black"));
        LineDao.deleteById(line.getId());
        assertThat(LineDao.findAll().isEmpty()).isTrue();
    }

    @DisplayName("존재하는 노선 id가 없으면 삭제되지 않는지 테스트")
    @Test
    void deleteById_not_exist() {
        Line line = LineDao.save(new Line("testName", "black"));
        LineDao.deleteById(-1L);
        assertThat(LineDao.findAll().isEmpty()).isFalse();
    }

    @DisplayName("존재하는 노선 id가 있으면 Optional이 비지 않았는지 테스트")
    @Test
    void findById_exist() {
        Line line = LineDao.save(new Line("testName", "black"));
        Optional<Line> result = LineDao.findById(line.getId());
        assertThat(result.isPresent()).isTrue();
    }

    @DisplayName("존재하는 노선 id가 없으면 Optional이 비었는지 테스트")
    @Test
    void findById_not_exist() {
        Line line = LineDao.save(new Line("testName", "black"));
        Optional<Line> result = LineDao.findById(-1L);
        assertThat(result.isPresent()).isFalse();
    }

    @DisplayName("id가 없는 노선의 이름을 바꿀때 예외가 발생하는지 테스트")
    @Test
    void changeLineName_no_id() {
        assertThatThrownBy(() -> LineDao.changeLineName(-1L, "testName2"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("바뀐 이름이 중복될 때 예외가 발생하는지 테스트")
    @Test
    void changeLineName_duplicate() {
        Line line = LineDao.save(new Line("testName", "black"));
        Line line2 = LineDao.save(new Line("testName2", "black"));
        assertThatThrownBy(() -> LineDao.changeLineName(line.getId(), "testName2"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("id가 있고 바뀐 이름이 중복되지 않을 때 예외가 발생하지 않는지 테스트")
    @Test
    void changeLineName_success() {
        Line line = LineDao.save(new Line("testName", "black"));
        Line line2 = LineDao.save(new Line("testName2", "black"));
        LineDao.changeLineName(line.getId(), "testName3");
        assertThat(line.getName()).isEqualTo("testName3");
    }

    @DisplayName("원래 자신의 이름으로 바꿨을 때 예외가 발생하지 않는지 테스트")
    @Test
    void changeLineName_self_loop() {
        Line line = LineDao.save(new Line("testName", "black"));
        Line line2 = LineDao.save(new Line("testName2", "black"));
        LineDao.changeLineName(line.getId(), "testName");
        assertThat(line.getName()).isEqualTo("testName");
    }

    void delete_all() {
        List<Line> lines = LineDao.findAll();
        lines.clear();
    }
}
