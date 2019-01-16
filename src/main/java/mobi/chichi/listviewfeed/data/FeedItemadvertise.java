package mobi.chichi.listviewfeed.data;

public class FeedItemadvertise {

    private int id;
    private String userid, name, status, image, startdate, enddate, oprog, link;


    public FeedItemadvertise() {
    }


    public FeedItemadvertise(int id, String name, String userid, String image, String status, String startdate, String enddate, String oprog, String link) {
        super();
        this.id = id;
        this.name = name;
        this.userid = userid;
        this.image = image;
        this.status = status;
        this.startdate = startdate;
        this.enddate = enddate;
        this.oprog = oprog;
        this.link = link;
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


    public String getstartdate() {
        return startdate;
    }


    public void setstartdate(String startdate) {
        this.startdate = startdate;
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


    public String getenddate() {
        return enddate;
    }


    public void setenddate(String enddate) {
        this.enddate = enddate;
    }


    public String getoprog() {
        return oprog;
    }


    public void setoprog(String oprog) {
        this.oprog = oprog;
    }


    public String getlink() {
        return link;
    }


    public void setlink(String link) {
        this.link = link;
    }

}
