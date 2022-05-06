package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.domain.Line;

@SpringBootTest
@Transactional
class LineServiceTest {

    @Autowired
    private LineService lineService;

    @DisplayName("노선을 저장한다")
    @Test
    void 노선_저장() {
        Line line = new Line("1호선", "bg-darkblue-600");

        Line savedLine = lineService.save(line);

        assertAll(
                () -> assertThat(savedLine.getName()).isEqualTo(line.getName()),
                () -> assertThat(savedLine.getColor()).isEqualTo(line.getColor())
        );
    }

    @DisplayName("중복된 이름의 노선을 저장할 경우 예외가 발생한다")
    @Test
    void 중복된_노선_예외발생() {
        Line line = new Line("2호선", "bg-green-600");

        lineService.save(line);

        assertThatThrownBy(() -> lineService.save(line))
                .isInstanceOf(DuplicateKeyException.class)
                .hasMessageContaining("이미 존재하는 노선");
    }

    @DisplayName("모든 노선을 조회한다")
    @Test
    void 모든_노선_조회() {
        Line line1 = new Line("1호선", "bg-darkblue-600");
        Line line2 = new Line("2호선", "bg-green-600");

        lineService.save(line1);
        lineService.save(line2);

        assertThat(lineService.findAll().size()).isEqualTo(2);
    }

    @DisplayName("노선을 조회한다")
    @Test
    void 노선_조회() {
        Line line = new Line("3호선", "bg-orange-600");
        Line savedLine = lineService.save(line);

        Line foundLine = lineService.findById(savedLine.getId());

        assertAll(
                () -> assertThat(foundLine.getName()).isEqualTo(line.getName()),
                () -> assertThat(foundLine.getColor()).isEqualTo(line.getColor())
        );
    }

    @DisplayName("존재하지 않는 노선을 조회할 시 예외가 발생한다")
    @Test
    void 존재하지않는_노선_조회_예외발생() {
        assertThatThrownBy(() -> lineService.findById(1L))
                .isInstanceOf(EmptyResultDataAccessException.class)
                .hasMessageContaining("존재하지 않는 노선");
    }

    @DisplayName("노선을 업데이트한다")
    @Test
    void 노선_업데이트() {
        Line line = new Line("4호선", "bg-purple-600");
        Line newLine = new Line("4호선", "bg-skyblue-600");
        Line savedLine = lineService.save(line);

        Line updatedLine = lineService.update(savedLine.getId(), newLine);

        assertAll(
                () -> assertThat(updatedLine.getName()).isEqualTo(newLine.getName()),
                () -> assertThat(updatedLine.getColor()).isEqualTo(newLine.getColor())
        );
    }

    @DisplayName("중복된 노선 이름으로 업데이트 시 예외가 발생한다")
    @Test
    void 중복노선_업데이트_예외발생() {
        lineService.save(new Line("5호선", "bg-purple-600"));
        Line savedLine = lineService.save(new Line("6호선", "bg-brown-600"));

        Line newLine = new Line("5호선", "bg-brown-600");

        assertThatThrownBy(() -> lineService.update(savedLine.getId(), newLine))
                .isInstanceOf(DuplicateKeyException.class)
                .hasMessageContaining("이미 존재하는 노선");
    }

    @DisplayName("노선 삭제")
    @Test
    void 노선_삭제() {
        Line line = lineService.save(new Line("신분당선", "bg-red-600"));

        lineService.deleteById(line.getId());

        assertThat(lineService.findAll().size()).isEqualTo(0);
    }
}