package com.jaldeeinc.jaldee.Interface;

import com.jaldeeinc.jaldee.response.QueueTimeSlotModel;

public interface ISelectQ {
    void sendSelectedQueueInfo(String displayTime, int queueId, QueueTimeSlotModel queueDetails, String selectedDate);
}
