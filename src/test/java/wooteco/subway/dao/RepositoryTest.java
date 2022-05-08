package wooteco.subway.dao;

import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.context.annotation.Import;

@Import({
        LineRepository.class,
        StationRepository.class
})
@DataJdbcTest
public class RepositoryTest {
}
