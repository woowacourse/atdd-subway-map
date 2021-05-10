package wooteco.subway.domain;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Construction {

    private final Set<Section> sections;
    private final List<Section> sectionsToCreate;
    private final List<Section> sectionsToRemove;

    public Construction(Set<Section> sections) {
        this.sections = new HashSet<>(sections);
        this.sectionsToCreate = new ArrayList<>();
        this.sectionsToRemove = new ArrayList<>();
    }

    public void insertSection(Section section) {
        validateToInsertSection();
        Section sectionToConstruct = sectionToConstruct(section);
        Station sameStation = sectionToConstruct.sameStation(section);
        sectionsToRemove.add(sectionToConstruct);
        registerSections(section, sectionToConstruct, sameStation);
    }

    private void registerSections(Section section, Section sectionToConstruct, Station sameStation) {
        if (isFirstSection(sectionToConstruct) || isLastSection(sectionToConstruct)) {
            sectionsToCreate().add(section);
            return;
        }
        registerSectionsWhenNotEndSection(section, sectionToConstruct, sameStation);
    }

    private void registerSectionsWhenNotEndSection(Section section, Section sectionToConstruct, Station sameStation) {
        if (section.getUpStation().equals(sameStation)) {
            registerSectionsToUpdateWhenSameUpStation(section, sectionToConstruct);
        }
        if (section.getDownStation().equals(sameStation)) {
            registerSectionsToUpdateWhenSameDownStation(section, sectionToConstruct);
        }
    }

    private void validateToInsertSection() {
        if (!sectionsToCreate.isEmpty()) {
            throw new IllegalStateException("이미 구간을 수정하였습니다.");
        }
    }

    private void registerSectionsToUpdateWhenSameUpStation(Section section,
        Section sectionToConstruct) {
        sectionsToCreate.add(section);
        sectionsToCreate.add(new Section(section.getLine(),
            section.getDownStation(),
            sectionToConstruct.getDownStation(),
            new Distance(
                sectionToConstruct.getDistance().value() - section.getDistance().value())));
    }

    private void registerSectionsToUpdateWhenSameDownStation(Section section,
        Section sectionToConstruct) {
        sectionsToCreate.add(new Section(sectionToConstruct.getLine(),
            sectionToConstruct.getUpStation(),
            section.getUpStation(),
            new Distance(
                sectionToConstruct.getDistance().value() - section.getDistance().value())));
        sectionsToCreate.add(section);
    }

    private Section sectionToConstruct(Section sectionToInsert) {
        return sections.stream()
            .filter(section -> isSectionToConstruct(section, sectionToInsert))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("추가할 수 없는 구간입니다."));
    }

    private boolean isSectionToConstruct(Section section, Section sectionToInsert) {
        if (section.getUpStation().equals(sectionToInsert.getDownStation())) {
            return isFirstSection(section);
        }
        if (section.getDownStation().equals(sectionToInsert.getUpStation())) {
            return isLastSection(section);
        }
        return section.hasOnlyOneSameStation(sectionToInsert);
    }

    private boolean isFirstSection(Section section) {
        return sections.stream()
            .noneMatch(sectionsForSearch -> sectionsForSearch.getDownStation()
                .equals(section.getUpStation()));
    }

    private boolean isLastSection(Section section) {
        return sections.stream()
            .noneMatch(sectionForSearch -> sectionForSearch.getUpStation()
                .equals(section.getDownStation()));
    }

    public List<Section> sectionsToCreate() {
        return new ArrayList<>(sectionsToCreate);
    }

    public List<Section> getSectionsToRemove() {
        return new ArrayList<>(sectionsToRemove);
    }
}
