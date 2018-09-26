package Phone;

public abstract class State {

    public boolean isBusy() {
        throw new IllegalStateException();
    }

    public void error() {
        throw new IllegalStateException();
    }

    public void recievedInvite() {
        throw new IllegalStateException();
    }

    public void sendInvite() {
        throw new IllegalStateException();
    }

    public void recievedTRO() {
        throw new IllegalStateException();
    }

    public void sendTRO() {
        throw new IllegalStateException();
    }

    public void recievedTROAcknowledge() {

    }

    public void sendTROAcknowledge() {

    }

}
