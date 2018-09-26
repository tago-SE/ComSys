package Handler;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;

public class StandardInputHandler extends Thread implements Closeable {

    private final StateHandler stateHandler = StateHandler.getInstance();
    private BufferedReader stdin;
    private boolean run;


    public StandardInputHandler() {
        stdin  = new BufferedReader(new InputStreamReader(System.in));
        run = true;
    }

    @Override
    public void run() {
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

    @Override
    public void close() throws IOException {
        run = false;
        interrupt();
    }
}
