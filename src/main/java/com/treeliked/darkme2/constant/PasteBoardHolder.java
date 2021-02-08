package com.treeliked.darkme2.constant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 粘贴板工具
 *
 * @author lss
 * @date 2020/12/28, 周一
 */
public class PasteBoardHolder {

    public static final int MAX_HISTORY_COUNT = 99;

    /**
     * key: userId，sessionId
     */
    private static Map<String, List<String>> holder;

    public static void addUserPasteHistory(String userId, String content) {
        if (holder == null) {
            synchronized (PasteBoardHolder.class) {
                if (holder == null) {
                    holder = new HashMap<>(16);
                }
            }
        } else {
            List<String> userHistory = holder.getOrDefault(userId, new ArrayList<>());
            userHistory.add(0, content);
            if (userHistory.size() > MAX_HISTORY_COUNT) {
                userHistory.remove(MAX_HISTORY_COUNT);
            }
            holder.put(userId, userHistory);
        }
    }

    public static String getUserLatestPaste(String userId) {
        return getUserPasteHistory(userId).get(0);
    }

    public static List<String> getUserPasteHistory(String userId) {
        if (holder == null) {
            return new ArrayList<>();
        }
        return holder.getOrDefault(userId, new ArrayList<>());
    }

}
