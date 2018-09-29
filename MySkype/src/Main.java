
import Handler.StandardInputHandler;
import Handler.StateHandler;
import Net.Server;
import States.State;
import States.StateReady;

import java.io.IOException;

public class Main {

    public static void waitForQuitSignal(StateHandler state) {
        while (!state.shouldQuit()) {
            try {
                Thread.sleep(33);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws IOException {

        System.out.println("testing commit from stationary pc");

        StateHandler stateHandler = StateHandler.getInstance();

        stateHandler.setState(new StateReady(null));

        try {
            stateHandler.server = new Server(Integer.parseInt(args[0]));
            stateHandler.server.start();
        } catch (IndexOutOfBoundsException | NumberFormatException e) {
            System.err.println(Strings.SERVER_ARG_ERR + " " + Strings.APP_TERM);
        } catch (IOException ioe) {
            System.err.println(Strings.SERVER_START_ERR + " " + Strings.APP_TERM);
        }

        StandardInputHandler userInput = new StandardInputHandler();
        userInput.start();

        waitForQuitSignal(stateHandler);

        System.out.println("Program terminated.");

        userInput.close();

    }
}
