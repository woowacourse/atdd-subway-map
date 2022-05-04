package wooteco.subway.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;

@Service
@Transactional(readOnly = true)
public class LineService {

	private final LineDao lineDao;

	public LineService(LineDao lineDao) {
		this.lineDao = lineDao;
	}

	@Transactional
	public Line create(String name, String color) {
		validateNameNotDuplicated(name);
		Long lineId = lineDao.save(new Line(name, color));
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
}
