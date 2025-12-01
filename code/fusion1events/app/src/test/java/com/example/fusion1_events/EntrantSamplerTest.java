package com.example.fusion1_events;

import static org.junit.Assert.*;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class EntrantSamplerTest {

    @Test
    public void testEmptyWaitingList() {
        List<String> waitingList = Collections.emptyList();
        List<String> sampled = EntrantSampler.sampleEntrants(waitingList, 3);
        assertTrue(sampled.isEmpty());
    }

    @Test
    public void testNullWaitingList() {
        List<String> sampled = EntrantSampler.sampleEntrants(null, 3);
        assertTrue(sampled.isEmpty());
    }

    @Test
    public void testSampleSizeZero() {
        List<String> waitingList = Arrays.asList("Alice", "Bob", "Charlie");
        List<String> sampled = EntrantSampler.sampleEntrants(waitingList, 0);
        assertTrue(sampled.isEmpty());
    }

    @Test
    public void testSampleSizeLargerThanList() {
        List<String> waitingList = Arrays.asList("Alice", "Bob");
        List<String> sampled = EntrantSampler.sampleEntrants(waitingList, 5);
        assertEquals(2, sampled.size());
        assertTrue(sampled.contains("Alice"));
        assertTrue(sampled.contains("Bob"));
    }

    @Test
    public void testSampleExactSize() {
        List<String> waitingList = Arrays.asList("Alice", "Bob", "Charlie");
        List<String> sampled = EntrantSampler.sampleEntrants(waitingList, 3);
        assertEquals(3, sampled.size());
        assertTrue(waitingList.containsAll(sampled));
    }

    @Test
    public void testSampleSubset() {
        List<String> waitingList = Arrays.asList("Alice", "Bob", "Charlie", "David");
        List<String> sampled = EntrantSampler.sampleEntrants(waitingList, 2);
        assertEquals(2, sampled.size());
        for (String s : sampled) {
            assertTrue(waitingList.contains(s));
        }
    }
}
