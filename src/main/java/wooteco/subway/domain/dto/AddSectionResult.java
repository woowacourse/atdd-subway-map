package wooteco.subway.domain.dto;

import java.util.List;
import wooteco.subway.domain.Section;

public class AddSectionResult {
    private Section newEndSection;
    private List<Section> splitSections;
    private Section removedSection;
    private boolean endSectionInserted;

    private AddSectionResult(Section newEndSection) {
    }

    public AddSectionResult(List<Section> splitSections, Section removedSection) {
        this.splitSections = splitSections;
        this.removedSection = removedSection;
    }

    public AddSectionResult(Section newEndSection, boolean endSectionInserted) {
        this.newEndSection = newEndSection;
        this.endSectionInserted = endSectionInserted;
    }

    public static AddSectionResult createWithNewEndSection(Section newEndSection) {
        return new AddSectionResult(newEndSection, true);
    }

    public static AddSectionResult createSplitSections(List<Section> splitSections, Section removedSection) {
        return new AddSectionResult(splitSections, removedSection);
    }

    public boolean isNewEndSectionInserted() {
        return endSectionInserted;
    }

    public Section getNewEndSection() {
        return newEndSection;
    }

    public List<Section> getSplitSections() {
        return splitSections;
    }

    public Section getFirstSplitSection() {
        return splitSections.get(0);
    }

    public Section getSecondSplitSection() {
        return splitSections.get(1);
    }

    public Section getRemovedSection() {
        return removedSection;
    }
}
