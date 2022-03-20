package igorlink.donationexecutor;

import org.bukkit.entity.Player;

public abstract class DonationAction {
    private final String executionName;

    protected DonationAction(String executionName) {
        this.executionName = executionName;
    }

    public String getExecutionName() {
        return executionName;
    }

    public abstract void onAction(Player player, String donationUsername, String donationAmount);
}
