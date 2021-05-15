package wooteco.subway.line.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import wooteco.subway.line.domain.LineRepository;
import wooteco.subway.line.exception.LineNotFoundException;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@SpringBootTest
public class LineRepositoryTest {

    @Autowired
    private LineRepository lineRepository;

    @Test
    @DisplayName("라인 ID가 DB에 존재하지 않을 때 예외")
    public void throwExceptionWhenLineNotFound() {
        assertThatExceptionOfType(LineNotFoundException.class).isThrownBy(() -> {
            lineRepository.findById(1L);
        }).withMessageContaining("해당 라인을 찾을 수 없습니다.");
    }
}
