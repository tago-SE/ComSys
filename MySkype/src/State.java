/*public enum State {
    Ready, Waiting, Calliong, Speaking, Hangingup;
}
*/

public abstract class State implements PhoneInt {

    protected State state;

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

}