package wooteco.subway.service;

import org.springframework.stereotype.Service;
import wooteco.subway.dao.LineRepository;
import wooteco.subway.dao.SectionRepository;
import wooteco.subway.dao.StationRepository;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.SectionRequest;

import java.util.List;

@Service
public class SectionService {
    private final StationRepository stationRepository;
    private final LineRepository lineRepository;
    private final SectionRepository sectionRepository;

    public SectionService(StationRepository stationRepository, LineRepository lineRepository, SectionRepository sectionRepository) {
        this.stationRepository = stationRepository;
        this.lineRepository = lineRepository;
        this.sectionRepository = sectionRepository;
    }

    public void createSection(long lineId, LineRequest lineRequest) {
        Station upStation = stationRepository.findById(lineRequest.getUpStationId());
        Station downStation = stationRepository.findById(lineRequest.getDownStationId());
        int distance = lineRequest.getDistance();

        sectionRepository.save(lineId, Section.of(upStation, downStation, distance));
    }

    public LineResponse addSection(long lineId, SectionRequest sectionRequest) {
        Line line = loadLine(lineId);

        Station upStation = stationRepository.findById(sectionRequest.getUpStationId());
        Station downStation = stationRepository.findById(sectionRequest.getDownStationId());
        int distance = sectionRequest.getDistance();

        line.addSection(upStation, downStation, distance);
        updateSections(lineId, line);
        return LineResponse.from(line);
    }

    public void deleteSection(long lineId, long stationId) {
        Line line = loadLine(lineId);

        validateIsRemovable(line);

        Station station = stationRepository.findById(stationId);
        line.deleteStation(station);
        updateSections(lineId, line);
    }

    public Line loadLine(long lineId) {
        Line line = lineRepository.findById(lineId);
        List<Section> sections = sectionRepository.findAllByLineId(lineId);

        sections.forEach(this::loadEachLine);
        line.setSectionsFrom(sections);
        return line;
    }

    private void loadEachLine(Section section) {
        Long upStationId = sectionRepository.getUpStationIdById(section.getId());
        section.setUpStation(stationRepository.findById(upStationId));

        Long downStationId = sectionRepository.getDownStationIdById(section.getId());
        section.setDownStation(stationRepository.findById(downStationId));
    }

    private void validateIsRemovable(Line line) {
        if (!line.isRemovable()) {
            throw new IllegalArgumentException("등록된 역이 2개 이하일 때는 삭제할 수 없습니다.");
        }
    }

    private void updateSections(long lineId, Line line) {
        sectionRepository.deleteSectionsByLineId(lineId);
        sectionRepository.saveSections(lineId, line.getSections().sections());
    }
}
