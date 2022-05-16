package study;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.junit.jupiter.api.Test;

public class EdgesToSortedNodesTest {

    @Test
    void toSortedList() {
        HashMap<Integer, Integer> input = new HashMap<>() {{
            put(10000, 100000);
            put(10, 100);
            put(1, 10);
            put(1000, 10000);
            put(100, 1000);
        }};

        HashMap<Integer, Integer> input2 = new HashMap<>() {{
            put(100000, 10000);
            put(100, 10);
            put(10, 1);
            put(10000, 1000);
            put(1000, 100);
        }};

        List<Integer> actual = operation(input, input2);
        List<Integer> expected = List.of(1, 10, 100, 1000, 10000, 100000);

        assertThat(actual).isEqualTo(expected);
    }

    private List<Integer> operation(HashMap<Integer, Integer> map,
                                    HashMap<Integer, Integer> oppositeMap) {
        List<Integer> list = new ArrayList<>();

        int capacity = map.values().size() + 1;

        // get first key
        final int start = (int) map.keySet().toArray()[0];
        list.add(start);

        int cur = start;
        boolean isForward = true;

        while (list.size() < capacity) {
            if (isForward && map.containsKey(cur)) {
                int next = map.get(cur);
                list.add(next);
                cur = next;
                continue;
            }
            isForward = false;
            cur = list.get(0);
            int next =  oppositeMap.get(cur);
            list.add(0, next);
            cur = next;
        }
        return list;
    }
}
