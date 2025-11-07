package com.example.fusion1_events;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EntrantSampler {

    public static List<String> sampleEntrants(List<String> waitingList, int sampleSize) {
        List<String> result = new ArrayList<>();

        if (waitingList == null || waitingList.isEmpty() || sampleSize <= 0) {
            return result;
        }

        if (sampleSize >= waitingList.size()) {
            result.addAll(waitingList);
            return result;
        }

        List<String> shuffled = new ArrayList<>(waitingList);
        Collections.shuffle(shuffled);
        result.addAll(shuffled.subList(0, sampleSize));
        return result;
    }
}
