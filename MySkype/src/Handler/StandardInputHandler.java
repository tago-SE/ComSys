package Handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class StandardInputHandler extends Thread {

    private StateHandler stateHandler;
    private BufferedReader stdin;
    private boolean run;

    public StandardInputHandler(StateHandler stateHandler) {
        this.stateHandler = stateHandler;
        stdin  = new BufferedReader(new InputStreamReader(System.in));
        run = true;
    }

    @Override
    public void start() {
        while (run) {
            try {
                if (stdin.ready()) {
                    stateHandler.parseCommand(stdin.readLine());
                }
            } catch (IOException e) {
                System.err.println("Standard I/O error.");
                run = false;
            }
        }
        try {
            stdin.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Standard I/O closing...");
    }
}
