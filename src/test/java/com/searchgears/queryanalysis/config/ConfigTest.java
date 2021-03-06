package com.searchgears.queryanalysis.config;

import com.google.common.collect.ImmutableSet;
import org.apache.lucene.analysis.util.ClasspathResourceLoader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ConfigTest {
    private Config config;
    private List<Rule> rules;
    private Map<String, Matcher> matchers;

    @BeforeEach
    public void readConfig() {
        config = parseConfig("solr/testcore/conf/queryanalysis.yml");
        rules = config.getRules();
        matchers = config.getMatchers();
    }

    @Test
    public void rulesMatchersAreReadCorrectly() {
        assertTrue(rules.size() == 1);
        Rule rule = rules.get(0);
        assertArrayEquals(new String[]{"publisher", "publisherMarker"}, rule.getMatchers().toArray());
    }

    @Test
    public void rulesParamsAreReadCorrectly() {
        Rule rule = rules.get(0);
        Map<String, String> params = rule.getParams();
        assertTrue(params.size() == 1);
        assertEquals(params.get("qf"), "authors^1000");
    }

    @Test
    public void matchersAreReadCorrectly() {
        assertTrue(matchers.size() == 2);
        assertEquals(ImmutableSet.of("publisher", "publisherMarker"), matchers.keySet());
    }

    @Test
    public void matchersHaveCorrectDictionary() {
        assertEquals("publisher.dic", matchers.get("publisher").getDictionary());
    }

    @Test
    public void matcherWithoutDefinitionThrows() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> parseConfig("solr/testcore/conf/queryanalysis-invalid-matcher.yml"));
        assertEquals("No definition for matcher foobasel. ", exception.getMessage());
    }

    @Test
    public void nonExistingConfigFileThrows() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> Config.fromCorePath(new ClasspathResourceLoader(this.getClass()), "non-existing"));
        assertEquals("Error reading file \"non-existing\". ", exception.getMessage());
    }

    private Config parseConfig(String path) {
        return Config.fromClasspath(path);
    }

}
