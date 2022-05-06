package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.exception.NotFoundException;

class LineServiceTest {

    private LineService lineService;
    private LineDao fakeLineDao;

    @BeforeEach
    void setUp() {
        fakeLineDao = new FakeLineDao();
        lineService = new LineService(fakeLineDao);
    }

    @Test
    @DisplayName("노선을 생성한다.")
    void createLine() {
        // given
        final LineRequest request = new LineRequest("7호선", "bg-red-600", null, null, 0);

        // when
        final LineResponse response = lineService.create(request);

        // then
        assertThat(response.getName()).isEqualTo(request.getName());
    }

    @Test
    @DisplayName("저장하려는 노선의 이름이 중복되면 예외를 던진다.")
    void Create_DuplicateName_ExceptionThrown() {
        // given
        final String name = "7호선";
        final String color = "bg-red-600";
        final Line line = new Line(name, color);
        fakeLineDao.save(line);

        final LineRequest request = new LineRequest(name, color, null, null, 0);

        // then
        assertThatThrownBy(() -> lineService.create(request))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessage("중복된 이름의 노선은 저장할 수 없습니다.");
    }

    @Test
    @DisplayName("모든 노선을 조회한다.")
    void showLines() {
        // given
        fakeLineDao.save(new Line("1호선", "bg-red-600"));
        fakeLineDao.save(new Line("수인분당선", "bg-blue-600"));

        // when
        final List<LineResponse> responses = lineService.findAll();

        // then
        assertThat(responses).hasSize(2);
    }

    @Test
    @DisplayName("id에 해당하는 노선을 조회한다.")
    void findById() {
        // given
        final String name = "1호선";
        final String color = "bg-red-600";
        final Line savedLine = fakeLineDao.save(new Line(name, color)).orElseThrow();

        // when
        final LineResponse response = lineService.findById(savedLine.getId());

        // then
        assertThat(response.getName()).isEqualTo(name);
        assertThat(response.getColor()).isEqualTo(color);
    }

    @Test
    @DisplayName("id에 해당하는 노선이 존재하지 않으면 예외를 던진다.")
    void FindById_NotExistId_ExceptionThrown() {
        assertThatThrownBy(() -> lineService.findById(999L))
                        .isInstanceOf(NotFoundException.class)
                        .hasMessage("해당 ID에 맞는 노선을 찾지 못했습니다.");
    }

    @Test
    @DisplayName("id에 해당하는 노선 정보를 수정한다.")
    void updateById() {
        // given
        final Line savedLine = fakeLineDao.save(new Line("1호선", "bg-red-600")).orElseThrow();

        final String name = "7호선";
        final String color = "bg-blue-600";
        final LineRequest request = new LineRequest(name, color, null, null, 0);

        // when
        lineService.updateById(savedLine.getId(), request);

        // then
        final Line updatedLine = fakeLineDao.findById(savedLine.getId()).orElseThrow();
        assertThat(updatedLine.getName()).isEqualTo(name);
        assertThat(updatedLine.getColor()).isEqualTo(color);
    }

    @Test
    @DisplayName("업데이트하려는 이름이 중복되면 예외를 던진다.")
    void UpdateById_DuplicateName_ExceptionThrown() {
        // given
        final String name = "1호선";
        final String color = "bg-blue-600";

        fakeLineDao.save(new Line(name, "bg-red-600"));
        final Line line = fakeLineDao.save(new Line("5호선", color)).orElseThrow();

        final LineRequest request = new LineRequest(name, color, null, null, 0);

        // then
        assertThatThrownBy(() -> lineService.updateById(line.getId(), request))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessage("중복된 이름의 노선이 존재합니다.");
    }

    @Test
    @DisplayName("id에 해당하는 노선을 삭제한다.")
    void deleteById() {
        // given
        final Line savedLine = fakeLineDao.save(new Line("1호선", "bg-red-600")).orElseThrow();

        // when
        lineService.deleteById(savedLine.getId());

        // then
        final List<Line> remainLines = fakeLineDao.findAll();
        assertThat(remainLines).hasSize(0);
    }
}