package State;

public class StateSpeaking extends State {

    @Override
    public synchronized State hangup() {
        return new StateHangingUp();
    }
}
