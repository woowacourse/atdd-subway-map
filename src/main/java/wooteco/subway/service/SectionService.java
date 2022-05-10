package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.entity.SectionEntity;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.ui.dto.SectionRequest;

@Service
public class SectionService {

    private final SectionDao sectionDao;

    public SectionService(SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    public Long save(SectionRequest sectionRequest) {
        Section section = new Section(sectionRequest.getLineId(), sectionRequest.getUpStationId(),
            sectionRequest.getDownStationId(), sectionRequest.getDistance());

        List<SectionEntity> sectionEntities = sectionDao.findByLineId(sectionRequest.getLineId());
        Sections sections = getSections(sectionEntities);

        if (sections.isForkedRoad(section)) {
            return updateForkedRoad(sectionRequest, sectionEntities);
        }

        return sectionDao.save(section);
    }

    private Sections getSections(List<SectionEntity> sectionEntities) {
        List<Section> sections = sectionEntities.stream()
            .map(i -> new Section(i.getLine_id(), i.getUpStationId(), i.getDownStationId(),
                i.getDistance()))
            .collect(Collectors.toList());
        return new Sections(sections);
    }

    private Long updateForkedRoad(SectionRequest sectionRequest, List<SectionEntity> sectionEntities) {
        SectionEntity sectionEntity = sectionEntities.stream()
            .filter(i -> i.getUpStationId().equals(sectionRequest.getUpStationId()))
            .findAny()
            .orElseThrow(IllegalArgumentException::new);

        if (sectionEntity.getDistance() <= sectionRequest.getDistance()) {
            throw new IllegalArgumentException("등록할 구간의 길이가 기존 역 사이의 길이보다 길거나 같으면 안됩니다.");
        }

        sectionDao.update(sectionEntity.getId(), sectionRequest.getDownStationId(),
            sectionRequest.getDistance());

        return sectionDao.save(
            new Section(sectionRequest.getLineId(), sectionRequest.getUpStationId(),
                sectionRequest.getDownStationId(),
                sectionEntity.getDistance() - sectionRequest.getDistance()));
    }
}
