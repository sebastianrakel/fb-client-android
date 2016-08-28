package eu.devunit.fb_client.filebin.Answer.Base;

/**
 * Created by sebastian on 8/28/16.
 */
public class ErrorAnswer extends BaseAnswer implements IAnswer {
    private String message;
    private String errorId;

    @Override
    public boolean isSuccess() {
        return false;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getErrorId() {
        return errorId;
    }

    public void setErrorId(String errorId) {
        this.errorId = errorId;
    }
}
