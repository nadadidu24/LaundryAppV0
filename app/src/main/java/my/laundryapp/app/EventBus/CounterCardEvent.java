package my.laundryapp.app.EventBus;

public class CounterCardEvent {
    private boolean success;

    public CounterCardEvent(boolean success){
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
