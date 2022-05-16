package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.domain.Line;
import wooteco.subway.ui.dto.LineCreateRequest;
import wooteco.subway.ui.dto.LineRequest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
class LineDaoTest {

    @Autowired
    private LineDao lineDao;

    @DisplayName("노선 저장")
    @Test
    void save() {
        // given
        LineCreateRequest line = new LineCreateRequest("분당선", "yellow", 1L, 2L, 2);

        // when
        Long id = lineDao.save(line);

        // then
        assertThat(id).isEqualTo(3L);
    }

    @DisplayName("노선 이름이 존재하는지 확인")
    @Test
    void existsByName() {
        // given
        String name = "신분당선";

        // when
        boolean result = lineDao.existsByName(name);

        // then
        assertThat(result).isTrue();
    }

    @DisplayName("특정 id를 제외하고 노선 이름이 존재하는지 확인")
    @Test
    void existsByNameExceptWithId() {
        // given
        String name = "신분당선";

        // when
        boolean result = lineDao.existsByNameExceptWithId(name, 1L);

        // then
        assertThat(result).isFalse();
    }

    @DisplayName("해당 id 존재하는지 확인")
    @Test
    void existsById() {
        // given

        // when
        boolean result = lineDao.existsById(1L);

        // then
        assertThat(result).isTrue();
    }

    @DisplayName("노선 id로 검색")
    @Test
    void findById() {
        // given
        Line expected = new Line(1L, "신분당선", "red");

        // when
        Optional<Line> line = lineDao.findById(1L);

        // then
        assertThat(line.isPresent()).isTrue();
        assertThat(line.get()).isEqualTo(expected);
    }

    @DisplayName("노선 전체 조회")
    @Test
    void findAll() {
        // given

        // when
        List<Line> lines = lineDao.findAll();

        // then
        assertThat(lines.size()).isEqualTo(2);
    }

    @DisplayName("노선 정보 수정")
    @Test
    void update() {
        // given
        Long id = 1L;
        LineRequest lineRequest = new LineRequest("신분당선", "pink");

        // when
        lineDao.update(id, lineRequest);

        // then
        Optional<Line> line = lineDao.findById(id);

        assertThat(line.isPresent()).isTrue();
        assertThat(line.get()).extracting(Line::getName, Line::getColor)
                .contains(lineRequest.getName(), lineRequest.getColor());
    }

    @DisplayName("노선 삭제")
    @Test
    void deleteById() {
        // given
        Long id = 1L;

        // when
        lineDao.deleteById(id);

        // then
        assertThat(lineDao.findById(id)).isEqualTo(Optional.empty());
    }
}