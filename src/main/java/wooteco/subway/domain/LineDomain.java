package wooteco.subway.domain;

public class LineDomain {

    private final Long id;
    private final Name name;
    private final String color;
    private final SectionsDomain sections;

    public LineDomain(final Long id, final Name name, final String color, final SectionsDomain sections) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.sections = sections;
    }

    public LineDomain(final Long id, final Name name, final String color) {
        this(id, name, color, null);
    }

    public LineDomain addSections(final SectionsDomain sections) {
        return new LineDomain(id, name, color, sections);
    }

    public Long getId() {
        return id;
    }

    public Name getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public SectionsDomain getSections() {
        return sections;
    }
}
