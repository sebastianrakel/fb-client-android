package eu.devunit.fb_client.filebin.Answer.Base;

/**
 * Created by sebastian on 8/28/16.
 */
public class SuccessAnswer extends BaseAnswer implements IAnswer {

    public <T extends SuccessAnswer> T getAnswerAs(Class<T> type) {
        try {
            T instance = type.newInstance();

            instance.setData(this.getData());

            return instance;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean isSuccess() {
        return true;
    }
}
