package wooteco.subway.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;

@Service
@Transactional(readOnly = true)
public class LineService {

	private final LineDao lineDao;

	public LineService(LineDao lineDao) {
		this.lineDao = lineDao;
	}

	@Transactional
	public Line create(String name, String color, Section section) {
		validateNameNotDuplicated(name);
		Long lineId = lineDao.save(new Line(name, color, List.of(section)));
		return lineDao.findById(lineId);
	}

	private void validateNameNotDuplicated(String name) {
		if (lineDao.existsByName(name)) {
			throw new IllegalArgumentException("해당 이름의 지하철 노선이 이미 존재합니다");
		}
	}

	public List<Line> listLines() {
		return lineDao.findAll();
	}

	public Line findOne(Long id) {
		return lineDao.findById(id);
	}

	@Transactional
	public Line update(Line line) {
		lineDao.update(line);
		return findOne(line.getId());
	}

	@Transactional
	public void remove(Long id) {
		lineDao.remove(id);
	}

	@Transactional
	public void addSection(Long id, Section section) {
		Line line = lineDao.findById(id);
		line.addSection(section)
			.ifPresent(lineDao::updateSection);
		lineDao.saveSection(id, section);
	}
}
