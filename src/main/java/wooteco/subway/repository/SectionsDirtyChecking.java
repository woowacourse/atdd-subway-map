package wooteco.subway.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.Section;

@Component
public class SectionsDirtyChecking {

    private List<Section> sections;
    private SectionDao sectionDao;

    public SectionsDirtyChecking(SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    public void setInitSections(List<Section> sections) {
        this.sections = new ArrayList<>(sections);
    }

    public void dirtyChecking(List<Section> changedSections) {
        for (Section changedSection : changedSections) {
            if (isNewSection(changedSection)) {
                sectionDao.create(
                    changedSection.getLineId(),
                    changedSection.getUpStationId(),
                    changedSection.getDownStationId(),
                    changedSection.getDistance()
                );
            }
            if (isEditedSection(changedSection)) {
                sectionDao.edit(
                    changedSection.getId(),
                    changedSection.getLineId(),
                    changedSection.getUpStationId(),
                    changedSection.getDownStationId(),
                    changedSection.getDistance()
                );
            }
        }
        for (Section section : sections) {
            isDeletedSectionAndDirtyChecking(changedSections, section);
        }
    }

    private boolean isNewSection(Section changedSection) {
        Optional<Section> filteredSection = sections.stream()
            .filter(section -> section.getId().equals(changedSection.getId()))
            .findFirst();
        return !filteredSection.isPresent();
    }

    private boolean isEditedSection(Section changedSection) {
        Optional<Section> filteredSection = sections.stream()
            .filter(section -> {
                return section.getId().equals(changedSection.getId()) &&
                    (!section.getUpStationId().equals(changedSection.getUpStationId())
                        || !section.getDownStationId().equals(changedSection.getDownStationId())
                        || !(section.getDistance() == changedSection.getDistance())
                        || !section.getLineId().equals(changedSection.getLineId()));
            }).findFirst();
        return filteredSection.isPresent();
    }

    private void isDeletedSectionAndDirtyChecking(List<Section> changedSections, Section section) {
        Optional<Section> filteredSection = changedSections.stream()
            .filter(changedSection -> section.getId().equals(changedSection.getId()))
            .findFirst();
        if (!filteredSection.isPresent()) {
            sectionDao.deleteById(section.getId());
        }
    }
}
