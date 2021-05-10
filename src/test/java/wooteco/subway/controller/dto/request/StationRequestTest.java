package wooteco.subway.controller.dto.request;

import static org.assertj.core.api.Assertions.assertThat;

import javax.validation.Validation;
import javax.validation.Validator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class StationRequestTest {

    @Test
    @DisplayName("Request 생성에 필요한 정보가 Null 일 때 예외처리")
    void nullCreateException() {
        // given
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

        // when
        StationRequest stationRequest = new StationRequest(null);

        //then
        assertThat(validator.validate(stationRequest).size()).isEqualTo(1);
    }
}