
package com.hirehub.util;

import java.util.List;
import java.util.Locale;

public final class ExperienceMatcher {

    private static final List<String> EXPERIENCE_LEVELS = List.of(
        "FRESHER", "0-1", "1-2", "2-3", "3-5", "5+"
    );

    private ExperienceMatcher() {
    }

    public static int calculateScore(
            String candidateExperience,
            String jobExperience) {

        if (jobExperience == null || jobExperience.isBlank()) {
            return 0;
        }

        if (candidateExperience == null || candidateExperience.isBlank()) {
            return 0;
        }

        String candidate = candidateExperience.trim()
                .toUpperCase(Locale.ROOT);

        String job = jobExperience.trim()
                .toUpperCase(Locale.ROOT);

        int candidateIndex = EXPERIENCE_LEVELS.indexOf(candidate);
        int jobIndex = EXPERIENCE_LEVELS.indexOf(job);

        if (candidateIndex == -1 || jobIndex == -1) {
            return 0;
        }

        int difference = Math.abs(candidateIndex - jobIndex);

        if (difference == 0) {
            return 25;
        }

        if (difference == 1) {
            return 15;
        }

        return 5;
    }
}
