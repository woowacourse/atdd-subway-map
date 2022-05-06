package wooteco.subway.dao;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.domain.Line;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("지하철 노선 관련 가짜 DAO 테스트")
class LineMockDaoTest {

    private static final Line LINE = new Line(1L, "신분당선", "bg-red-600");

    private final LineMockDao lineMockDao = new LineMockDao();

    @AfterEach
    void afterEach() {
        lineMockDao.clear();
    }

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void save() {
        lineMockDao.save(LINE);

        assertThat(lineMockDao.findAll()).hasSize(1);
    }

    @DisplayName("중복된 아이디의 지하철 노선이 있다면 true 를 반환한다.")
    @Test
    void existLineById() {
        long lineId = lineMockDao.save(LINE);

        assertThat(lineMockDao.existLineById(lineId)).isTrue();
    }

    @DisplayName("중복된 이름의 지하철 노선이 있다면 true 를 반환한다.")
    @Test
    void existLineByName() {
        lineMockDao.save(LINE);

        assertThat(lineMockDao.existLineByName("신분당선")).isTrue();
    }

    @DisplayName("중복된 색상의 지하철 노선이 있다면 true 를 반환한다.")
    @Test
    void existLineByColor() {
        lineMockDao.save(LINE);

        assertThat(lineMockDao.existLineByColor("bg-red-600")).isTrue();
    }

    @DisplayName("지하철 노선의 목록을 조회한다.")
    @Test
    void findAll() {
        lineMockDao.save(LINE);

        assertThat(lineMockDao.findAll()).hasSize(1);
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void find() {
        long lineId = lineMockDao.save(LINE);

        Optional<Line> line = lineMockDao.find(lineId);

        assertAll(
                () -> assertThat(line.get().getName()).isEqualTo("신분당선"),
                () -> assertThat(line.get().getColor()).isEqualTo("bg-red-600")
        );
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void update() {
        long lineId = lineMockDao.save(LINE);

        lineMockDao.update(lineId, new Line(lineId, "다른분당선", "bg-green-600"));

        Optional<Line> line = lineMockDao.find(lineId);
        assertAll(
                () -> assertThat(line.get().getName()).isEqualTo("다른분당선"),
                () -> assertThat(line.get().getColor()).isEqualTo("bg-green-600")
        );
    }

    @DisplayName("지하철 노선을 삭제한다.")
    @Test
    void delete() {
        long lineId = lineMockDao.save(LINE);

        lineMockDao.delete(lineId);

        assertThat(lineMockDao.findAll()).hasSize(0);
    }

    @DisplayName("모든 지하철 노선 정보를 삭제한다.")
    @Test
    void clear() {
        lineMockDao.save(LINE);

        lineMockDao.clear();

        assertThat(lineMockDao.findAll()).hasSize(0);
    }
}
