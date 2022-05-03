package wooteco.subway.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import wooteco.subway.domain.Line;

public class LineDao {

	private Long seq = 0L;
	private final List<Line> lines = new ArrayList<>();

	public Long save(Line line) {
		Line newLine = new Line(++seq, line.getName(), line.getColor());
		lines.add(newLine);
		return newLine.getId();
	}

	public List<Line> findAll() {
		return lines;
	}

	public Line findById(Long id) {
		return lines.stream()
			.filter(line -> id.equals(line.getId()))
			.findAny()
			.orElseThrow(() -> new NoSuchElementException("해당 id에 맞는 지하철 노선이 없습니다."));
	}

	public void update(Long id, String name, String color) {
		lines.remove(findById(id));
		lines.add(new Line(id, name, color));
	}

	public void remove(Long id) {
		lines.remove(findById(id));
	}
}
