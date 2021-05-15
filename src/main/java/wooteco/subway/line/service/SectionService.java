package wooteco.subway.line.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.exception.NotFoundException;
import wooteco.subway.line.domain.Section;
import wooteco.subway.line.domain.Sections;
import wooteco.subway.line.dto.SectionRequest;
import wooteco.subway.line.repository.LineRepository;
import wooteco.subway.line.repository.SectionRepository;
import wooteco.subway.station.repository.StationRepository;

@Service
public class SectionService {
    private final LineRepository lineRepository;
    private final StationRepository stationRepository;
    private final SectionRepository sectionRepository;

    public SectionService(final LineRepository lineRepository, final StationRepository stationRepository, final SectionRepository sectionRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
        this.sectionRepository = sectionRepository;
    }

    public void lineCreateAdd(final Long lineId, final SectionRequest sectionRequest) {
        validateLineId(lineId);
        validateStations(sectionRequest);
        Section section = new Section(sectionRequest.getUpStationId(), sectionRequest.getDownStationId(), sectionRequest.getDistance());
        sectionRepository.save(lineId, section.getUpStationId(), section.getDownStationId(), section.getDistance());
    }

    @Transactional
    public void add(final Long lineId, final SectionRequest sectionRequest) {
        Sections sections = new Sections(sectionRepository.getSectionsByLineId(lineId));
        validateAddRequest(lineId, sectionRequest, sections);
        Section section = new Section(sectionRequest.getUpStationId(), sectionRequest.getDownStationId(), sectionRequest.getDistance());

        if (sections.containUpStationId(section.getUpStationId())) {
            addBaseOnUpStation(lineId, section, sections);
            return;
        }
        addBaseOnDownStation(lineId, sectionRequest);
    }

    private void addBaseOnDownStation(final Long lineId, final SectionRequest sectionRequest) {
        sectionRepository.saveBaseOnDownStation(lineId, sectionRequest);
    }

    private void addBaseOnUpStation(final Long lineId, final Section section, final Sections sections) {
        if(sections.containUpStationId(section.getUpStationId())){
            Long beforeConnectedStationId = sections.getDownStationId(section.getUpStationId());
            saveSectionBetweenStationsBaseOnUpStation(lineId, section, sections, beforeConnectedStationId);
            return;
        }
        sectionRepository.save(lineId, section.getUpStationId(), section.getDownStationId(), section.getDistance());
    }

    private void saveSectionBetweenStationsBaseOnUpStation(final Long lineId, final Section section, final Sections sections, final Long beforeConnectedStationId) {
        int beforeDistance = sections.getDistance(section.getUpStationId(), beforeConnectedStationId);
        if(beforeDistance <= section.getDistance()){
            throw new IllegalArgumentException("기존에 존재하는 구간의 길이가 더 짧습니다.");
        }
        sectionUpdateBetweenSaveBaseOnUpStation(lineId, section, beforeConnectedStationId, beforeDistance);
    }

    private void sectionUpdateBetweenSaveBaseOnUpStation(final Long lineId, final Section section, final Long beforeConnectedStationId, final int beforeDistance) {
        sectionRepository.save(lineId, section.getUpStationId(), section.getDownStationId(), section.getDistance());
        sectionRepository.save(lineId, section.getDownStationId(), beforeConnectedStationId, beforeDistance - section.getDistance());
        sectionRepository.delete(lineId, section.getUpStationId(), beforeConnectedStationId);
    }

    private void validateAddRequest(final Long lineId, final SectionRequest sectionRequest, final Sections sections) {
        validateLineId(lineId);
        validateStations(sectionRequest);
        sections.isValidateSection(sectionRequest.getUpStationId(), sectionRequest.getDownStationId());
    }

    private void validateStations(final SectionRequest sectionRequest) {
        if (!stationRepository.isExistId(sectionRequest.getUpStationId()) || !stationRepository.isExistId(sectionRequest.getDownStationId())) {
            throw new NotFoundException("존재하지 않는 station을 구간에 등록할 수 없습니다.");
        }
    }

    @Transactional
    public void delete(final Long lineId, final Long stationId) {
        validateDeleteRequest(lineId, stationId);
        sectionRepository.deleteSection(lineId, stationId);
    }

    private void validateDeleteRequest(final Long lineId, final Long stationId) {
        validateLineId(lineId);
        Sections sections = new Sections(sectionRepository.getSectionsByLineId(lineId));
        sections.validateDeleteStation(stationId);
    }

    private void validateLineId(final Long lineId) {
        if (!lineRepository.isExistId(lineId)) {
            throw new NotFoundException("존재하지 않는 Line id 입니다.");
        }
    }
}
