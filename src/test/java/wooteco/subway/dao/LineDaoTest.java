package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.domain.Line;
import wooteco.subway.exception.line.NoSuchLineException;

class LineDaoTest extends DaoTest {

    private static final String RED_LINE_NAME = "레드라인";
    private static final String RED = "bg-red-600";
    private static final String BLUE_LINE_NAME = "블루라인";
    private static final String BLUE = "bg-blue-600";

    private Line redLine;
    private Line blueLine;

    @BeforeEach
    void setUpData() {
        redLine = new Line(RED_LINE_NAME, RED);
        blueLine = new Line(BLUE_LINE_NAME, BLUE);
    }

    @Test
    @DisplayName("노선을 저장하면 저장된 노선 정보를 반환한다.")
    void Save() {
        // when
        final Line savedLine = lineDao.insert(redLine).orElseThrow();

        // then
        assertThat(savedLine.getName()).isEqualTo(redLine.getName());
        assertThat(savedLine.getColor()).isEqualTo(redLine.getColor());
    }

    @Test
    @DisplayName("저장하려는 노선 이름이 중복이면 예외를 던진다.")
    void Save_DuplicateName_ExceptionThrown() {
        lineDao.insert(redLine).orElseThrow();

        // when
        final Line duplicateLine = new Line(RED_LINE_NAME, BLUE);

        // then
        final Optional<Line> possibleLine = lineDao.insert(duplicateLine);
        assertThat(possibleLine).isEmpty();
    }

    @Test
    @DisplayName("모든 노선을 조회한다.")
    void FindAll() {
        // given
        lineDao.insert(redLine);
        lineDao.insert(blueLine);

        // when
        final List<Line> lines = lineDao.findAll();

        // then
        assertThat(lines).hasSize(2);
    }

    @Test
    @DisplayName("id를 통해 해당하는 노선을 조회한다.")
    void FindById() {
        // given
        final Line persistLine = lineDao.insert(redLine).orElseThrow();

        // when
        final Optional<Line> actual = lineDao.findById(persistLine.getId());

        // then
        assertThat(actual).isPresent()
                .contains(persistLine);
    }

    @Test
    @DisplayName("존재하지 않은 id로 노선을 조회하면 Empty Optional을 반환한다.")
    void FindById_InvalidId_EmptyOptional() {
        // when
        final Optional<Line> possibleLine = lineDao.findById(999L);

        // then
        assertThat(possibleLine).isEmpty();
    }

    @Test
    @DisplayName("id를 통해 해당하는 노선을 업데이트한다.")
    void UpdateById() {
        // given
        final Line persistLine = lineDao.insert(redLine).orElseThrow();

        // when
        final Line updatedLine = lineDao.updateById(persistLine.getId(), blueLine).orElseThrow();

        // then
        assertThat(updatedLine).isEqualTo(blueLine);
    }

    @Test
    @DisplayName("업데이트하려는 노선이 존재하지 않으면 에외가 발생한다.")
    void UpdateById_InvalidId_ExceptionThrown() {
        assertThatThrownBy(() -> lineDao.updateById(999L, redLine))
                .isInstanceOf(NoSuchLineException.class);
    }

    @Test
    @DisplayName("id를 통해 해당하는 노선을 삭제한다.")
    void DeleteById() {
        // given
        final Line persistLine = lineDao.insert(redLine).orElseThrow();

        // when
        final Long id = persistLine.getId();
        final Integer affectedRows = lineDao.deleteById(id);

        // then
        assertThat(affectedRows).isOne();
        assertThat(lineDao.findAll()).isEmpty();
    }

    @Test
    @DisplayName("존재하지 않는 id의 역을 삭제하면 예외가 발생한다.")
    void DeleteById_InvalidId_ExceptionThrown() {
        assertThatThrownBy(() -> lineDao.deleteById(999L))
                .isInstanceOf(NoSuchLineException.class);
    }
}
