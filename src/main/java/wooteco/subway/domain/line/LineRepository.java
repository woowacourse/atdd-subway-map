package wooteco.subway.domain.line;

import java.util.List;

public interface LineRepository {

    Long saveLine(Line line);

    List<Line> findLines();

    Line findLineById(Long lineId);

    void updateLine(Line line);

    void removeLine(Long lineId);
}
