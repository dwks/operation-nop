package ca.ubc.cs.nop;

public interface RequestHandler {
    public void onSuccess(String response);
    public void onFailure();
}
