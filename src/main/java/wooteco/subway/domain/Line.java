package wooteco.subway.domain;

import java.util.List;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import wooteco.subway.common.Id;
import wooteco.subway.exception.badRequest.WrongInformationException;

@Getter
public class Line {

    @Id
    private Long id;
    private String name;
    private String color;
    private Sections sections;

    private Line(Long id, String name, String color, Sections sections) {
        if (StringUtils.isEmpty(name) || StringUtils.isEmpty(color)) {
            throw new WrongInformationException();
        }
        this.id = id;
        this.name = name;
        this.color = color;
        this.sections = sections;
    }

    public static Line create(String name, String color) {
        return create(null, name, color, Sections.create());
    }

    public static Line create(Long id, String name, String color) {
        return create(id, name, color, Sections.create());
    }

    public static Line create(Long id, String name, String color, Sections sections) {
        return new Line(id, name, color, sections);
    }

    public boolean isSameName(String name) {
        return this.name.equals(name);
    }

    public boolean isSameId(Long id) {
        return this.id.equals(id);
    }

    public void changeInfo(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public boolean isNotSameId(Long id) {
        return !this.id.equals(id);
    }

    public boolean isSameColor(String color) {
        return this.color.equals(color);
    }

    public void addSection(Section section) {
        sections.addSection(section);
    }

    public Section firstSection() {
        return sections.firstSection();
    }

    public void addSections(Sections sections) {
        this.sections = sections;
    }

    public List<Station> stations() {
        return sections.asStations();
    }
}
