package Net;

import java.io.Closeable;
import java.io.IOException;

@Deprecated
public interface ClientInt extends Closeable {
    void write(String msg) throws IOException;
}
