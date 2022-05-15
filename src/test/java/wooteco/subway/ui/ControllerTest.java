package wooteco.subway.ui;

import org.assertj.core.api.ClassAssert;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.bind.MethodArgumentNotValidException;

import static org.assertj.core.api.Assertions.assertThat;

public class ControllerTest {
    public ClassAssert checkValidException(MvcResult exception) {
        return assertThat(exception.getResolvedException().getClass()).isAssignableFrom(MethodArgumentNotValidException.class);
    }
}
