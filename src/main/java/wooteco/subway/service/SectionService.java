package wooteco.subway.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.dto.SectionRequest;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class SectionService {
    private final SectionDao sectionDao;

    public SectionService(SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    @Transactional(readOnly = false)
    public void insertSectionInLine(Long lineId, SectionRequest sectionRequest) {
        Section section = Section.of(lineId,
                sectionRequest.getUpStationId(),
                sectionRequest.getDownStationId(),
                sectionRequest.getDistance());

        List<Section> sectionsByLineId = sectionDao.findAllByLineId(section.getLineId());
        Sections sections = new Sections(sectionsByLineId);

        sections.validateInsert(section);

        if (sections.isInsertSectionInEitherEndsOfLine(section)) {
            sectionDao.save(section);
            return;
        }
        insertSectionInMiddleOfLine(section, sections);
        sectionDao.save(section);
    }

    private void insertSectionInMiddleOfLine(Section section, Sections sections) {
        Section updateSection = sections.getSectionNeedToBeUpdatedForInsert(section);
        sectionDao.update(updateSection);
    }

    @Transactional(readOnly = false)
    public void delete(Long lineId, Long stationId) {
        List<Section> sectionsByLineId = sectionDao.findAllByLineId(lineId);
        Sections sections = new Sections(sectionsByLineId);
        sections.validateDeletable();

        Optional<Section> upSection = sections.getSectionFromUpToDownStationMapByStationId(stationId);
        Optional<Section> downSection = sections.getSectionFromDownToUpStationMapByStationId(stationId);

        if (upSection.isPresent() && downSection.isPresent()) {
            sectionDao.save(Section.of(lineId,
                    downSection.get(),
                    upSection.get()));
        }

        upSection.ifPresent(section -> sectionDao.delete(section.getId()));
        downSection.ifPresent(section -> sectionDao.delete(section.getId()));
    }
}
