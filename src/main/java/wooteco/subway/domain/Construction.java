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
        if (isEndSectionInsertion(section)) {
            sectionsToCreate.add(section);
            return;
        }
        insertSectionWhenNotEndSectionInsertion(section);
    }

    private void validateToInsertSection() {
        if (!sectionsToCreate.isEmpty()) {
            throw new IllegalStateException("이미 구간을 수정하였습니다.");
        }
    }

    private boolean isEndSectionInsertion(Section section) {
        Section firstSection = firstSection();
        Section lastSection = lastSection();

        return firstSection.getUpStation().equals(section.getDownStation())
            || lastSection.getDownStation().equals(section.getUpStation());
    }

    private void insertSectionWhenNotEndSectionInsertion(Section section) {
        Section sectionToConstruct = sectionToConstruct(section);
        sectionsToRemove.add(sectionToConstruct);
        registerSections(section, sectionToConstruct);
    }

    private Section sectionToConstruct(Section sectionToInsert) {
        return sections.stream()
            .filter(section -> section.hasOnlyOneSameStation(sectionToInsert))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("추가할 수 없는 구간입니다."));
    }

    private void registerSections(Section section, Section sectionToConstruct) {
        Station sameStation = sectionToConstruct.sameStation(section);
        if (section.getUpStation().equals(sameStation)) {
            registerSectionsToUpdateWhenSameUpStation(section, sectionToConstruct);
        }
        if (section.getDownStation().equals(sameStation)) {
            registerSectionsToUpdateWhenSameDownStation(section, sectionToConstruct);
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

    private Section firstSection() {
        return sections.stream()
            .filter(section -> sections.stream()
                .noneMatch(section1 -> section.getUpStation().equals(section1.getDownStation())))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("첫 번째 구간이 존재하지 않습니다."));
    }

    private Section lastSection() {
        return sections.stream()
            .filter(section -> sections.stream()
                .noneMatch(section1 -> section.getDownStation().equals(section1.getUpStation())))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("마지막 구간이 존재하지 않습니다."));
    }

    public List<Section> sectionsToCreate() {
        return new ArrayList<>(sectionsToCreate);
    }

    public List<Section> getSectionsToRemove() {
        return new ArrayList<>(sectionsToRemove);
    }
}
