package wooteco.subway.util;

import java.util.stream.Collector;
import java.util.stream.Collectors;

public class CollectorsUtils {

    public static <T> Collector<T, ?, T> findOneCertainly() {
        return Collectors.collectingAndThen(
            Collectors.toList(),
            list -> {
                if (list.size() == 1) {
                    return list.get(0);
                }
                throw new RuntimeException("not connecetd"); // TODO : fix to custom exception
            }
        );
    }
}
