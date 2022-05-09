package wooteco.subway.service;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import wooteco.subway.dao.SectionDao;

@ExtendWith(MockitoExtension.class)
public class ServiceTest {
    @Mock
    protected SectionDao sectionDao;
}
