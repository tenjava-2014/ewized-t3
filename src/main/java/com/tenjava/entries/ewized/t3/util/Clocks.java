package com.tenjava.entries.ewized.t3.util;

import com.tenjava.entries.ewized.t3.TenJava;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;

/** Simple bukkit clocks */
public abstract class Clocks {
    public int time;

    public Clocks(int time) {
        this.time = time;
    }

    public Clocks() {}

    /** Run at first tick */
    public void first(int pos) {}

    /** Run every tick */
    public void tick(int pos) {}

    /** Run every sec */
    public void sec(int pos) {}

    /** Run last tick */
    public void last(int pos) {}

    /** Schedule tasks */
    public List<BukkitTask> run() {
        int offset = time;
        List<BukkitTask> tasks = new ArrayList<>();

        // offset = 10
        // i = 0
        // clock 10, 0  10
        // clock 10, 1  9
        for (int i = 0; i < time; i++) {
            tasks.add(Bukkit.getScheduler().runTaskLater(TenJava.get(), new Clock(time , i), offset));
            //Common.debug("{0} : {1} : {2}", time, i, offset);
            offset--;
        }

        return tasks;
    }

    /** @return the total time */
    public int getTotal() {
        return time;
    }

    /** Internal clock tracker */
    private class Clock implements Runnable {
        private int start;
        private int position;

        public Clock(int start, int position) {
            this.start = start;
            this.position = position;
        }

        @Override
        public void run() {
            // first tick
            if (position == start - 1) {
                first(position);
            }
            // last tick
            else if (position == 0) {
                last(position);
            }
            // all ticks
            else {
                // sec tick
                if (position % Common.TICK == 0) {
                    sec(position);
                }
                // normal ticks
                else {
                    tick(position);
                }
            }
        }
    }
}
