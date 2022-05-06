package wooteco.subway.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import wooteco.subway.domain.Line;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.exception.ClientException;

public class FakeLineDao implements LineDao {

    private static final Map<String, Line> LINES = new HashMap<>();

    private static Long seq = 0L;

    @Override
    public Line save(LineRequest line) {
        if (LINES.containsKey(line.getName())) {
            throw new ClientException("이미 등록된 지하철노선입니다.");
        }
        Line persistLine = new Line(++seq, line.getName(), line.getColor());
        LINES.put(line.getName(), persistLine);
        return persistLine;
    }

    @Override
    public List<Line> findAll() {
        return LINES.keySet()
                .stream()
                .map(LINES::get)
                .collect(Collectors.toList());
    }

    @Override
    public Line find(Long id) {
        return LINES.keySet()
                .stream()
                .filter(key -> LINES.get(key).getId() == id)
                .map(key -> LINES.get(key))
                .findAny()
                .orElseThrow(() -> new ClientException("존재하지 않는 노선입니다."));
    }

    @Override
    public int update(Long id, LineRequest line) {
        if (!LINES.containsKey(line.getName())) {
            throw new ClientException("존재하지 않는 노선입니다.");
        }
        LINES.put(line.getName(), new Line(id, line.getName(), line.getColor()));
        if (LINES.containsKey(line.getName())) {
            return 1;
        }
        return 0;
    }

    @Override
    public int delete(Long id) {
        String lineName = LINES.keySet()
                .stream()
                .filter(key -> LINES.get(key).getId() == id)
                .findAny()
                .orElseThrow(() -> new ClientException("존재하지 않는 노선입니다."));
        LINES.remove(lineName);
        if (LINES.containsKey(lineName)) {
            return 0;
        }
        return 1;
    }
}
