package State;

public class StateHangingUp extends State {

    private static final int HANGUP_TIME = 150;

    public State recievedByeAck() {
        try {
            Thread.sleep(HANGUP_TIME);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return new StateReady();
    }
}
