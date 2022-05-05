package wooteco.subway.domain;

public class Line {

	private static final long TEMPORARY_ID = 0L;

	private final Long id;
	private final Name name;
	private final String color;

	public Line(String name, String color) {
		this(TEMPORARY_ID, name, color);
	}

	public Line(Long id, String name, String color) {
		this.id = id;
		this.name = new Name(name);
		this.color = color;
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
}
