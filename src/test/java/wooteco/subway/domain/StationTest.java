package wooteco.subway.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("[도메인] Station")
class StationTest {

    @Test
    @DisplayName("id가 같은지 확인")
    void isSameId() {
        Station sample = new Station(1L, "sample");
        Station mock = new Station(1L, "mock");

        assertTrue(sample.isSameId(mock.getId()));
    }

    @Test
    @DisplayName("이름이 같은지 확인")
    void isSameName() {
        Station sample = new Station(1L, "name");
        Station mock = new Station(3L, "name");

        assertTrue(sample.isSameName(mock.getName()));
    }
}