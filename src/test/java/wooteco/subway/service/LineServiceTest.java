package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;

@Transactional
@SpringBootTest
@Sql("classpath:schema.sql")
class LineServiceTest {

    private final LineService lineService;

    @Autowired
    public LineServiceTest(LineService lineService) {
        this.lineService = lineService;
    }

    @DisplayName("노선을 저장한다.")
    @Test
    void save() {
        LineRequest lineRequest = new LineRequest("2호선", "green");

        LineResponse lineResponse = lineService.save(lineRequest);

        assertThat(lineResponse.getName()).isEqualTo(lineRequest.getName());
    }

    @DisplayName("같은 이름의 노선을 저장하는 경우 예외가 발생한다.")
    @Test
    void saveExistingName() {
        LineRequest lineRequest = new LineRequest("2호선", "green");

        lineService.save(lineRequest);

        assertThatThrownBy(() -> lineService.save(lineRequest))
                .isInstanceOf(DuplicateKeyException.class);
    }

    @DisplayName("모든 지하철 노선을 조회한다.")
    @Test
    void findAll() {
        LineRequest line1 = new LineRequest("2호선", "green");
        LineRequest line2 = new LineRequest("3호선", "orange");
        LineRequest line3 = new LineRequest("8호선", "pink");

        lineService.save(line1);
        lineService.save(line2);
        lineService.save(line3);

        assertThat(lineService.findAll().size()).isEqualTo(3);
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void findById() {
        LineRequest lineRequest = new LineRequest("2호선", "green");
        LineResponse lineResponse = lineService.save(lineRequest);

        LineResponse foundLine = lineService.findById(lineResponse.getId());

        assertThat(foundLine.getName()).isEqualTo(lineResponse.getName());
    }

    @DisplayName("존재하지 않는 지하철 노선을 조회할 경우 예외가 발생한다.")
    @Test
    void findNotExistingLine() {
        assertThatThrownBy(() -> lineService.findById(1L))
                .isInstanceOf(EmptyResultDataAccessException.class);
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void update() {
        LineRequest lineRequest = new LineRequest("2호선", "green");
        LineResponse lineResponse = lineService.save(lineRequest);

        LineResponse update = lineService.update(lineResponse.getId(), new LineRequest("3호선", "orange"));

        assertThat(update.getName()).isEqualTo("3호선");
    }

    @DisplayName("지하철 노선을 삭제한다.")
    @Test
    void deleteById() {
        LineRequest lineRequest = new LineRequest("2호선", "green");
        LineResponse lineResponse = lineService.save(lineRequest);

        lineService.deleteById(lineResponse.getId());

        assertThat(lineService.findAll().size()).isZero();
    }
}
