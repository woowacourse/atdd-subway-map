package wooteco.subway.domain.line.value.section;

import wooteco.subway.exception.line.NegativeIdException;

import java.util.Objects;

public class SectionId extends Number {

    private final Long sectionId;

    private SectionId() {
        this.sectionId = -1L;
    }

    public SectionId(Long sectionId) {
        validateThatIsNegativeOrZeroNumber(sectionId);
        this.sectionId = sectionId;
    }

    public static SectionId empty() {
        return new SectionId();
    }

    private void validateThatIsNegativeOrZeroNumber(Long id) {
        if(id < 0) {
            throw new NegativeIdException();
        }
    }

    @Override
    public int intValue() {
        return sectionId.intValue();
    }

    @Override
    public long longValue() {
        return sectionId.longValue();
    }

    @Override
    public float floatValue() {
        return sectionId.floatValue();
    }

    @Override
    public double doubleValue() {
        return sectionId.doubleValue();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SectionId sectionId1 = (SectionId) o;
        return Objects.equals(sectionId, sectionId1.sectionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sectionId);
    }

}
