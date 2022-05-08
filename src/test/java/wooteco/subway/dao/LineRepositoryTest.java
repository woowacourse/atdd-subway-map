package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import javax.sql.DataSource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.TestConstructor.AutowireMode;
import wooteco.subway.dao.entity.LineEntity;

@JdbcTest
@TestConstructor(autowireMode = AutowireMode.ALL)
@DisplayName("Line 레포지토리")
class LineRepositoryTest {

    private final LineRepository lineRepository;

    public LineRepositoryTest(JdbcTemplate jdbcTemplate, DataSource dataSource,
                              NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.lineRepository = new LineRepository(jdbcTemplate, dataSource, namedParameterJdbcTemplate);
    }

    @Test
    @DisplayName("LineEntity 저장")
    void save() {
        final LineEntity lineEntity = new LineEntity("2호선", "bg-600-green");
        final LineEntity save = lineRepository.save(lineEntity);

        assertAll(
                () -> assertThat(save.getId()).isNotNull(),
                () -> assertThat(lineEntity.getName()).isEqualTo(save.getName()),
                () -> assertThat(lineEntity.getColor()).isEqualTo(save.getColor())
        );
    }

    @Test
    @DisplayName("LineEntity 전체 조회")
    void findAll() {
        final LineEntity lineEntity = new LineEntity("2호선", "bg-600-green");
        final LineEntity lineEntity2 = new LineEntity("3호선", "bg-600-blue");
        lineRepository.save(lineEntity);
        lineRepository.save(lineEntity2);

        final List<LineEntity> lines = lineRepository.findAll();

        assertThat(lines).extracting("name").containsAll(List.of(lineEntity.getName(), lineEntity2.getName()));
    }

    @Test
    @DisplayName("LineEntity 단 건 조회")
    void findById() {
        final LineEntity lineEntity = new LineEntity("2호선", "bg-600-green");
        final LineEntity saved = lineRepository.save(lineEntity);

        final Optional<LineEntity> found = lineRepository.findById(saved.getId());
        final LineEntity savedEntity = found.orElseThrow(NoSuchElementException::new);

        assertAll(
                () -> assertThat(savedEntity.getName()).isEqualTo(saved.getName()),
                () -> assertThat(savedEntity.getColor()).isEqualTo(saved.getColor())
        );
    }

    @Test
    @DisplayName("LineEntity 업데이트")
    void updateById() {
        final LineEntity lineEntity = new LineEntity("2호선", "bg-600-green");
        final LineEntity saved = lineRepository.save(lineEntity);

        final LineEntity newLineEntity = new LineEntity(saved.getId(), "3호선", "bg-700-blue");
        final long affectedRow = lineRepository.updateById(newLineEntity);

        final Optional<LineEntity> updated = lineRepository.findById(saved.getId());

        assertAll(
                () -> assertThat(affectedRow).isOne(),
                () -> assertThat(updated).isPresent(),
                () -> assertThat(updated.get().getName()).isEqualTo(newLineEntity.getName()),
                () -> assertThat(updated.get().getColor()).isEqualTo(newLineEntity.getColor())
        );
    }

    @Test
    @DisplayName("LineEntity 단 건 삭제")
    void deleteById() {
        final LineEntity lineEntity = new LineEntity("2호선", "bg-600-green");
        final LineEntity saved = lineRepository.save(lineEntity);
        final Long id = saved.getId();

        final Optional<LineEntity> before = lineRepository.findById(id);
        lineRepository.deleteById(id);
        final Optional<LineEntity> after = lineRepository.findById(id);

        assertAll(
                () -> assertThat(before).isPresent(),
                () -> assertThat(after).isNotPresent()
        );
    }
}
