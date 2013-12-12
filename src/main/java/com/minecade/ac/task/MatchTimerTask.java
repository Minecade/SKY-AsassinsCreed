package com.minecade.ac.task;

import org.bukkit.scheduler.BukkitRunnable;

import com.minecade.ac.engine.ACMatch;

public class MatchTimerTask extends BukkitRunnable {

    private ACMatch match;
    private int countdown;

    /**
     * Timer task constructor
     * 
     * @param game
     * @param countdown
     * @author kvnamo
     */
    public MatchTimerTask(ACMatch match, int countdown) {
        this.match = match;
        this.countdown = countdown;
    }

    /**
     * Sync task runned by bukkit scheduler
     * 
     * @author jdgil
     */
    @Override
    public void run() {
        this.countdown--;

        this.match.timeLeft(this.countdown);
        this.match.updateScoreBoard();
        this.match.updateLobbyPortal();
        if (this.countdown == 0) {
            switch (match.getStatus()) {
            case STARTING_MATCH:
                match.prepareMatch();
                this.countdown = this.match.getTimeLeft();
                return;
            case READY_TO_START:
                this.match.start();
                this.countdown = this.match.getTimeLeft();
                return;
            case RUNNING:
                this.match.verifyGameover();
                this.countdown = this.match.getTimeLeft();
                return;
            case STOPPING:
                this.match.finish(false);
                break;
            default:
                break;
            }
            super.cancel();
        }
    }

    /**
     * @param countdown
     *            the countdown to set
     */
    public void setCountdown(int countdown) {
        this.countdown = countdown;
    }
}
