package wooteco.subway.line.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.line.domain.Line;
import wooteco.subway.line.dto.LineRequest;
import wooteco.subway.line.repository.LineRepository;

import java.util.List;

@Service
public class LineService {
    private final LineRepository lineRepository;

    public LineService(LineRepository lineRepository) {
        this.lineRepository = lineRepository;
    }

    @Transactional
    public Line save(final LineRequest lineRequest) {
        validateDuplicate(lineRequest.getName());
        return lineRepository.save(lineRequest.getName(), lineRequest.getColor(),
                lineRequest.getUpStationId(), lineRequest.getDownStationId(), lineRequest.getDistance());
    }

    private void validateDuplicate(final String name) {
        if (lineRepository.findByName(name).isPresent()) {
            throw new IllegalStateException("[ERROR] 이미 존재하는 노선입니다.");
        }
    }

    public List<Line> findAll() {
        return lineRepository.findAll();
    }

    public Line findById(final Long id) {
        return lineRepository.findById(id);
    }

    public void update(final Long id, LineRequest lineRequest) {
        lineRepository.update(new Line(id, lineRequest.getName(), lineRequest.getColor()));
    }
//
//    public void delete(final Long id) {
//        lineDao.delete(id);
//    }
//
//    private Station findStationById(Long stationId) {
//        return stationDao.findById(stationId)
//                .orElseThrow(() -> new IllegalStateException("[ERROR] 존재하지 않는 역입니다."));
//    }
//
//    @Transactional
//    public void addSection(final Long lineId, final SectionRequest sectionRequest) {
//        Line line = findLineById(lineId);
//        List<Section> sections = findSectionsByLineId(lineId);
//        line.initSections(sections);
//
//        //
//        line.addSection(new Section(sectionRequest.getUpStationId(), sectionRequest.getDownStationId(), sectionRequest.getDistance()));
//
//        // TODO : 예외
//        //  lineId가 존재하는지
//        //  line의 section에 upstationId와 downStationId 둘다 존재하는지 - 노선의 구간에 이미 등록되어있음
//        //  upstationId 또는 downStationId로 section을 찾는데, 찾은 section의 distance가 sectionAddRequest의 distance보다 작거나 같은 경우
//
//
//        // TODO : line의 section에 sectionAddRequest의 upstationId가 존재하는지
//        //  존재하면 sectionAddRequest의 upstationId로 section을 찾고
//        //  찾은 section의 upstationId를 sectionAddRequest의 downStationId로 수정한다.
//        //  찾은 section의 distance를 sectionAddRequest의 distance를 뺀 값으로 수정한다.
//        //
//
//        // TODO : line의 section에 sectionAddRequest의 downStationId가 존재하는지
//        //  존재하면 sectionAddRequest의 downStationId로 section을 찾고
//        //  찾은 section의 downStationId를 sectionAddRequest의 upStationId로 수정한다.
//        //  찾은 section의 distance를 sectionAddRequest의 distance를 뺀 값으로 수정한다.
//
//        // TODO : section save
//
////        sectionDao.save(sectionRequest.toEntity(lineId));
//    }
//
//    private List<Section> findSectionsByLineId(Long lineId) {
//        return sectionDao.findAllByLineId(lineId)
//                .stream()
//                .map(section ->
//                        new Section(section.id(), findStationById(section.upStation().id()),
//                                findStationById(section.downStation().id()), section.distance()))
//                .collect(Collectors.toList());
//    }
//
//    private Line findLineById(Long lineId) {
//        return lineDao.findById(lineId)
//                .orElseThrow(() -> new IllegalStateException("[ERROR] 존재하지 않는 노선입니다."));
//    }
}
