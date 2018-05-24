package meme.app.dmcx.chatapp.Models;

public class FriendRequestModel {

    public String request_type;

    public FriendRequestModel() {}

    public FriendRequestModel(String request_type) {
        this.request_type = request_type;
    }

    public String getRequest_type() {
        return request_type;
    }

    public void setRequest_type(String request_type) {
        this.request_type = request_type;
    }
}
