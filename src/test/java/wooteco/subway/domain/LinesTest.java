package wooteco.subway.domain;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import wooteco.subway.exception.ClientException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LinesTest {

    @Test
    @DisplayName("중복된 지하철 노선 저장시 예외")
    void duplicateName() {
        Lines lines = new Lines(new ArrayList<>(List.of(new Line("1호선", "blue"))));

        assertThatThrownBy(() -> lines.add(new Line("1호선", "red")))
                .isInstanceOf(ClientException.class)
                .hasMessageContaining("이미 등록된 지하철노선입니다.");
    }

    @Test
    @DisplayName("존재하지 않는 노선 검색시 예외")
    void checkExistLine() {
        Lines lines = new Lines(new ArrayList<>(List.of(new Line(1L, "1호선", "blue"),
                new Line(2L, "2호선", "green"))));

        assertThatThrownBy(() -> lines.validateExist(3L))
                .isInstanceOf(ClientException.class)
                .hasMessageContaining("존재하지 않는 노선입니다.");
    }

    @Test
    @DisplayName("노선에 등록되어 있는 지하철 역 삭제시 예외")
    void checkDeletePossible() {
        Lines lines = new Lines(new ArrayList<>(List.of(new Line(1L, "1호선", "blue"),
                new Line(2L, "2호선", "green"))));

        assertThatThrownBy(() -> lines.validateCanDelete(2L))
                .isInstanceOf(ClientException.class)
                .hasMessageContaining("노선에 등록되어 있는 역은 제거할 수 없습니다.");
    }
}
