package wooteco.subway.domain;

import java.util.LinkedList;
import java.util.List;

public class Line {

	private static final long TEMPORARY_ID = 0L;

	private final Long id;
	private final Name name;
	private final String color;
	private final Sections sections;

	public Line(Long id, String name, String color) {
		this(id, name, color, new LinkedList<>());
	}

	public Line(String name, String color, List<Section> sections) {
		this(TEMPORARY_ID, name, color, sections);
	}

	public Line(Long id, String name, String color, List<Section> sections) {
		this.id = id;
		this.name = new Name(name);
		this.color = color;
		this.sections = new Sections(sections);
	}

	public Line createWithSection(List<Section> sections) {
		return new Line(id, name.getValue(), color, sections);
	}

	public boolean isSameName(String name) {
		return this.name.isSame(name);
	}

	public boolean isSameId(Long id) {
		return this.id.equals(id);
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name.getValue();
	}

	public String getColor() {
		return color;
	}

	public List<Station> listOrderedStations() {
		return sections.getStations();
	}

	public List<Section> getSections() {
		return sections.getValues();
	}
}
