package application.logic;

import java.util.Date;

public class CommentShowFormat {
    private int commentId;
    private String userUrl;
    private String userType;
    private String userName;
    private String content;
    private long date;
    private String restName;
    private String dishName;

    public CommentShowFormat(Comment comment) {
        this.commentId = comment.getId();
        this.userUrl = comment.getUserUrl();
        this.userType = comment.getUserType();
        this.userName = comment.getUserName();
        this.content = comment.getContent();
        this.restName = comment.getDish().getDishName();
        this.dishName = comment.getDish().getRestaurant().getRestaurantName();
        setUploadDate(comment.getDate());
    }

    private void setUploadDate(Date date) { //TODO change format
        this.date = date.getTime();
    }

    public int getCommentId() { return commentId; }

    public String getUserUrl() {
        return userUrl;
    }

    public String getUserType() {
        return userType;
    }

    public String getUserName() {
        return userName;
    }

    public long getDate() {
        return date;
    }

    public String getContent() {
        return content;
    }

    public String getRestName() { return restName; }

    public String getDishName() { return dishName; }
}