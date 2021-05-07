package wooteco.subway.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.domain.line.Line;
import wooteco.subway.exception.ExceptionStatus;
import wooteco.subway.exception.SubwayException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@JdbcTest
class LineDaoTest {

    private LineDao lineDao;
    private long testLineId;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        lineDao = new LineDao(jdbcTemplate);
        String schemaQuery = "create table if not exists LINE (id bigint auto_increment not null, name varchar(255) " +
                "not null unique, color varchar(20) not null, primary key(id))";
        jdbcTemplate.execute(schemaQuery);
        testLineId = lineDao.save(new Line("testLine", "white"));
    }

    @Nested
    @DisplayName("save 메서드는")
    class Describe_save {

        @Nested
        @DisplayName("이름이 중복되지 않은 엔티티의 경우")
        class Context_with_unique_name {

            @DisplayName("노선을 정상적으로 등록한다.")
            @Test
            void save() {
                Line line = new Line("testLine2", "black");
                lineDao.save(line);

                List<Line> lines = lineDao.findAll();

                assertThat(lines).hasSize(2);
            }
        }

        @Nested
        @DisplayName("이름이 중복된 경우")
        class Context_with_duplicated_name {

            @DisplayName("노선 등록에 실패한다.")
            @Test
            void cannotSave() {
                Line line = new Line("testLine2", "black");
                lineDao.save(line);

                assertThatCode(() -> lineDao.save(line))
                        .isInstanceOf(SubwayException.class)
                        .hasMessage(ExceptionStatus.DUPLICATED_NAME.getMessage());
            }
        }
    }

    @Nested
    @DisplayName("findById 메서드는")
    class Describe_findById {

        @Nested
        @DisplayName("id에 해당하는 엔티티가 존재하는 경우")
        class Context_with_valid_id {

            @DisplayName("노선 조회에 성공한다.")
            @Test
            void findById() {
                Line line = lineDao.findById(testLineId)
                        .get();

                assertThat(line).isEqualTo(new Line(testLineId, "testLine", "white"));
            }
        }

        @Nested
        @DisplayName("id에 해당하는 엔티티가 없는 경우")
        class Context_with_invalid_id {

            @DisplayName("노선 조회에 실패한다.")
            @Test
            void cannotFindById() {
                Optional<Line> line = lineDao.findById(6874);

                assertThat(line).isEmpty();
            }
        }
    }

    @Nested
    @DisplayName("update 메서드는")
    class Describe_update {

        @Nested
        @DisplayName("id에 해당하는 엔티티가 존재하는 경우")
        class Context_with_valid_id {

            @DisplayName("노선 수정에 성공한다.")
            @Test
            void update() {
                lineDao.update(testLineId, "changedName", "grey");

                Line line = lineDao.findById(testLineId)
                        .get();

                assertThat(line.getName()).isEqualTo("changedName");
                assertThat(line.getColor()).isEqualTo("grey");
            }

            @DisplayName("수정하려는 이름이 이미 존재하면 예외가 발생한다.")
            @Test
            void duplicatedKey() {
                String duplicatedName = "dup";
                lineDao.save(new Line(duplicatedName, "grey"));

                assertThatCode(() -> lineDao.update(testLineId, duplicatedName, "white"))
                        .isInstanceOf(SubwayException.class)
                        .hasMessage(ExceptionStatus.DUPLICATED_NAME.getMessage());
            }
        }

        @Nested
        @DisplayName("id에 해당하는 엔티티가 존재하지 않는 경우")
        class Context_with_invalid_id {

            @DisplayName("노선 수정에 실패한다.")
            @Test
            void cannotUpdate() {
                assertThatCode(() -> lineDao.update(6874, "rrr", "yellow"))
                        .isInstanceOf(SubwayException.class)
                        .hasMessage(ExceptionStatus.ID_NOT_FOUND.getMessage());
            }
        }
    }

    @Nested
    @DisplayName("deleteById 메서드는")
    class Describe_deleteById {

        @Nested
        @DisplayName("id에 해당하는 엔티티가 존재하는 경우")
        class Context_with_valid_id {

            @DisplayName("노선 삭제에 성공한다.")
            @Test
            void deleteById() {
                long id = lineDao.save(new Line("dummy", "blue"));
                int beforeLineCounts = lineDao.findAll().size();

                lineDao.deleteById(id);
                int afterLineCounts = lineDao.findAll().size();

                assertThat(beforeLineCounts - 1).isEqualTo(afterLineCounts);
            }
        }

        @Nested
        @DisplayName("id에 해당하는 엔티티가 존재하지 않는 경우")
        class Context_with_invalid_id {

            @DisplayName("노선 삭제에 실패한다.")
            @Test
            void cannotUpdate() {
                assertThatCode(() -> lineDao.deleteById(6874))
                        .isInstanceOf(SubwayException.class)
                        .hasMessage(ExceptionStatus.ID_NOT_FOUND.getMessage());
            }
        }
    }
}
