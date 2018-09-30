package Net;

import java.io.Closeable;
import java.io.IOException;

public interface HostInt extends Closeable {

    public void write(String msg) throws IOException;

    public void setTimeout(int time);

}
