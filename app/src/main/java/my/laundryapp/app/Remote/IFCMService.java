package my.laundryapp.app.Remote;

import io.reactivex.Observable;
import my.laundryapp.app.Model.FCMResponse;
import my.laundryapp.app.Model.FCMSendData;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface IFCMService {
    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAA7LHjtDM:APA91bH9obB9uzenDjFDX5EEV28k8s49m6FaNnvLe6uvfO8aahXZUZrGKzCkohINUAttjXdEjGnm1DjAVeCaM73fGPJrPc_uVQpfAk4owwW8tPAi69QfNjrydfgZRhKBcn9y-mBLzroH"
    })
    @POST("fcm/send")
    Observable<FCMResponse> sendNotification (@Body FCMSendData body);

}
