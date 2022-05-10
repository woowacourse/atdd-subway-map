package wooteco.subway.dao;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import wooteco.subway.controller.AcceptanceTest;
import wooteco.subway.dao.entity.LineEntity;
import wooteco.subway.dao.entity.SectionEntity;
import wooteco.subway.dao.entity.StationEntity;

public class SectionDaoTest extends AcceptanceTest {

    @Autowired
    private SectionDao sectionDao;
    @Autowired
    private StationDao stationDao;
    @Autowired
    private LineDao lineDao;

    @DisplayName("구간 정보를 저장한다.")
    @Test
    void save() {
        StationEntity jamsil = stationDao.save(new StationEntity("잠실역"));
        StationEntity seonreong = stationDao.save(new StationEntity("선릉역"));
        LineEntity twoLine = lineDao.save(new LineEntity("2호선", "연두색"));

        SectionEntity expected = new SectionEntity(jamsil.getId(), seonreong.getId(), twoLine.getId(), 10);
        SectionEntity actual = sectionDao.save(expected);

        Assertions.assertThat(actual).isEqualTo(expected);
    }
}
