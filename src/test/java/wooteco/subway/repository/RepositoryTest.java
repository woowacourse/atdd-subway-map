package wooteco.subway.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.context.annotation.Import;

@Import({
        LineRepository.class,
        StationRepository.class,
        SectionRepository.class
})
@DataJdbcTest
public class RepositoryTest {

    @Autowired
    protected StationRepository stationRepository;

    @Autowired
    protected LineRepository lineRepository;

    @Autowired
    protected SectionRepository sectionRepository;

}
