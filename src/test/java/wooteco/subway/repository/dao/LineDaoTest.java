package wooteco.subway.repository.dao;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;

import javax.sql.DataSource;
import wooteco.subway.repository.dao.entity.line.LineEntity;

@DisplayName("지하철노선 DB")
@JdbcTest
class LineDaoTest {

    private static final String LINE_NAME = "신분당선";
    private static final String LINE_COLOR = "red";
    private static final LineEntity LINE_ENTITY = new LineEntity(1L, LINE_NAME, LINE_COLOR);

    @Autowired
    private DataSource dataSource;
    private LineDao lineDao;

    @BeforeEach
    void setUp() {
        this.lineDao = new LineDao(dataSource);
    }

    @DisplayName("지하철노선을 저장한다.")
    @Test
    void save() {
        Long actual = lineDao.save(LINE_ENTITY);
        assertThat(actual).isGreaterThan(0);
    }

    @DisplayName("노선 목록을 조회한다.")
    @ParameterizedTest
    @ValueSource(ints = {5})
    void findAll(int expected) {
        LongStream.rangeClosed(1, expected)
                .mapToObj(id -> new LineEntity(id, "호선" + id, "색상" + id))
                .forEach(lineDao::save);

        assertThat(lineDao.findAll()).hasSize(expected);
    }

    @DisplayName("노선을 조회한다.")
    @Test
    void findById() {
        LineEntity expected = LINE_ENTITY;
        Optional<LineEntity> actual = lineDao.findById(lineDao.save(expected));

        assertThat(actual).isPresent();
        assertThat(actual.get()).usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(expected);
    }

    @DisplayName("존재하지 않는 노선을 조회한다.")
    @Test
    void findWithNonexistentId() {
        Optional<LineEntity> actual = lineDao.findById(1L);
        assertThat(actual).isEmpty();
    }

    @DisplayName("해당 식별자의 노선이 존재하는지 확인한다.")
    @ParameterizedTest
    @CsvSource(value = {"0,true", "1,false"})
    void existsById(Long difference, boolean expected) {
        Long lineId = lineDao.save(LINE_ENTITY);

        boolean actual = lineDao.existsById(lineId + difference);
        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("해당 이름의 노선이 존재하는지 확인한다.")
    @ParameterizedTest
    @MethodSource("provideForExistsByName")
    void existsByName(String lineName, boolean expected) {
        lineDao.save(LINE_ENTITY);

        boolean actual = lineDao.existsByName(lineName);
        assertThat(actual).isEqualTo(expected);
    }

    private static Stream<Arguments> provideForExistsByName() {
        return Stream.of(
                Arguments.of(LINE_NAME, true),
                Arguments.of(LINE_NAME + "temp", false));
    }

    @DisplayName("해당 색상의 노선이 존재하는지 확인한다.")
    @ParameterizedTest
    @MethodSource("provideForExistsByColor")
    void existsByColor(String lineColor, boolean expected) {
        lineDao.save(LINE_ENTITY);

        boolean actual = lineDao.existsByColor(lineColor);
        assertThat(actual).isEqualTo(expected);
    }

    private static Stream<Arguments> provideForExistsByColor() {
        return Stream.of(
                Arguments.of(LINE_COLOR, true),
                Arguments.of(LINE_COLOR + "temp", false));
    }

    @DisplayName("노선 정보를 수정한다.")
    @ParameterizedTest
    @CsvSource(value = {"2호선,black", "1호선,white"})
    void update(String lineName, String lineColor) {
        Long lineId = lineDao.save(LINE_ENTITY);

        LineEntity expected = new LineEntity(lineId, lineName, lineColor);
        lineDao.update(expected);

        Optional<LineEntity> actual = lineDao.findById(lineId);

        assertThat(actual).isPresent();
        assertThat(actual.get()).usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @DisplayName("노선을 삭제한다.")
    @Test
    void remove() {
        Stream.of(LINE_ENTITY)
                .map(lineDao::save)
                .forEach(lineDao::remove);

        assertThat(lineDao.findAll()).isEmpty();
    }
}
