package com.hw.hlcmt.JavaRepositories;

import java.util.Comparator;

public class MTComparator implements Comparator<MessageModel> {
    public int compare(MessageModel a, MessageModel b) {
        int dateComparison = Integer.valueOf(b.getYear()).compareTo(a.getYear());
        return dateComparison == 0 ? Integer.valueOf(b.getWeek()).compareTo(a.getWeek()) : dateComparison;
    }
}
