package wooteco.subway.service;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import wooteco.subway.dao.LineDao;

@ExtendWith(MockitoExtension.class)
class ServiceTest {

    @Mock
    protected LineDao lineDao;
}
