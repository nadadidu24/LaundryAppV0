package my.laundryapp.app.Callback;

import java.util.List;

import my.laundryapp.app.Model.CommentModel;

public interface ICommentCallbackListener {

    void onCommentLoadSuccess(List<CommentModel> commentModels);
    void onCommentLoadFailed(String message);

}
