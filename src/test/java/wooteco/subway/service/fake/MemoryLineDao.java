package wooteco.subway.service.fake;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;

public class MemoryLineDao implements LineDao {

	private Long seq = 0L;
	private final List<Line> lines = new ArrayList<>();

	@Override
	public Long save(Line line) {
		Line newLine = new Line(++seq, line.getName(), line.getColor(), line.getSections());
		lines.add(newLine);
		return newLine.getId();
	}

	@Override
	public List<Line> findAll() {
		return lines;
	}

	@Override
	public Line findById(Long id) {
		return lines.stream()
			.filter(line -> line.isSameId(id))
			.findAny()
			.orElseThrow(() -> new NoSuchElementException("해당 id에 맞는 지하철 노선이 없습니다."));
	}

	@Override
	public void update(Line line) {
		lines.remove(findById(line.getId()));
		lines.add(line);
	}

	@Override
	public void remove(Long id) {
		lines.remove(findById(id));
	}

	@Override
	public Boolean existsByName(String name) {
		return lines.stream()
			.anyMatch(line -> line.isSameName(name));
	}

	@Override
	public void updateSection(Section section) {

	}

	@Override
	public void removeSection(Section section) {

	}

	@Override
	public void saveSection(Long id, Section section) {

	}
}
