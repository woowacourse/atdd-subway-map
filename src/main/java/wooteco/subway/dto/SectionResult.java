package wooteco.subway.dto;

import wooteco.subway.domain.Section;

public class SectionResult {
    private boolean canAddAsBetweenStation;
    private Section existedSection;
    private Section insertedSection;
    private Section generatedSection;

    public SectionResult(boolean canAddAsBetweenStation) {
        this.canAddAsBetweenStation = canAddAsBetweenStation;
    }

    private SectionResult(boolean canAddAsBetweenStation, Section existedSection, Section insertedSection, Section generatedSection) {
        this.canAddAsBetweenStation = canAddAsBetweenStation;
        this.existedSection = existedSection;
        this.insertedSection = insertedSection;
        this.generatedSection = generatedSection;
    }

    public static SectionResult of(Section existedSection, Section insertedSection) {
        Section generatedSection = Section.createBySections(existedSection, insertedSection);
        return new SectionResult(true, existedSection, insertedSection, generatedSection);
    }

    public boolean canAddAsBetweenStation() {
        return canAddAsBetweenStation;
    }

    public Section getExistedSection() {
        return existedSection;
    }

    public Section getInsertedSection() {
        return insertedSection;
    }

    public Section getGeneratedSection() {
        return generatedSection;
    }
}
