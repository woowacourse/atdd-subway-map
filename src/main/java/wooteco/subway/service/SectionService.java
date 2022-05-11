package wooteco.subway.service;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.dto.SectionRequest;

@Service
public class SectionService {

    private final SectionDao sectionDao;

    public SectionService(SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    public void connectNewSection(Long lineId, SectionRequest sectionRequest) {
        Section section = new Section(lineId, sectionRequest.getUpStationId(),
                sectionRequest.getDownStationId(), sectionRequest.getDistance());
        Sections previousSections = new Sections(sectionDao.findByLineId(lineId));
        Sections updatedSections = previousSections.connect(section);
        save(updatedSections.findDifferentSections(previousSections));
    }

    private void save(List<Section> updated) {
        saveNewSection(updated);
        updateChangedSection(updated);
    }

    private void updateChangedSection(final List<Section> updated) {
        final Optional<Section> changedSection = updated.stream()
                .filter(section -> !section.hasNoId())
                .findAny();
        changedSection.ifPresent(sectionDao::update);
    }

    private void saveNewSection(final List<Section> updated) {
        final Optional<Section> newSection = updated.stream()
                .filter(Section::hasNoId)
                .findAny();
        newSection.ifPresent(sectionDao::save);
    }

    public void deleteStation(Long lineId, Long stationId) {
        Sections previousSections = new Sections(sectionDao.findByLineId(lineId));
        Sections deletedSections = previousSections.delete(stationId);
        deletePrevious(previousSections, deletedSections);
        addChanged(previousSections, deletedSections);
    }

    private void addChanged(Sections previousSections, Sections deletedSections) {
        final List<Section> changed = deletedSections.findDifferentSections(previousSections);
        for (Section section : changed) {
            sectionDao.save(section);
        }
    }

    private void deletePrevious(Sections previousSections, Sections deletedSections) {
        final List<Section> deleted = previousSections.findDifferentSections(deletedSections);
        for (Section section : deleted) {
            sectionDao.deleteById(section.getId());
        }
    }
}
