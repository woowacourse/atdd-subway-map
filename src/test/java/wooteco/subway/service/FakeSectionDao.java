package wooteco.subway.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.SectionEntity;

public class FakeSectionDao implements SectionDao {

    private final List<SectionEntity> sections = new ArrayList<>();
    private Long seq = 0L;

    @Override
    public Long save(Long lineId, Long upStationId, Long downStationId, int distance) {
        return ++seq;
    }

    @Override
    public List<SectionEntity> findByLineId(Long lineId) {
        return sections.stream()
            .filter(section -> section.getLineId().equals(lineId))
            .collect(Collectors.toList());
    }
}
