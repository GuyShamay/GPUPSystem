package exception;

public class TargetExistException extends RuntimeException {

    public TargetExistException(String dupTarget) {
        super("There is already a target named: " + dupTarget);
    }
}
