package wooteco.subway.service;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;

@ExtendWith(MockitoExtension.class)
abstract class ServiceTest {

    @Mock
    protected StationDao stationDao;

    @Mock
    protected LineDao lineDao;

    @Mock
    protected SectionDao sectionDao;
}
