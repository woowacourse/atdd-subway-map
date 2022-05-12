package wooteco.subway.domain;

import java.util.Optional;

public class RemoveSections {

    private final Section deleteSection;
    private final Optional<Section> updateSection;

    public RemoveSections(Section deleteSection, Optional<Section> updateSection) {
        this.deleteSection = deleteSection;
        this.updateSection = updateSection;
    }

    public Section getDeleteSection() {
        return deleteSection;
    }

    public Optional<Section> getUpdateSection() {
        return updateSection;
    }
}
