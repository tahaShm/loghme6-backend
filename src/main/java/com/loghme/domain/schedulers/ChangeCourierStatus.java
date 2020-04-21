package com.loghme.domain.schedulers;

import com.loghme.domain.utils.Order;

import java.util.TimerTask;

public class ChangeCourierStatus extends TimerTask {
    private Order order;

    public ChangeCourierStatus(Order order) {this.order = order;}

    public void run() {
        order.setStatus("done");
        cancel();
    }
}
