package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.entity.SectionEntity;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.ui.dto.SectionDeleteRequest;
import wooteco.subway.ui.dto.SectionRequest;

@Service
public class SectionService {

    private final SectionDao sectionDao;

    public SectionService(SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    public Long save(SectionRequest sectionRequest) {
        Section section = sectionRequest.toSection();

        List<SectionEntity> sectionEntities = sectionDao.findByLineId(sectionRequest.getLineId());
        Sections sections = getSections(sectionEntities);

        if (sections.isMiddleSection(section)) {
            sections.validateForkedLoad(section);
            return updateMiddleSection(section, sectionEntities);
        }

        return sectionDao.save(section);
    }

    private Sections getSections(List<SectionEntity> sectionEntities) {
        List<Section> sections = sectionEntities.stream()
            .map(i -> new Section(i.getLine_id(), i.getUpStationId()
                    , i.getDownStationId(), i.getDistance()))
            .collect(Collectors.toList());
        return new Sections(sections);
    }

    private Long updateMiddleSection(Section section, List<SectionEntity> sectionEntities) {
        SectionEntity upStationSectionEntity = sectionEntities.stream()
            .filter(i -> i.getUpStationId().equals(section.getUpStationId()))
            .findAny()
            .orElseThrow(IllegalArgumentException::new);

        if (upStationSectionEntity.getDistance() <= section.getDistance()) {
            throw new IllegalArgumentException("등록할 구간의 길이가 기존 역 사이의 길이보다 길거나 같으면 안됩니다.");
        }

        sectionDao.update(upStationSectionEntity.getId(), section.getDownStationId(),
            section.getDistance());

        return sectionDao.save(
            new Section(section.getLineId(), section.getUpStationId(),
                section.getDownStationId(),
                upStationSectionEntity.getDistance() - section.getDistance()));
    }

    public boolean removeSection(SectionDeleteRequest sectionDeleteRequest) {
        List<SectionEntity> sectionEntities = sectionDao.findByLineId(
            sectionDeleteRequest.getLineId());
        Sections sections = getSections(sectionEntities);

        validateRemoveSection(sectionDeleteRequest, sections);

        SectionEntity upStationSection = sectionEntities.stream()
            .filter(i -> i.getDownStationId().equals(sectionDeleteRequest.getStationId()))
            .findAny()
            .orElseThrow(IllegalArgumentException::new);

        SectionEntity deleteSectionStation = sectionEntities.stream()
            .filter(i -> i.getUpStationId().equals(sectionDeleteRequest.getStationId()))
            .findAny()
            .orElseThrow(IllegalArgumentException::new);

        int totalDistance = upStationSection.getDistance() + deleteSectionStation.getDistance();
        sectionDao.update(upStationSection.getId(), deleteSectionStation.getDownStationId(),
            totalDistance);
        return sectionDao.deleteById(deleteSectionStation.getId());
    }

    private void validateRemoveSection(SectionDeleteRequest sectionDeleteRequest,
        Sections sections) {
        if (!sections.isMiddleSection(new Section(sectionDeleteRequest.getStationId(),
            sectionDeleteRequest.getStationId()))) {
            throw new IllegalArgumentException("종점은 제거할 수 없습니다.");
        }
    }
}
