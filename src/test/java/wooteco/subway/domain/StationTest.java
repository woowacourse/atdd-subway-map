package wooteco.subway.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("[도메인] Station")
class StationTest {

    @Test
    @DisplayName("id가 같은지 확인")
    void isSameId() {
        Station sample = Station.create(1L, "sample");
        Station mock = Station.create(1L, "mock");

        assertTrue(sample.isSameId(mock.getId()));
    }

    @Test
    @DisplayName("이름이 같은지 확인")
    void isSameName() {
        Station sample = Station.create(1L, "name");
        Station mock = Station.create(3L, "name");

        assertTrue(sample.isSameName(mock.getName()));
    }
}