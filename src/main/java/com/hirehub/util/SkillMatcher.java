
package com.hirehub.util;

import java.util.Set;

public final class SkillMatcher {

    private SkillMatcher() {
    }

    public static int calculateScore(
            Set<String> candidateSkills,
            String jobSkills) {

        Set<String> preferredSkills =
                SkillNormalizer.normalize(candidateSkills);

        Set<String> requiredSkills =
                SkillNormalizer.normalize(jobSkills);

        if (preferredSkills.isEmpty()) {
            return 0;
        }

        long matchedSkills = preferredSkills.stream()
                .filter(requiredSkills::contains)
                .count();

        return (int) Math.round(
                (matchedSkills * 50.0) / preferredSkills.size()
        );
    }
}
