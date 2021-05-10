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

    private void registerSectionsToUpdateWhenSameDownStation(Section section, Section sectionToConstruct) {
        sectionsToCreate.add(new Section(sectionToConstruct.getLine(),
            sectionToConstruct.getUpStation(),
            section.getUpStation(),
            new Distance(sectionToConstruct.getDistance().value() - section.getDistance().value())));
        sectionsToCreate.add(new Section(sectionToConstruct.getLine(),
            section.getUpStation(),
            section.getDownStation(),
            new Distance(section.getDistance().value())));
    }

    private void registerSectionsToUpdateWhenSameUpStation(Section section, Section sectionToConstruct) {
        sectionsToCreate.add(new Section(section.getLine(),
            sectionToConstruct.getUpStation(),
            section.getDownStation(),
            new Distance(section.getDistance().value())));
        sectionsToCreate.add(new Section(section.getLine(),
            section.getDownStation(),
            sectionToConstruct.getDownStation(),
            new Distance(sectionToConstruct.getDistance().value() - section.getDistance().value())));
    }

    private Section sectionToConstruct(Section sectionToInsert) {
        return sections.stream()
            .filter(section -> section.hasOnlyOneSameName(sectionToInsert))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("추가할 수 없는 구간입니다."));
    }

    public List<Section> sectionsToCreate() {
        return new ArrayList<>(sectionsToCreate);
    }

    public List<Section> getSectionsToRemove() {
        return new ArrayList<>(sectionsToRemove);
    }
}
