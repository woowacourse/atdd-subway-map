package wooteco.subway.domain;

import java.util.ArrayList;
import java.util.List;

public class Line {
    private Long id;
    private String name;
    private String color;
    private Sections sections;

    public Line(String name, String color) {
        this(0L, name, color, new ArrayList<>());
    }

    public Line(String name, String color, List<Section> sections) {
        this(0L, name, color, sections);
    }

    public Line(Long id, String name, String color) {
        this(id, name, color, new ArrayList<>());
    }

    public Line(Long id, String name, String color, List<Section> sections) {
        this(id, name, color, new Sections(sections));
    }

    public Line(Long id, String name, String color, Sections sections) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.sections = sections;
    }

    public Long id() {
        return id;
    }

    public String name() {
        return name;
    }

    public String color() {
        return color;
    }

    public boolean sameId(final Long id) {
        return this.id.equals(id);
    }

    public boolean sameName(final String name) {
        return this.name.equals(name);
    }

    public void changeName(final String name) {
        this.name = name;
    }

    public void changeColor(String color) {
        this.color = color;
    }

    public void initSections(List<Section> sections) {
        this.sections = new Sections(sections);
    }

    // TODO : 예외

    //  upstationId 또는 downStationId로 section을 찾는데, 찾은 section의 distance가 sectionAddRequest의 distance보다 작거나 같은 경우


    // TODO : line의 section에 sectionAddRequest의 upstationId가 존재하는지
    //  존재하면 sectionAddRequest의 upstationId로 section을 찾고
    //  찾은 section의 upstationId를 sectionAddRequest의 downStationId로 수정한다.
    //  찾은 section의 distance를 sectionAddRequest의 distance를 뺀 값으로 수정한다.
    //

    // TODO : line의 section에 sectionAddRequest의 downStationId가 존재하는지
    //  존재하면 sectionAddRequest의 downStationId로 section을 찾고
    //  찾은 section의 downStationId를 sectionAddRequest의 upStationId로 수정한다.
    //  찾은 section의 distance를 sectionAddRequest의 distance를 뺀 값으로 수정한다.


    public void addSection(Section section) {
        //  line의 section에 upstationId와 downStationId 둘다 존재하는지 - 노선의 구간에 이미 등록되어있음
        checkAbleToAddSection(section);
        sections.add(section);
    }

    private void checkAbleToAddSection(Section section) {
        if (sections.isAlreadyRegistered(section)) {
            throw new IllegalStateException("[ERROR] 이미 등록되어 있는 구간입니다.");
        }
    }
}
