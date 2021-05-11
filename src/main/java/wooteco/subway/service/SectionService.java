package wooteco.subway.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wooteco.subway.dao.LineRepository;
import wooteco.subway.dao.SectionRepository;
import wooteco.subway.dao.StationRepository;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.SectionRequest;

import java.util.List;

@Service
public class SectionService {
    private final StationRepository stationRepository;
    private final LineRepository lineRepository;
    private final SectionRepository sectionRepository;

    @Autowired
    public SectionService(StationRepository stationRepository, LineRepository lineRepository, SectionRepository sectionRepository) {
        this.stationRepository = stationRepository;
        this.lineRepository = lineRepository;
        this.sectionRepository = sectionRepository;
    }

    public void createSection(long lineId, SectionRequest sectionRequest) {
        Line line = this.loadLine(lineId);

        Station upStation = this.stationRepository.findById(sectionRequest.getUpStationId());
        Station downStation = this.stationRepository.findById(sectionRequest.getDownStationId());
        int distance = sectionRequest.getDistance();

        line.addSection(upStation, downStation, distance);

        this.updateSections(lineId, line);
    }

    public void deleteSection(long lineId, long stationId) {
        Line line = this.loadLine(lineId);

        Station station = this.stationRepository.findById(stationId);
        line.deleteStation(station);

        this.updateSections(lineId, line);
    }

    private Line loadLine(long lineId) {
        Line line = this.lineRepository.findById(lineId);
        List<Section> sections = this.sectionRepository.findAllByLineId(lineId);

        for (Section section : sections) {
            long sectionId = section.getId();

            Long upStationIdById = this.sectionRepository.getUpStationIdById(sectionId);
            Station upStation = this.stationRepository.findById(upStationIdById);

            Long downStationIdById = this.sectionRepository.getDownStationIdById(sectionId);
            Station downStation = this.stationRepository.findById(downStationIdById);

            section.setUpStation(upStation);
            section.setDownStation(downStation);
        }

        line.setSectionsFrom(sections);
        return line;
    }

    private void updateSections(long lineId, Line line) {
        this.sectionRepository.deleteSectionsByLineId(lineId);
        this.sectionRepository.saveSections(lineId, line.getSections().sections());
    }
}
