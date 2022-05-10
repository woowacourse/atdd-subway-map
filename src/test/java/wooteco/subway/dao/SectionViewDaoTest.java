package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import wooteco.subway.entity.SectionViewEntity;
import wooteco.subway.entity.StationEntity;

@SuppressWarnings("NonAsciiCharacters")
class SectionViewDaoTest extends DaoTest {

    private static final StationEntity STATION1 = new StationEntity(1L, "이미 존재하는 역 이름");
    private static final StationEntity STATION2 = new StationEntity(2L, "선릉역");
    private static final StationEntity STATION3 = new StationEntity(3L, "잠실역");

    @Autowired
    private SectionViewDao dao;

    @Test
    void findAllByLineId_메서드는_lineId에_해당하는_모든_구간_데이터를_조회() {
        List<SectionViewEntity> actual = dao.findAllByLineId(1L);

        List<SectionViewEntity> expected = List.of(
                new SectionViewEntity(1L, STATION1, STATION2, 10),
                new SectionViewEntity(1L, STATION2, STATION3, 5));

        assertThat(actual).isEqualTo(expected);
    }
}
