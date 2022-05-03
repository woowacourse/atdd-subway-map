package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.service.dto.LineDto;

@Service
@Transactional(readOnly = true)
public class LineService {

	private final LineDao lineDao;

	public LineService(LineDao lineDao) {
		this.lineDao = lineDao;
	}

	@Transactional
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

	@Transactional
	public LineDto update(Line line) {
		lineDao.update(line);
		return findOne(line.getId());
	}

	@Transactional
	public void remove(Long id) {
		lineDao.remove(id);
	}
}
