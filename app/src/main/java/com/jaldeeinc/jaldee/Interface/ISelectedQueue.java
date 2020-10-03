package com.jaldeeinc.jaldee.Interface;

import com.jaldeeinc.jaldee.response.QueueTimeSlotModel;

public interface ISelectedQueue {
    void sendSelectedQueue(String displayQueueTime, QueueTimeSlotModel queue, int id);
}
