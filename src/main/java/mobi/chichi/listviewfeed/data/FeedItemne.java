package mobi.chichi.listviewfeed.data;

public class FeedItemne {

    private int id;
    private String userid, postid, name, status, image, profilePic, timeStamp, url, nlike, ncomment, mylike,
            lastcommentuserid, lastcommentname, lastcommentprofilePic, lastcomment, lprog, oprog, fragment;


    public FeedItemne() {
    }


    public FeedItemne(int id, String name, String userid, String postid, String image, String status,
                      String profilePic, String timeStamp, String url, String nlike, String ncomment, String mylike,
                      String lastcommentname, String lastcommentuserid, String lastcommentprofilePic, String lastcomment,
                      String lprog, String oprog, String fragment) {
        super();
        this.id = id;
        this.name = name;
        this.userid = userid;
        this.image = image;
        this.status = status;
        this.profilePic = profilePic;
        this.timeStamp = timeStamp;
        this.url = url;
        this.nlike = nlike;
        this.ncomment = ncomment;
        this.mylike = mylike;
        this.lastcommentname = lastcommentname;
        this.lastcommentuserid = lastcommentuserid;
        this.lastcommentprofilePic = lastcommentprofilePic;
        this.lastcomment = lastcomment;
        this.postid = postid;
        this.lprog = lprog;
        this.oprog = oprog;
        this.fragment = fragment;
    }


    public int getId() {
        return id;
    }


    public void setId(int id) {
        this.id = id;
    }


    public String getUserId() {
        return userid;
    }


    public void setUserId(String userid) {
        this.userid = userid;
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public String getImage() {
        return image;
    }


    public void setImage(String image) {
        this.image = image;
    }


    public String getStatus() {
        return status;
    }


    public void setStatus(String status) {
        this.status = status;
    }


    public String getProfilePic() {
        return profilePic;
    }


    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }


    public String getTimeStamp() {
        return timeStamp;
    }


    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }


    public String getUrl() {
        return url;
    }


    public void setUrl(String url) {
        this.url = url;
    }


    public String getNLike() {
        return nlike;
    }


    public void setNLike(String nlike) {
        this.nlike = nlike;
    }


    public String getNComment() {
        return ncomment;
    }


    public void setNComment(String ncomment) {
        this.ncomment = ncomment;
    }


    public String getMylike() {
        return mylike;
    }


    public void setMylike(String mylike) {
        this.mylike = mylike;
    }


    public String getLastCommentUserId() {
        return lastcommentuserid;
    }


    public void setLastCommentUserId(String lastcommentuserid) {
        this.lastcommentuserid = lastcommentuserid;
    }


    public String getLastCommentName() {
        return lastcommentname;
    }


    public void setLastCommentName(String lastcommentname) {
        this.lastcommentname = lastcommentname;
    }


    public String getLastCommentProfilePic() {
        return lastcommentprofilePic;
    }


    public void setLastCommentProfilePic(String lastcommentprofilePic) {
        this.lastcommentprofilePic = lastcommentprofilePic;
    }


    public String getLastComment() {
        return lastcomment;
    }


    public void setLastComment(String lastcomment) {
        this.lastcomment = lastcomment;
    }


    public String getPostID() {
        return postid;
    }


    public void setPostID(String postid) {
        this.postid = postid;
    }


    public String getlprog() {
        return lprog;
    }


    public void setlprog(String lprog) {
        this.lprog = lprog;
    }


    public String getoprog() {
        return oprog;
    }


    public void setoprog(String oprog) {
        this.oprog = oprog;
    }


    public String getFragment() {
        return fragment;
    }


    public void setfragment(String fragment) {
        this.fragment = fragment;
    }
}
