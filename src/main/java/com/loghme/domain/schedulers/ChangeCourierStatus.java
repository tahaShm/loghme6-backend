package com.loghme.domain.schedulers;

import com.loghme.domain.utils.Order;
import com.loghme.repository.LoghmeRepository;

import java.util.TimerTask;

public class ChangeCourierStatus extends TimerTask {
    private int orderId;

    public ChangeCourierStatus(int orderId) {this.orderId = orderId;}

    public void run() {
//        order.setStatus("done");
        LoghmeRepository.getInstance().updateOrderStatus("done", orderId);
        cancel();
    }
}
