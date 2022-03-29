package com.jaldeeinc.jaldee.Interface;

import com.jaldeeinc.jaldee.model.SelectedSlotDetail;

import java.util.List;

public interface ISelectSlotInterface {

    void sendSelectedTime(String time, String displayTime, int scheduleId);
    void sendSelectedTime(List<SelectedSlotDetail> selectedSlotDetails);
}
