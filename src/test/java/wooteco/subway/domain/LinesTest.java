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
}
