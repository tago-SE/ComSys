/*public enum State {
    Ready, Waiting, Calliong, Speaking, Hangingup;
}
*/

public abstract class PhoneState implements PhoneInt {

    static PhoneState instance;     // Not sure

    private PhoneState state;       // Not sure


    public PhoneState() {
        instance = this;
    }

    @Override
    public void call (String name, int port) {
        throw new IllegalStateException();
    }

    @Override
    public void answer() {
        throw new IllegalStateException();
    }

    @Override
    public void hangup() {
        throw new IllegalStateException();
    }

    @Override
    public void ring() {
        throw new IllegalStateException();
    }

    public PhoneState setState(PhoneState state) {
        this.state = state;
        return state;
    }

    public PhoneState getState() {
        return state;
    }

}