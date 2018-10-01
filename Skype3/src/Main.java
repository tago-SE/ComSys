import Handler.StateHandler;
import State.StateReady;
import State.StateRinging;

public class Main {

    public static void main(String[] args) {
        StateHandler handler = StateHandler.getInstance();
        handler.startUserInput();
        handler.startServer(Integer.parseInt(args[0]));
        handler.setState(new StateReady());
    }
}
