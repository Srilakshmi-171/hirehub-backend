
package com.hirehub.util;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public final class SkillNormalizer {

    private static final Map<String, String> ALIASES = Map.ofEntries(
        Map.entry("js", "javascript"),
        Map.entry("javascript", "javascript"),
        Map.entry("spring framework", "spring"),
        Map.entry("spring", "spring"),
        Map.entry("my sql", "mysql"),
        Map.entry("rest api", "rest"),
        Map.entry("rest apis", "rest")
    );

    private SkillNormalizer() {
    }

    public static Set<String> normalize(String skills) {

        if (skills == null || skills.isBlank()) {
            return Set.of();
        }

        return Arrays.stream(skills.split(","))
                .map(String::trim)
                .filter(skill -> !skill.isBlank())
                .map(skill -> skill.toLowerCase(Locale.ROOT)
                        .replaceAll("\\s+", " "))
                .map(skill -> ALIASES.getOrDefault(skill, skill))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public static Set<String> normalize(Set<String> skills) {

        if (skills == null || skills.isEmpty()) {
            return Set.of();
        }

        return skills.stream()
                .filter(skill -> skill != null && !skill.isBlank())
                .map(skill -> skill.trim().toLowerCase(Locale.ROOT)
                        .replaceAll("\\s+", " "))
                .map(skill -> ALIASES.getOrDefault(skill, skill))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }
}
