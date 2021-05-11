package wooteco.subway.line.section;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.line.section.dto.SectionRequest;

@Service
@Transactional
public class SectionService {

    private final SectionDao sectionDao;

    public SectionService(SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    public void save(Long lineId, SectionRequest sectionRequest) {
        Section section = new Section(sectionRequest.getUpStationId(), sectionRequest.getDownStationId(), sectionRequest.getDistance());

        // TO-DO 1. Section 정보를 가져온다.

        // 케이스 1: 기존에 B(상행) - A(하행) // 입력 A(상행) - C(하행). 즉 A가 하행인 케이스를 찾아야 한다.
        // 케이스 2: 기존에 C(상행) - B(하행) // 입력 A(상행) - C(하행). 즉 C가 상행인 역을 찾아야 한다.
        // 케이스 3: 기존에 A(상행) - B(하행) // 입력 A(상행) - C(하행). 즉 A와 C 사이에 B가 끼워넣어져야 한다.

        // 그렇게 이렇게 하고 보니깐 정렬이 어렵다 -_-;;

        // A-C 일 때, A가 상행인 Section이 존재하는가?: A-B로 가정
            // SECTION where line_id = lineId and up_station_id = targetUpStationId;
            // 으로 A가 상행인 Section을 찾는다.
            // 그 후, A-B간의 거리 > A-C의 거리?
            // 맞다면 A-C-B가 되며, 기존의 A-B간의 거리와 A-C만큼의 거리를 뺀 만큼 B-C라는 구간이 필요하다.
            // ex) A-B: 10, A-C: 7 ==> A-C: 7, C-B: 3
                // 이 경우, 일단 Section을 새로 생성하고 A와 C 구간에 대한 정보를 저장한다.
                // 그리고 기존에 검색했던 Section(A-B)은 새로 집어넣는 구간(A-C)의 하행선(C)을 시작점으로
                // "변경"하고 새로들어온 Section 만큼을 뺀다음 저장한다. (==> C-B)
            // 아니라면 예외 케이스 3번에서 걸린다.

        // A-C일 때, C가 하행인 Section이 존재하는가?: B-C로 가정
            // SECTION where line_id = lineId and down_station_id = targetDownStationId;
            // 으로 C가 하행인 Section을 찾는다.
            // 그 후, B-C간의 거리 > A-C의 거리?
            // 맞다면 B-A-C가 되며, 기존의 B-C간의 거리와 A-C만큼의 거리를 뺀 만큼 B-A라는 구간이 필요하다.
            // ex) B-C: 10, A-C: 7 ==> B-A: 3, A-C: 7
                // 이 경우, 일단 Section을 새로 생성하고 A와 C 구간에 대한 정보를 저장한다.
                // 그리고 기존에 검색했던 Section(B-C)은 새로 집어넣는 구간(A-C)의 상행선(A)을 종점으로
                // "변경"하고 새로들어온 Section 만큼을 뺀다음 저장한다. (==> B-A)
            // 아니라면 예외 케이스 3번에서 걸린다.

        // A가 하행인 Section이 존재하는가? B-A 혹은 B-A-D로 가정한다.
            // A가 종점이면 B-A-C가 된다.

        // C가 상행인 Section이 존재하는가? C-B 혹은 D-C-B로 가정한다.
            // C가 역의 시작점이면 A-C-B가 된다.

        // 예외케이스 1: 만약에 찾고자 하는 Section이 없다면 에러 호출
        // 예외케이스 2: 유저 입력 에러 케이스 관련
        // 예외케이스 3: 케이스 3의 경우에서 A와 B의 사이에 C를 추가해야 하는 상황에서...
        // 예외케이스 3-1: A-C사이의 거리가 A-B의 거리보다 크면 예외 케이스 발생
        // 예외케이스 3-2: A-C사이의 거리가 A-B의 거리와 같다면 예외 케이스 발생

        sectionDao.save(lineId, section);
    }

    public void deleteByLineId(Long lineId) {
        sectionDao.deleteByLineId(lineId);
    }

    public Sections findByLineId(Long lineId) {
        Sections sections = new Sections(sectionDao.findByLineId(lineId));
        sections.sort();
        return sections;
    }
}
