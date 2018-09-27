package States;

public class StateHangingUp extends StateBusy {

    @Override
    public void recievedByeAck() {
        System.out.println("Hangup completed");
    }
}
