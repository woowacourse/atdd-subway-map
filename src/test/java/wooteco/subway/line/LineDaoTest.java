package wooteco.subway.line;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.station.Station;
import wooteco.subway.station.StationService;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
public class LineDaoTest {

    @Autowired
    private LineDao lineDao;

    private Long mockUpStationId = 1L;
    private Long mockDownStationId = 2L;
    private Long savedLineId;

    @BeforeEach
    private void initLine(){
        savedLineId = lineDao.save("코기선", "black", mockUpStationId, mockDownStationId);
    }

    @DisplayName("노선의 상행 종점을 조회한다.")
    @Test
    public void findUpStation(){
        final Long upStationId = lineDao.findUpStationId(savedLineId);
        assertThat(upStationId).isEqualTo(mockUpStationId);
    }

    @DisplayName("노선의 하행 종점을 조회한다.")
    @Test
    public void findDownStation(){
        final Long downStationId = lineDao.findDownStationId(savedLineId);
        assertThat(downStationId).isEqualTo(mockDownStationId);
    }
}
