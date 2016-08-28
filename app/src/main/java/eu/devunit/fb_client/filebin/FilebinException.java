package eu.devunit.fb_client.filebin;

import eu.devunit.fb_client.filebin.Answer.Base.ErrorAnswer;
import eu.devunit.fb_client.filebin.Answer.Base.IAnswer;

/**
 * Created by sebastian on 8/28/16.
 */
public class FilebinException extends Exception {
    private ErrorAnswer errorAnswer;

    public FilebinException(IAnswer answer) {
        this((ErrorAnswer) answer);
    }

    public FilebinException(ErrorAnswer errorAnswer) {
        super(errorAnswer.getMessage());
        this.errorAnswer = errorAnswer;
    }

    public ErrorAnswer getErrorAnswer() {
        return errorAnswer;
    }
}
