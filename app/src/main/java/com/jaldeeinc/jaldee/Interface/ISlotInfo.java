package com.jaldeeinc.jaldee.Interface;

import com.jaldeeinc.jaldee.model.SelectedSlotDetail;

import java.util.List;

public interface ISlotInfo {
    void sendSlotInfo(String time, String displayTime, int scheduleId, String toString, String s);
    void sendSlotInfo(List<SelectedSlotDetail> selectedSlotDetails);
}
