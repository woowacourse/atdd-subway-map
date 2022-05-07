package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;

@Service
@Transactional(readOnly = true)
public class LineService {

	private final LineDao lineDao;
	private final SectionDao sectionDao;

	public LineService(LineDao lineDao, SectionDao sectionDao) {
		this.lineDao = lineDao;
		this.sectionDao = sectionDao;
	}

	@Transactional
	public Line create(String name, String color) {
		validateNameNotDuplicated(name);
		Long lineId = lineDao.save(new Line(name, color));
		return lineDao.findById(lineId);
	}

	@Transactional
	public Line create(String name, String color, Section section) {
		validateNameNotDuplicated(name);
		Long lineId = lineDao.save(new Line(name, color));
		Long sectionId = sectionDao.save(lineId, section);

		Section foundSection = sectionDao.findById(sectionId);
		Line line = lineDao.findById(lineId);
		return line.createWithSection(List.of(foundSection));
	}

	private void validateNameNotDuplicated(String name) {
		if (lineDao.existsByName(name)) {
			throw new IllegalArgumentException("해당 이름의 지하철 노선이 이미 존재합니다");
		}
	}

	public List<Line> listLines() {
		return lineDao.findAll().stream()
			.map(line -> line.createWithSection(sectionDao.findByLineId(line.getId())))
			.collect(Collectors.toList());
	}

	public Line findOne(Long id) {
		Line line = lineDao.findById(id);
		List<Section> sections = sectionDao.findByLineId(line.getId());
		return line.createWithSection(sections);
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
