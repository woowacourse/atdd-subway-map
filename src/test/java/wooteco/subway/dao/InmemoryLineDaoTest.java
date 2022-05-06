package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.domain.Line;

class InmemoryLineDaoTest {

    private final InmemoryLineDao inmemoryLineDao = InmemoryLineDao.getInstance();

    @AfterEach
    void afterEach() {
        inmemoryLineDao.clear();
    }

    @Test
    @DisplayName("Line을 등록할 수 있다.")
    void save() {
        Line line = new Line("신분당선", "bg-red-600");
        Line savedLine = inmemoryLineDao.save(line);

        assertThat(savedLine.getId()).isNotNull();
    }

    @Test
    @DisplayName("Line을 id로 조회할 수 있다.")
    void findById() {
        Line line = inmemoryLineDao.save(new Line("신분당선", "bg-red-600"));
        Line findLine = inmemoryLineDao.findById(line.getId());

        assertThat(findLine).isEqualTo(line);
    }

    @Test
    @DisplayName("Line 전체 조회할 수 있다.")
    void findAll() {
        inmemoryLineDao.save(new Line("신분당선", "bg-red-600"));
        inmemoryLineDao.save(new Line("분당선", "bg-green-600"));

        assertThat(inmemoryLineDao.findAll()).hasSize(2);
    }

    @Test
    @DisplayName("Line 이름이 존재하는지 확인할 수 있다.")
    void existByName() {
        inmemoryLineDao.save(new Line("신분당선", "bg-red-600"));

        assertThat(inmemoryLineDao.existByName("신분당선")).isTrue();
    }

    @Test
    @DisplayName("Line을 수정할 수 있다.")
    void update() {
        Line line = inmemoryLineDao.save(new Line("신분당선", "bg-red-600"));
        int result = inmemoryLineDao.update(new Line(line.getId(), "분당선", line.getColor()));

        assertThat(result).isEqualTo(1);
    }

    @Test
    @DisplayName("Line을 삭제할 수 있다.")
    void delete() {
        Line line = inmemoryLineDao.save(new Line("신분당선", "bg-red-600"));

        assertThatCode(() -> inmemoryLineDao.delete(line.getId())).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("없는 id의 Line을 삭제할 수 없다.")
    void deleteByInvalidId() {
        Line line = inmemoryLineDao.save(new Line("신분당선", "bg-red-600"));
        Long lineId = line.getId() + 1;

        assertThatThrownBy(() -> inmemoryLineDao.delete(lineId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("없는 line 입니다.");
    }

    @Test
    @DisplayName("이미 삭제한 id의 Line 삭제할 수 없다.")
    void deleteByDuplicatedId() {
        Line line = inmemoryLineDao.save(new Line("신분당선", "bg-red-600"));
        Long lineId = line.getId();
        inmemoryLineDao.delete(lineId);

        assertThatThrownBy(() -> inmemoryLineDao.delete(lineId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("없는 line 입니다.");
    }
}
