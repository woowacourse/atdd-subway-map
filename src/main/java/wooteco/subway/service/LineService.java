package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;

import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.service.dto.LineDto;

public class LineService {

	private final LineDao lineDao;

	public LineService(LineDao lineDao) {
		this.lineDao = lineDao;
	}

	public LineDto create(String name, String color) {
		validateNameNotDuplicated(name);
		Long lineId = lineDao.save(new Line(name, color));
		Line line = lineDao.findById(lineId);
		return LineDto.from(line);
	}

	private void validateNameNotDuplicated(String name) {
		if (lineDao.existsByName(name)) {
			throw new IllegalArgumentException("해당 이름의 지하철 노선이 이미 존재합니다");
		}
	}

	public List<LineDto> listLines() {
		return lineDao.findAll()
			.stream()
			.map(LineDto::from)
			.collect(Collectors.toUnmodifiableList());
	}

	public LineDto findOne(Long id) {
		return LineDto.from(lineDao.findById(id));
	}

	public LineDto update(Long id, String name, String color) {
		lineDao.update(id, name, color);
		return findOne(id);
	}

	public void remove(Long id) {
		lineDao.remove(id);
	}
}
