import Handler.StateHandler;
import Handler.UserInput;

public class Main {

    public static void main(String[] args) {
        StateHandler handler = StateHandler.getInstance();
        handler.startUserInput();
        handler.startServer(Integer.parseInt(args[0]));

    }
}
