package wooteco.subway.infra.entity;

import java.util.ArrayList;
import java.util.List;

public class LineEntity {

    private Long id;
    private String name;
    private String color;

    private List<SectionEntity> sectionEntities;

    public LineEntity() {
    }

    public LineEntity(Long id, String name, String color, List<SectionEntity> sectionEntities) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.sectionEntities = sectionEntities;
    }

    public LineEntity(Long id, String name, String color) {
        this(id, name, color, new ArrayList<>());
    }

    public LineEntity(String name, String color) {
        this(null, name, color);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public List<SectionEntity> getSectionEntities() {
        return sectionEntities;
    }
}
