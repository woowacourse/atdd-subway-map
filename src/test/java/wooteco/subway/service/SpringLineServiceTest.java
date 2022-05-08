package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import javax.sql.DataSource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.TestConstructor.AutowireMode;
import wooteco.subway.dao.LineRepository;
import wooteco.subway.domain.Line;
import wooteco.subway.exception.LineColorDuplicateException;
import wooteco.subway.exception.LineNameDuplicateException;
import wooteco.subway.service.dto.LineServiceRequest;

@JdbcTest
@TestConstructor(autowireMode = AutowireMode.ALL)
@DisplayName("노선 서비스")
class SpringLineServiceTest {

    private static final LineServiceRequest LINE_FIXTURE = new LineServiceRequest("2호선", "bg-color-600");
    private static final LineServiceRequest LINE_FIXTURE2 = new LineServiceRequest("3호선", "bg-color-700");
    private static final LineServiceRequest LINE_FIXTURE3 = new LineServiceRequest("4호선", "bg-color-800");

    private final LineService lineService;

    public SpringLineServiceTest(JdbcTemplate jdbcTemplate, DataSource dataSource,
                                 NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.lineService = new SpringLineService(
                new LineRepository(jdbcTemplate, dataSource, namedParameterJdbcTemplate));
    }

    @Nested
    @DisplayName("새로운 노선을 저장할 때")
    class SaveTest {

        @DisplayName("노선 이름이 중복되지 않으면 저장할 수 있다")
        @Test
        void saveSuccessIfNotExists() {
            assertThatCode(() -> lineService.save(LINE_FIXTURE))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("노선 이름이 중복되면 예외가 발생한다")
        void saveWithDuplicateNameShouldFail() {
            // given
            lineService.save(LINE_FIXTURE);

            final LineServiceRequest nameDuplicate = new LineServiceRequest(
                    LINE_FIXTURE.getName(),
                    "new" + LINE_FIXTURE.getColor()
            );

            // when & then
            assertThatThrownBy(() -> lineService.save(nameDuplicate))
                    .isInstanceOf(LineNameDuplicateException.class)
                    .hasMessage("이미 존재하는 노선 이름입니다 : " + LINE_FIXTURE.getName());
        }

        @Test
        @DisplayName("노선 색상이 중복되면 예외가 발생한다")
        void saveWithDuplicateColorShouldFail() {
            // given
            lineService.save(LINE_FIXTURE);

            final LineServiceRequest colorDuplicate = new LineServiceRequest(
                    "new" + LINE_FIXTURE.getName(),
                    LINE_FIXTURE.getColor()
            );

            // when & then
            assertThatThrownBy(() -> lineService.save(colorDuplicate))
                    .isInstanceOf(LineColorDuplicateException.class)
                    .hasMessage("이미 존재하는 노선 색상입니다 : " + LINE_FIXTURE.getColor());
        }
    }

    @Test
    @DisplayName("전체 지하철 노선을 조회할 수 있다")
    void findAll() {
        // given
        final List<String> expected = List.of(LINE_FIXTURE.getName(), LINE_FIXTURE2.getName(), LINE_FIXTURE3.getName());

        // when
        lineService.save(LINE_FIXTURE);
        lineService.save(LINE_FIXTURE2);
        lineService.save(LINE_FIXTURE3);

        // then
        assertThat(lineService.findAll()).extracting("name").isEqualTo(expected);
    }

    @Test
    @DisplayName("아이디로 지하철 노선을 조회할 수 있다")
    void findById() {
        // given
        final Line line = lineService.save(LINE_FIXTURE);

        // when
        final Line found = lineService.findById(line.getId());

        // then
        assertThat(line).isEqualTo(found);
    }

    @Nested
    @DisplayName("노선을 업데이트 할 때")
    class update {

        @Test
        @DisplayName("이름과 색상이 중복되지 않을 경우 수정 가능하다")
        void updateSuccessWithNotDuplicateNameAndColor() {
            // given
            final Line line = lineService.save(LINE_FIXTURE);
            final Long id = line.getId();

            // when
            final LineServiceRequest lineRequest = new LineServiceRequest("22호선", "bg-color-777");
            lineService.update(id, lineRequest);
            final Line updated = lineService.findById(id);

            // then
            assertAll(
                    () -> assertThat(updated.getId()).isEqualTo(id),
                    () -> assertThat(updated.getName()).isEqualTo("22호선"),
                    () -> assertThat(updated.getColor()).isEqualTo("bg-color-777")
            );
        }

        @Test
        @DisplayName("아이디가 존재하지 않을 경우 수정 불가능하다")
        void updateWithNotExistIdShouldFail() {
            // given
            final LineServiceRequest notExistId = new LineServiceRequest("22호선", "bg-color-777");
            lineService.update(1L, notExistId);
            final Line updated = lineService.findById(notExistId.getId());

            // then
            assertAll(
                    () -> assertThat(updated.getId()).isEqualTo(notExistId.getId()),
                    () -> assertThat(updated.getName()).isEqualTo("22호선"),
                    () -> assertThat(updated.getColor()).isEqualTo("bg-color-777")
            );
        }

        @Test
        @DisplayName("이름이 중복될 경우 수정 불가능하다")
        void updateWithDuplicateNameShouldFail() {
            // given
            final Line line = lineService.save(LINE_FIXTURE);
            final Long id = line.getId();

            // when
            final LineServiceRequest duplicateNameRequest = new LineServiceRequest(
                    LINE_FIXTURE.getName(),
                    "new" + LINE_FIXTURE.getColor()
            );

            // then
            assertThatThrownBy(() -> lineService.update(id, duplicateNameRequest))
                    .isInstanceOf(LineNameDuplicateException.class)
                    .hasMessage("이미 존재하는 노선 이름입니다 : " + duplicateNameRequest.getName());
        }

        @Test
        @DisplayName("색상이 중복될 경우 수정 불가능하다")
        void updateWithDuplicateColorShouldFail() {
            // given
            final Line line = lineService.save(LINE_FIXTURE);
            final Long id = line.getId();

            // when
            final LineServiceRequest lineRequest = new LineServiceRequest("22호선", "bg-color-777");
            lineService.update(id, lineRequest);
            final Line updated = lineService.findById(id);

            // then
            assertAll(
                    () -> assertThat(updated.getId()).isEqualTo(id),
                    () -> assertThat(updated.getName()).isEqualTo("22호선"),
                    () -> assertThat(updated.getColor()).isEqualTo("bg-color-777")
            );
        }
    }

    @Test
    @DisplayName("아이디로 지하철노선을 삭제할 수 있다")
    void deleteById() {
        // given
        final Line line = lineService.save(LINE_FIXTURE);
        final List<Line> lines = lineService.findAll();

        // when
        lineService.deleteById(line.getId());
        final List<Line> afterDelete = lineService.findAll();

        // then
        assertAll(
                () -> assertThat(lines).isNotEmpty(),
                () -> assertThat(afterDelete).isEmpty()
        );
    }
}
