package wooteco.subway.service;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.LineRepository;
import wooteco.subway.dao.SectionRepository;
import wooteco.subway.dao.StationRepository;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.SectionRequest;

@Service
public class SectionService {

    private final SectionRepository sectionRepository;
    private final StationRepository stationRepository;

    public SectionService(SectionRepository sectionRepository, StationRepository stationRepository) {
        this.sectionRepository = sectionRepository;
        this.stationRepository = stationRepository;
    }

    @Transactional
    public void create(final Long lineId, final SectionRequest sectionRequest) {
        List<Section> rawSections = sectionRepository.findByLineId(lineId);
        Sections sections = new Sections(rawSections);

        Station upStation = stationRepository.findById(sectionRequest.getUpStationId());
        Station downStation = stationRepository.findById(sectionRequest.getDownStationId());
        Section section = new Section(lineId, upStation, downStation, sectionRequest.getDistance());
        sections.add(section);

        sectionRepository.save(section);
        sections.pickUpdate(sectionRepository
                        .findByLineId(lineId))
                .ifPresent(sectionRepository::update);
    }
}
