package wooteco.subway.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Estimate {
    private final List<Section> sectionsToCreate;
    private final List<Section> sectionsToRemove;

    public Estimate(List<Section> sectionsToCreate,
        List<Section> sectionsToRemove) {
        this.sectionsToCreate = sectionsToCreate;
        this.sectionsToRemove = sectionsToRemove;
    }

    public List<Section> sectionsToCreate() {
        return new ArrayList<>(sectionsToCreate);
    }

    public List<Section> sectionsToRemove() {
        return new ArrayList<>(sectionsToRemove);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Estimate estimate = (Estimate) o;
        return Objects.equals(sectionsToCreate, estimate.sectionsToCreate)
            && Objects.equals(sectionsToRemove, estimate.sectionsToRemove);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sectionsToCreate, sectionsToRemove);
    }
}
