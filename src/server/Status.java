package server;

public enum Status {
    SUCCESFUL(200), NOT_FOUND(404), FORBIDDEN(403);
    public final int code;

    Status(int i) {
        code = i;
    }
}
