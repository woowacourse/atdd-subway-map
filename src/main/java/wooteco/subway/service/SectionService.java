package wooteco.subway.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.domain.SimpleSection;
import wooteco.subway.exception.section.DeleteSectionIsNotPermittedException;
import wooteco.subway.exception.section.NoneOfSectionIncludedInLine;
import wooteco.subway.exception.section.SectionDistanceMismatchException;
import wooteco.subway.exception.section.SectionsAlreadyExistException;

import java.util.List;
import java.util.Optional;

@Service
public class SectionService {

    private final SectionDao sectionDao;

    public SectionService(SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    public void insert(Long lineId, SimpleSection section) {
        sectionDao.insert(new Section(lineId, section));
    }

    public List<Section> findAllByLineId(Long id) {
        return sectionDao.findAllByLineId(id);
    }

    public void validateEndStationsAreIncluded(Long lineId, SimpleSection section) {
        if (sectionDao.isIncludeAllEndStations(new Section(lineId, section))) {
            throw new SectionsAlreadyExistException();
        }
    }

    public int getSectionCountsByLineId(Long lineId) {
        return sectionDao.countsByLineId(lineId);
    }

    @Transactional
    public void insertSections(Long lineId, SimpleSection simpleSection) {
        final Optional<Section> optionalSectionConversed =
                sectionDao.findOneIfIncludeConversed(new Section(lineId, simpleSection));
        if (optionalSectionConversed.isPresent()) {
            sectionDao.insert(new Section(lineId, simpleSection));
            return;
        }

        final Section section = sectionDao.findOneIfInclude(new Section(lineId, simpleSection))
                .orElseThrow(NoneOfSectionIncludedInLine::new);
        validateCanBeInserted(section, simpleSection);
        final SimpleSection updatedSection = updateOriginalSectionToMakeOneLine(section, simpleSection);
        sectionDao.update(new Section(lineId, updatedSection)); // 기존 섹션을 업데이트함. 삽입된 구간을 포함하여.
        sectionDao.insert(new Section(lineId, simpleSection)); // 추가된 섹션을 삽입함.
    }

    private SimpleSection updateOriginalSectionToMakeOneLine(Section section, SimpleSection insertedSection) {
        if (section.isEqualUpStationId(insertedSection)) {
            return new SimpleSection(insertedSection.getDownStationId(),
                    section.getDownStationId(),
                    updateDistance(section, insertedSection));
        }

        // downStation 이 같다면
        return new SimpleSection(section.getUpStationId(),
                section.getUpStationId(),
                updateDistance(section, insertedSection));
    }

    private int updateDistance(Section section, SimpleSection simpleSection) {
        final int maxDistance = section.calculateMaxDistance(simpleSection);
        final int minDistance = section.calculateMinDistance(simpleSection);
        return maxDistance - minDistance;
    }

    private void validateCanBeInserted(Section section, SimpleSection simpleSection) {
        if (!section.isLongerDistanceThan(simpleSection)) {
            throw new SectionDistanceMismatchException();
        }
    }

    public void validateSectionCount(Long lineId) {
        final int counts = getSectionCountsByLineId(lineId);
        if (counts <= 1) {
            throw new DeleteSectionIsNotPermittedException();
        }
    }

    public void deleteAllSectionByLineId(Long lineId) {
        sectionDao.deleteAllByLineId(lineId);
    }

    public void deleteSection(Long lineId, Long stationId) {
        final List<Section> sections = sectionDao.findAllSectionsIncludeStationId(lineId, stationId);
        if (sections.isEmpty()) {
            throw new DeleteSectionIsNotPermittedException();
        }
        Section section = adjustSection(lineId, new Sections(sections));
        sectionDao.delete(section);
    }

    private Section adjustSection(Long lineId, Sections sections) {
        if (sections.hasOnlyOneSection()) {
            return sections.section(0);
        }
        sectionDao.update(new Section(lineId, sections.updateSectionToOneLine()));
        return sections.section(1);
    }
}
