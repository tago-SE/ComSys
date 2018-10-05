package State;

public class StateWaiting extends State {

    @Override
    public State hangup() {
        return new StateReady();
    }

    @Override
    public synchronized State recievedTROAck() {
        return new StateSpeaking();
    }
}
