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
            addBaseOnUpStation(lineId, sectionRequest);
            return;
        }
        addBaseOnDownStation(lineId, sectionRequest);
    }

    private void addBaseOnDownStation(final Long lineId, final SectionRequest sectionRequest) {
        sectionRepository.saveBaseOnDownStation(lineId, sectionRequest);
    }

    // TODO : sections에게 현재 upId을 하행으로 가지고 있는 section이 있는지 물어봄
    // TODO : sections이 있다고 하면, 중간에 끼어 들어가는 상황
    // TODO : sections로부터 Input upId를 출발지으로 가지고 있는 section의 도착지를 찾는다. (만약 가지고 있다면 중간삽입)
    private void addBaseOnUpStation(final Long lineId, final SectionRequest sectionRequest) {
        sectionRepository.saveBaseOnUpStation(lineId, sectionRequest);
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
