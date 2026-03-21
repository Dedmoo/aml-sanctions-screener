package com.mehmetserin.aml.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class SanctionsScreeningService {

    public record WatchEntry(String id, String name, String listType) {}
    public record Hit(String watchlistId, String matchedName, String listType, int distance, double score) {}
    public record ScreenResult(String query, String decision, int riskScore, List<Hit> hits) {}

    private final List<WatchEntry> watchlist = List.of(
            new WatchEntry("WL-1", "Ivan Petrov", "SANCTIONS"),
            new WatchEntry("WL-2", "Acme Trading LLC", "SANCTIONS"),
            new WatchEntry("WL-3", "Maria Santos", "PEP"),
            new WatchEntry("WL-4", "Northern Shadow Holdings", "SANCTIONS"),
            new WatchEntry("WL-5", "John Example", "PEP")
    );

    public ScreenResult screen(String rawName) {
        if (rawName == null || rawName.isBlank()) {
            throw new IllegalArgumentException("Name is required.");
        }
        String query = normalize(rawName);
        if (query.length() < 3) {
            throw new IllegalArgumentException("Name must be at least 3 characters after normalization.");
        }

        List<Hit> hits = new ArrayList<>();
        for (WatchEntry entry : watchlist) {
            String candidate = normalize(entry.name());
            int distance = levenshtein(query, candidate);
            int maxLen = Math.max(query.length(), candidate.length());
            double similarity = maxLen == 0 ? 1.0 : 1.0 - ((double) distance / maxLen);

            boolean strongContains = (query.length() >= 5 && candidate.contains(query))
                    || (candidate.length() >= 5 && query.contains(candidate));
            if (similarity >= 0.72 || strongContains) {
                double score = Math.max(similarity, strongContains ? 0.80 : 0.0);
                if ("SANCTIONS".equals(entry.listType())) {
                    score = Math.min(1.0, score + 0.05);
                }
                hits.add(new Hit(entry.id(), entry.name(), entry.listType(), distance, round(score)));
            }
        }
        hits.sort((a, b) -> Double.compare(b.score(), a.score()));

        int risk = hits.isEmpty() ? 0 : (int) Math.min(100, Math.round(hits.get(0).score() * 100));
        // Any watchlist hit must at least go to REVIEW; never CLEAR with hits.
        String decision;
        if (hits.isEmpty()) {
            decision = "CLEAR";
        } else if (risk >= 85) {
            decision = "BLOCK";
        } else {
            decision = "REVIEW";
            risk = Math.max(risk, 72);
        }
        return new ScreenResult(rawName.trim(), decision, risk, hits);
    }

    private static String normalize(String value) {
        return value.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9 ]", " ").replaceAll("\\s+", " ").trim();
    }

    private static double round(double v) {
        return Math.round(v * 1000.0) / 1000.0;
    }

    public static int levenshtein(String a, String b) {
        int[][] dp = new int[a.length() + 1][b.length() + 1];
        for (int i = 0; i <= a.length(); i++) dp[i][0] = i;
        for (int j = 0; j <= b.length(); j++) dp[0][j] = j;
        for (int i = 1; i <= a.length(); i++) {
            for (int j = 1; j <= b.length(); j++) {
                int cost = a.charAt(i - 1) == b.charAt(j - 1) ? 0 : 1;
                dp[i][j] = Math.min(Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1), dp[i - 1][j - 1] + cost);
            }
        }
        return dp[a.length()][b.length()];
    }
}
