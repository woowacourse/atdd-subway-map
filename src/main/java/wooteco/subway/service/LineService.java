package wooteco.subway.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import wooteco.subway.dao.repository.LineRepository;
import wooteco.subway.dao.repository.SectionRepository;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;

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
		line.findUpdatedSectionByAdd(section)
			.ifPresent(sectionRepository::update);
		sectionRepository.save(id, section);
	}

	@Transactional
	public void deleteSection(Long lineId, Long stationId) {
		Line line = lineRepository.findById(lineId);

		Sections deletedSections = line.deleteSectionByStation(stationId);
		validateSectionExist(deletedSections);

		deletedSections.executeEach(section -> sectionRepository.remove(section.getId()));
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
			sectionRepository.save(lineId, newSection);
		}
	}
}
