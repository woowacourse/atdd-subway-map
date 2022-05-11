package wooteco.subway.application;

import static wooteco.subway.application.ServiceFixture.강남역;
import static wooteco.subway.application.ServiceFixture.역삼역;

import java.util.List;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;

public class FakeSectionDao implements SectionDao<Section> {
    @Override
    public Section save(Section section) {
        return new Section(new Line(), section.getUpStation(), section.getDownStation(), 5);
    }

    @Override
    public int deleteSectionById(List<Long> ids) {
        return ids.size();
    }

    @Override
    public List<Section> findByLineId(Long lineId) {
        return List.of(new Section(new Line(), 강남역, 역삼역, 5));
    }

    @Override
    public int updateUpStationSection(Long lineId, Long originUpStationId, Long upStationId, int distance) {
        return 0;
    }

    @Override
    public int countByLineId(Long lineId) {
        return Math.toIntExact(lineId);
    }

    @Override
    public List<Section> findByLineIdAndStationId(Long lineId, Long stationId) {
        return List.of(new Section(2L, new Line(), 강남역, 역삼역, 5));
    }

    @Override
    public int updateDownStationSection(Long lineId, Long id, Long downStationId, int distance) {
        return 0;
    }
}
