package wooteco.subway.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import wooteco.subway.domain.Line;
import wooteco.subway.exception.BusinessException;

import javax.sql.DataSource;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@JdbcTest
public class LineDaoTest {

    @Autowired
    private DataSource dataSource;

    private LineDao dao;

    @BeforeEach
    void setUp() {
        dao = new LineDao(dataSource);
    }

    @Test
    @DisplayName("지하철 노선 저장")
    void save() {
        Line line = dao.save("선릉역", "green");

        assertThat(line.getId()).isNotNull();
        assertThat(line.getName()).isEqualTo("선릉역");
        assertThat(line.getColor()).isEqualTo("green");
    }

    @Test
    @DisplayName("전체 지하철 노선 조회")
    void findAll() {
        dao.save("선릉역", "purple");
        dao.save("구의역", "green");

        List<Line> response = dao.findAll();
        List<String> names = response.stream()
                .map(Line::getName)
                .collect(Collectors.toList());
        List<String> colors = response.stream()
                .map(Line::getColor)
                .collect(Collectors.toList());

        assertThat(response.size()).isEqualTo(2);
        assertThat(names).containsAll(List.of("선릉역", "구의역"));
        assertThat(colors).containsAll(List.of("purple", "green"));
    }

    @Test
    @DisplayName("지하철 노선이 존재하는 경우 조회 가능")
    void findById() {
        Line saveLine = dao.save("선릉역", "green");

        Line response = dao.findById(saveLine.getId());

        assertThat(response.getName()).isEqualTo("선릉역");
        assertThat(response.getColor()).isEqualTo("green");
    }

    @Test
    @DisplayName("지하철 노선이 존재하지 않는 경우 조회 불가능")
    void findByIdEmpty() {
        assertThatThrownBy(() -> dao.findById(1L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("존재하지 않는 노선입니다.");
    }

    @Test
    @DisplayName("지하철 노선이 존재하는 경우 삭제 가능")
    void delete() {
        Line line = dao.save("선릉역", "green");

        assertDoesNotThrow(() -> dao.delete(line.getId()));
        assertThat(dao.findAll().size()).isEqualTo(0);
    }

    @Test
    @DisplayName("지하철 노선이 존재하지 않는 경우 삭제 불가능")
    void deleteEmpty() {
        assertThatThrownBy(() -> dao.delete(1L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("존재하지 않는 노선입니다.");
    }

    @Test
    @DisplayName("지하철 노선이 존재하는 경우 수정 가능")
    void update() {
        Line saveLine = dao.save("선릉역", "yellow");

        dao.update(saveLine.getId(), "구의역", "green");
        Line findLine = dao.findById(saveLine.getId());

        assertThat(findLine.getName()).isEqualTo("구의역");
        assertThat(findLine.getColor()).isEqualTo("green");
    }

    @Test
    @DisplayName("지하철 노선이 존재하지 않는 경우 수정 불가능")
    void updateEmpty() {
        assertThatThrownBy(() -> dao.update(1L, "선릉역", "green"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("존재하지 않는 노선입니다.");
    }

    @Test
    @DisplayName("존재하는 이름인 경우 true 반환")
    void isExistNameWhenTrue() {
        dao.save("선릉역", "green");
        assertThat(dao.isExistName("선릉역")).isTrue();
    }

    @Test
    @DisplayName("존재하지 않는 이름인 경우 false 반환")
    void isExistNameWhenFalse() {
        assertThat(dao.isExistName("선릉역")).isFalse();
    }

    @Test
    @DisplayName("자신을 제외하고 존재하는 이름인 경우 true 반환")
    void isExistNameWithoutItselfWhenTrue() {
        Line saveLine = dao.save("선릉역", "green");
        assertThat(dao.isExistNameWithoutItself(saveLine.getId() + 1, "선릉역")).isTrue();
    }

    @Test
    @DisplayName("자신을 제외하고 존재하지 않는 이름인 경우 false 반환")
    void isExistNameWithoutItselfWhenFalse() {
        Line saveLine = dao.save("선릉역", "green");
        assertThat(dao.isExistNameWithoutItself(saveLine.getId(), "선릉역")).isFalse();
    }

}
