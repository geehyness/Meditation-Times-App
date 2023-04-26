package com.yukisoft.hlcmt.JavaRepositories.Comparators;

import com.yukisoft.hlcmt.JavaRepositories.Models.AudioModel;

import java.util.Comparator;

public class AudioComparator implements Comparator<AudioModel> {
    public int compare(AudioModel a, AudioModel q) {
        if (a.getDatePreached().before(q.getDatePreached())) {
            return 1;
        } else if (a.getDatePreached().after(q.getDatePreached())) {
            return -1;
        } else {
            return 0;
        }
    }
}
