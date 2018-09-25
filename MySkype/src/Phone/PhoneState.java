package Phone;/*public enum State {
    Ready, Waiting, Calliong, Speaking, Hangingup;
}
*/

public abstract class PhoneState implements PhoneInt {

    public static PhoneState instance;

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

    @Override
    public void acknowledge() {
        throw new IllegalStateException();
    }

    public static PhoneState setState(PhoneState state) {
        instance = state;
        return instance;
    }
}