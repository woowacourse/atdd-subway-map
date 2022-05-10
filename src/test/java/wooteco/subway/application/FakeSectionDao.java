package wooteco.subway.application;

import static wooteco.subway.application.ServiceFixture.강남역;
import static wooteco.subway.application.ServiceFixture.역삼역;

import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;

public class FakeSectionDao implements SectionDao<Section> {
    @Override
    public Section save(Section Section) {
        return new Section(new Line(), 강남역, 역삼역, 5);
    }

    @Override
    public int deleteSection(Long lineId, Long stationId) {

        throw new IllegalArgumentException("사용하지 않는 메서드입니다");
    }
}
