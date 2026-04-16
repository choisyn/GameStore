package com.gamestore.util;

import java.util.List;

public final class GameNameFormatter {

    private static final List<String> SAMPLE_STYLE_KEYWORDS = List.of(
        "开放世界",
        "动作",
        "剧情",
        "RPG",
        "角色扮演",
        "射击",
        "生存",
        "建造",
        "模拟",
        "经营",
        "回合",
        "策略",
        "竞速",
        "驾驶",
        "恐怖",
        "解谜",
        "卡牌",
        "音乐",
        "节奏",
        "多人",
        "联机",
        "Roguelike",
        "肉鸽",
        "派对"
    );

    private GameNameFormatter() {
    }

    public static String toDisplayName(String rawName) {
        if (rawName == null) {
            return null;
        }

        String normalized = rawName.trim();
        if (normalized.isEmpty()) {
            return normalized;
        }

        int colonIndex = findColonIndex(normalized);
        int dotIndex = normalized.indexOf('·');
        if (colonIndex <= 0 || dotIndex <= colonIndex + 1 || dotIndex >= normalized.length() - 1) {
            return normalized;
        }

        String baseName = normalized.substring(0, colonIndex).trim();
        String stylePart = normalized.substring(colonIndex + 1, dotIndex).trim();
        String themePart = normalized.substring(dotIndex + 1).trim();

        if (baseName.isEmpty() || stylePart.isEmpty() || themePart.isEmpty()) {
            return normalized;
        }

        if (looksLikeGeneratedStyle(stylePart) && themePart.length() <= 12) {
            return baseName;
        }

        return normalized;
    }

    private static int findColonIndex(String name) {
        int cnColonIndex = name.indexOf('：');
        int asciiColonIndex = name.indexOf(':');

        if (cnColonIndex < 0) {
            return asciiColonIndex;
        }
        if (asciiColonIndex < 0) {
            return cnColonIndex;
        }
        return Math.min(cnColonIndex, asciiColonIndex);
    }

    private static boolean looksLikeGeneratedStyle(String stylePart) {
        String normalizedStyle = stylePart.trim();
        if (normalizedStyle.length() > 16) {
            return false;
        }

        return SAMPLE_STYLE_KEYWORDS.stream().anyMatch(normalizedStyle::contains);
    }
}
