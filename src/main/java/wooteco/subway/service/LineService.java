package wooteco.subway.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;

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
		line.findUpdatedSectionByAdd(section)
			.ifPresent(lineDao::updateSection);
		lineDao.saveSection(id, section);
	}

	@Transactional
	public void deleteSection(Long lineId, Long stationId) {
		Line line = lineDao.findById(lineId);

		Sections deletedSections = line.deleteSectionByStation(stationId);
		validateSectionExist(deletedSections);

		deletedSections.executeEach(lineDao::removeSection);
		saveSectionIfUpdated(lineId, deletedSections);
	}

	private void validateSectionExist(Sections deletedSections) {
		if (deletedSections.isEmpty()) {
			throw new IllegalArgumentException("삭제하려는 역 구간이 없습니다.");
		}
	}

	private void saveSectionIfUpdated(Long lineId, Sections deletedSections) {
		Section newSection = deletedSections.sum();
		if (deletedSections.isNotExist(newSection)) {
			lineDao.saveSection(lineId, newSection);
		}
	}
}
