package Handler;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;

public class UserInput extends Thread implements Closeable {

    private final StateHandler stateHandler;
    private BufferedReader stdin;
    private boolean run;

    public UserInput(StateHandler handler) {
        stdin  = new BufferedReader(new InputStreamReader(System.in));
        stateHandler = handler;
    }

    @Override
    public void run() {
        run = true;
        try {
            while (run) {
                if (stdin.ready()) {
                    stateHandler.parseCommand(stdin.readLine());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                close();
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }
        System.out.println("Standard I/O closed");
    }


    @Override
    public void close() throws IOException {
        run = false;
        stdin.close();
    }
}
