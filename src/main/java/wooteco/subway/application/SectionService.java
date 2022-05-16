package wooteco.subway.application;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.exception.constant.SectionNotRegisterException;

import java.util.LinkedList;
import java.util.List;

@Service
@Transactional
@AllArgsConstructor
public class SectionService {

    private final SectionDao sectionDao;

    public long createSection(Section section) {
        List<Section> found = sectionDao.findByLineId(section.getLineId());
        Sections sections = new Sections(found);
        Section sectionWithSameUpStation = sections.getSectionWithSameUpStation(section.getUpStationId());
        Section sectionWithSameDownStation = sections.getSectionWithSameDownStation(section.getDownStationId());
        if(sections.isTerminalRegistration(section)) {
            return sectionDao.save(section);
        }
        if(sections.isAddStationInMiddle(section.getDistance(), sectionWithSameUpStation)) {
            return addDownStationInMiddle(section, sectionWithSameUpStation);
        }
        if(sections.isAddStationInMiddle(section.getDistance(), sectionWithSameDownStation)) {
            return addUpStationInMiddle(section, sectionWithSameDownStation);
        }
        sections.validateCreateSection(sectionWithSameUpStation, sectionWithSameDownStation);
        throw new SectionNotRegisterException();
    }


    @Transactional(readOnly = true)
    public LinkedList<Long> findSortedStationIds(long lineId) {
        List<Section> foundSections = sectionDao.findByLineId(lineId);
        Sections sections = new Sections(foundSections);
        return sections.getSorted();
    }

    public void removeSection(long lineId, long stationId) {
        List<Section> found = sectionDao.findByLineId(lineId);
        Sections sections = new Sections(found);
        sections.validateCanDeleteSection();

        Section upperSection = sections.getUpperSection(stationId);
        Section lowerSection = sections.getLowerSection(stationId);

        sections.joinSection(lowerSection, upperSection);

        sectionDao.update(lowerSection);
        sectionDao.deleteSection(upperSection);
    }

    private long addDownStationInMiddle(Section sectionToAdd, Section existingSection) {
        long createdSectionId = sectionDao.save(sectionToAdd);

        existingSection.setUpStationId(sectionToAdd.getDownStationId());
        existingSection.setDistance(existingSection.getDistance() - sectionToAdd.getDistance());
        sectionDao.update(existingSection);

        return createdSectionId;
    }

    private Long addUpStationInMiddle(Section sectionToAdd, Section existingSection) {
        long createdSectionId = sectionDao.save(sectionToAdd);

        existingSection.setDownStationId(sectionToAdd.getUpStationId());
        existingSection.setDistance(existingSection.getDistance() - sectionToAdd.getDistance());
        sectionDao.update(existingSection);

        return createdSectionId;
    }
}
