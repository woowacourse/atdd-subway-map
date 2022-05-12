package wooteco.subway.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import wooteco.subway.dao.repository.LineRepository;
import wooteco.subway.dao.repository.SectionRepository;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.SectionsDirtyChecker;

@Service
@Transactional(readOnly = true)
public class LineService {

	private final LineRepository lineRepository;
	private final SectionRepository sectionRepository;

	public LineService(LineRepository lineRepository, SectionRepository sectionRepository) {
		this.lineRepository = lineRepository;
		this.sectionRepository = sectionRepository;
	}

	@Transactional
	public Line create(String name, String color, Section section) {
		validateNameNotDuplicated(name);
		Long lineId = lineRepository.save(new Line(name, color, List.of(section)));
		return lineRepository.findById(lineId);
	}

	private void validateNameNotDuplicated(String name) {
		if (lineRepository.existsByName(name)) {
			throw new IllegalArgumentException("해당 이름의 지하철 노선이 이미 존재합니다");
		}
	}

	public List<Line> listLines() {
		return lineRepository.findAll();
	}

	public Line findOne(Long id) {
		return lineRepository.findById(id);
	}

	@Transactional
	public Line update(Line line) {
		lineRepository.update(line);
		return findOne(line.getId());
	}

	@Transactional
	public void remove(Long id) {
		lineRepository.remove(id);
	}

	@Transactional
	public void addSection(Long id, Section section) {
		Line line = lineRepository.findById(id);
		SectionsDirtyChecker checker = SectionsDirtyChecker.from(line.getSections());

		line.addSection(section);

		executeDirtyChecking(id, line, checker);
	}

	@Transactional
	public void deleteSection(Long lineId, Long stationId) {
		Line line = lineRepository.findById(lineId);
		SectionsDirtyChecker checker = SectionsDirtyChecker.from(line.getSections());

		line.deleteSectionByStation(stationId);

		executeDirtyChecking(lineId, line, checker);
	}

	private void executeDirtyChecking(Long lineId, Line line, SectionsDirtyChecker checker) {
		checker.findUpdated(line.getSections())
			.executeEach(sectionRepository::update);
		checker.findDeleted(line.getSections())
			.executeEach(section -> sectionRepository.remove(section.getId()));
		checker.findSaved(line.getSections())
			.executeEach(section -> sectionRepository.save(lineId, section));
	}
}
