package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("식별자")
class IdTest {

    @DisplayName("식별자는 양수여야 한다.")
    @ParameterizedTest
    @ValueSource(longs = {-5, 0})
    void validateIdPositive(Long id) {
        assertThatThrownBy(() -> new Id(id))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("식별자는 양수여야 합니다.");
    }

    @DisplayName("식별자가 지정되지 않으면 임시값을 할당한다.")
    @Test
    void createWithoutId() {
        Long temporaryId = (new Id()).getId();
        assertThat(temporaryId).isEqualTo(0L);
    }

    @DisplayName("식별자를 비교한다.")
    @ParameterizedTest
    @CsvSource(value = {"1,1,true", "1,2,false"})
    void equals(Long id1, Long id2, boolean expected) {
        Id thisId = new Id(id1);
        Id otherId = new Id(id2);

        boolean actual = thisId.equals(otherId);
        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("식별자를 반환한다.")
    @ParameterizedTest
    @ValueSource(longs = {1})
    void getId(Long expected) {
        Long actual = (new Id(expected)).getId();
        assertThat(actual).isEqualTo(expected);
    }
}
