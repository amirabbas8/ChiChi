package mobi.chichi;

import android.content.Context;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class UserFunctions {

    private static String getprofilelist = "http://chichi.mobi/getprofilelist1.php";
    private static String gethomelist = "http://chichi.mobi/gethomelist1.php";
    private static String getsearchlist = "http://chichi.mobi/getsearchlist1.php";
    private static String getsendtolist = "http://chichi.mobi/getsendtolist.php";
    private static String getcommentlist = "http://chichi.mobi/getcommentlist.php";
    private static String getfriendlist = "http://chichi.mobi/getfriendlist.php";

    private static String write_tag = "write";
    private static String delete_tag = "delete";
    private JSONParser jsonParser;

    // constructor
    public UserFunctions() {
        jsonParser = new JSONParser();
    }
    public JSONObject getmylike_writingslistloadmore(String id, String pid) {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("tag", "getmylike_writingslistloadmore"));
        params.add(new BasicNameValuePair("id", id));
        params.add(new BasicNameValuePair("pid", pid));
       // params.add(new BasicNameValuePair("taggs", taggs));
        String getmylike_writingslist = "http://chichi.mobi/getmylike_writingslist.php";
        return jsonParser.getJSONFromUrl(getmylike_writingslist, params);

    }


    public JSONObject getmylike_writingslist(String id) {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("tag", "getmylike_writingslist"));
        params.add(new BasicNameValuePair("id", id));
      //  params.add(new BasicNameValuePair("taggs", taggs));
        String getmylike_writingslist = "http://chichi.mobi/getmylike_writingslist.php";
        return jsonParser.getJSONFromUrl(getmylike_writingslist, params);

    }

    public JSONObject deletewritings(String postid, String userid) {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("tag", "deletewritings"));
        params.add(new BasicNameValuePair("postid", postid));
        params.add(new BasicNameValuePair("userid", userid));
        String deletewritings = "http://chichi.mobi/deletewritings.php";
        return jsonParser.getJSONFromUrl(deletewritings, params);

    }

    public JSONObject dislike_writings(String id, String postid, String userid) {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("tag", "dislike_writings"));
        params.add(new BasicNameValuePair("id", id));
        params.add(new BasicNameValuePair("postid", postid));
        params.add(new BasicNameValuePair("userid", userid));
        String dislike_writings = "http://chichi.mobi/dislike_writings.php";
        return jsonParser.getJSONFromUrl(dislike_writings, params);

    }


    public JSONObject like_writings(String id, String postid, String userid, String name, String profilepic) {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("tag", "like_writings"));
        params.add(new BasicNameValuePair("id", id));
        params.add(new BasicNameValuePair("postid", postid));
        params.add(new BasicNameValuePair("userid", userid));
        params.add(new BasicNameValuePair("name", name));
        params.add(new BasicNameValuePair("profilepic", profilepic));
        String like_writings = "http://chichi.mobi/like_writings.php";
        return jsonParser.getJSONFromUrl(like_writings, params);

    }


    public JSONObject getbestwritingslist(String id) {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("tag", "getbestwritingslist"));
        params.add(new BasicNameValuePair("id", id));
        String getbestwritingslist = "http://chichi.mobi/getbestwritingslist.php";
        return jsonParser.getJSONFromUrl(getbestwritingslist, params);

    }


    public JSONObject getbestwritingslistloadmore(String id, String pid) {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("tag", "getbestwritingslistloadmore"));
        params.add(new BasicNameValuePair("id", id));
        params.add(new BasicNameValuePair("pid", pid));
        String getbestwritingslistloadmore = "http://chichi.mobi/getbestwritingslist.php";
        return jsonParser.getJSONFromUrl(getbestwritingslistloadmore, params);

    }


    public JSONObject getbestpeoplelist(String id) {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("tag", "getbestpeoplelist"));
        params.add(new BasicNameValuePair("id", id));
        String getbestpeoplelist = "http://chichi.mobi/getbestpeoplelist.php";
        return jsonParser.getJSONFromUrl(getbestpeoplelist, params);

    }


    public JSONObject getwritingslistloadmore(String id, String pid, String taggs) {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("tag", "getwritingslistloadmore"));
        params.add(new BasicNameValuePair("id", id));
        params.add(new BasicNameValuePair("pid", pid));
        params.add(new BasicNameValuePair("taggs", taggs));
        String getwritingslist = "http://chichi.mobi/getwritingslist.php";
        return jsonParser.getJSONFromUrl(getwritingslist, params);

    }


    public JSONObject getwritingslist(String id, String taggs) {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("tag", "getwritingslist"));
        params.add(new BasicNameValuePair("id", id));
        params.add(new BasicNameValuePair("taggs", taggs));
        String getwritingslist = "http://chichi.mobi/getwritingslist.php";
        return jsonParser.getJSONFromUrl(getwritingslist, params);

    }


    public JSONObject addwriting(String id, String text, String pic, String name, String taggs) {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("tag", "addwriting"));
        params.add(new BasicNameValuePair("id", id));
        params.add(new BasicNameValuePair("text", text));
        params.add(new BasicNameValuePair("pic", pic));
        params.add(new BasicNameValuePair("name", name));
        params.add(new BasicNameValuePair("taggs", taggs));
        String writing = "http://chichi.mobi/addwriting.php";
        return jsonParser.getJSONFromUrl(writing, params);

    }


    public JSONObject editwrite(String postid,String id, String text, String link, String pic, String name, String imagename, String taggeduser) {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("tag", "editwrite"));
        params.add(new BasicNameValuePair("postid", postid));
        params.add(new BasicNameValuePair("id", id));
        params.add(new BasicNameValuePair("text", text));
        params.add(new BasicNameValuePair("link", link));
        params.add(new BasicNameValuePair("pic", pic));
        params.add(new BasicNameValuePair("name", name));
        params.add(new BasicNameValuePair("imagename", imagename));
        params.add(new BasicNameValuePair("taggeduser", taggeduser));
        String write = "http://chichi.mobi/editwrite.php";
        return jsonParser.getJSONFromUrl(write, params);
    }


    public JSONObject getdatausername(String username) {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<>();
        String getdata_tag = "getdata";
        params.add(new BasicNameValuePair("tag", getdata_tag));
        params.add(new BasicNameValuePair("username", username));
        String getdata = "http://chichi.mobi/getdatausername.php";
        return jsonParser.getJSONFromUrl(getdata, params);

    }


    public JSONObject editemailprofile(String id, String email) {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("tag", "editemailprofile"));
        params.add(new BasicNameValuePair("id", id));
        params.add(new BasicNameValuePair("email", email));
        String editemailprofile = "http://chichi.mobi/editemailprofile.php";
        return jsonParser.getJSONFromUrl(editemailprofile, params);

    }


    public JSONObject sendemailverify( String email, String code) {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("tag", "sendemailverify"));
        params.add(new BasicNameValuePair("email", email));
        params.add(new BasicNameValuePair("code", code));
        String sendemailverify = "http://chichi.mobi/sendemailverify.php";
        return jsonParser.getJSONFromUrl(sendemailverify, params);

    }


    public JSONObject support(String id,String subject,String feedback,String profilepic,String name,String imagename) {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("tag", "support"));
        params.add(new BasicNameValuePair("id", id));
        params.add(new BasicNameValuePair("subject", subject));
        params.add(new BasicNameValuePair("feedback", feedback));
        params.add(new BasicNameValuePair("profilepic", profilepic));
        params.add(new BasicNameValuePair("name", name));
        params.add(new BasicNameValuePair("imagename", imagename));
        return jsonParser.getJSONFromUrl("http://chichi.mobi/support.php", params);

    }


    public JSONObject getfriendlistloadmore(String id, String pid) {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("tag", "getfriendlistloadmore"));
        params.add(new BasicNameValuePair("id", id));
        params.add(new BasicNameValuePair("pid", pid));
        return jsonParser.getJSONFromUrl(getfriendlist, params);

    }


    public JSONObject report(String id, String userid, String kind, String comment) {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("tag", "report"));
        params.add(new BasicNameValuePair("id", id));
        params.add(new BasicNameValuePair("userid", userid));
        params.add(new BasicNameValuePair("kind", kind));
        params.add(new BasicNameValuePair("comment", comment));
        String report = "http://chichi.mobi/report.php";
        return jsonParser.getJSONFromUrl(report, params);

    }


    public JSONObject getad() {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("tag", "getad"));

        String getad = "http://chichi.mobi/getad.php";
        return jsonParser.getJSONFromUrl(getad, params);

    }


    public JSONObject advertiseedit(String id, String advertiseid, String name, String imagename, String link) {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("tag", "advertiseedit"));
        params.add(new BasicNameValuePair("id", id));
        params.add(new BasicNameValuePair("advertiseid", advertiseid));
        params.add(new BasicNameValuePair("name", name));
        params.add(new BasicNameValuePair("imagename", imagename));
        params.add(new BasicNameValuePair("link", link));
        String advertiseedit = "http://chichi.mobi/advertiseedit.php";
        return jsonParser.getJSONFromUrl(advertiseedit, params);

    }


    public JSONObject advertisedisactive(String id) {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("tag", "advertisedisactive"));
        params.add(new BasicNameValuePair("id", id));

        String advertisedisactive = "http://chichi.mobi/advertisedisactive.php";
        return jsonParser.getJSONFromUrl(advertisedisactive, params);

    }


    public JSONObject advertiseactive(String id) {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("tag", "advertiseactive"));
        params.add(new BasicNameValuePair("id", id));

        String advertiseactive = "http://chichi.mobi/advertiseactive.php";
        return jsonParser.getJSONFromUrl(advertiseactive, params);

    }


    public JSONObject payadvertise(String advertiseid, String userid) {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("tag", "payadvertise"));
        params.add(new BasicNameValuePair("advertiseid", advertiseid));
        params.add(new BasicNameValuePair("userid", userid));
        String payadvertise = "http://chichi.mobi/payadvertise.php";
        return jsonParser.getJSONFromUrl(payadvertise, params);

    }


    public JSONObject deleteadvertise(String advertiseid, String userid) {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("tag", "deleteadvertise"));
        params.add(new BasicNameValuePair("advertiseid", advertiseid));
        params.add(new BasicNameValuePair("userid", userid));
        String deleteadvertise = "http://chichi.mobi/deleteadvertise.php";
        return jsonParser.getJSONFromUrl(deleteadvertise, params);

    }


    public JSONObject advertisecheck(String id) {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("tag", "advertisecheck"));
        params.add(new BasicNameValuePair("id", id));

        String advertisecheck = "http://chichi.mobi/advertisecheck.php";
        return jsonParser.getJSONFromUrl(advertisecheck, params);

    }


    public JSONObject advertiseplus(String id, String name, String imagename, String link) {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("tag", "advertiseplus"));
        params.add(new BasicNameValuePair("id", id));
        params.add(new BasicNameValuePair("name", name));
        params.add(new BasicNameValuePair("imagename", imagename));
        params.add(new BasicNameValuePair("link", link));
        String advertiseplus = "http://chichi.mobi/advertiseplus.php";
        return jsonParser.getJSONFromUrl(advertiseplus, params);

    }


    public JSONObject getadvertiselist(String id) {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("tag", "advertise"));
        params.add(new BasicNameValuePair("id", id));
        String getadvertiselist = "http://chichi.mobi/getadvertiselist.php";
        return jsonParser.getJSONFromUrl(getadvertiselist, params);

    }


    public JSONObject verifydaric(String id, String trans_id, String ndaric, String phone) {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("tag", "verifydaric"));
        params.add(new BasicNameValuePair("id", id));
        params.add(new BasicNameValuePair("trans_id", trans_id));
        params.add(new BasicNameValuePair("ndaric", ndaric));
        params.add(new BasicNameValuePair("phone", phone));
        String verifydaric = "http://chichi.mobi/verifydaric.php";
        return jsonParser.getJSONFromUrl(verifydaric, params);

    }


    public JSONObject buydaric(String id, String ndaric) {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("tag", "buydaric"));
        params.add(new BasicNameValuePair("id", id));
        params.add(new BasicNameValuePair("amount", ndaric));
        String buydaric = "http://chichi.mobi/buydaric.php";
        return jsonParser.getJSONFromUrl(buydaric, params);

    }


    public JSONObject getphonefriendlist(String id, String myid) {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("tag", "getphonefriendlist"));
        params.add(new BasicNameValuePair("id", id));
        params.add(new BasicNameValuePair("myid", myid));
        String getphonefriendlist = "http://chichi.mobi/getphonefriendlist.php";
        return jsonParser.getJSONFromUrl(getphonefriendlist, params);

    }


    public JSONObject getsearchlistloadmore(String searchtext, String pid) {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("tag", "getsearchtextloadmore"));
        params.add(new BasicNameValuePair("searchtext", searchtext));
        params.add(new BasicNameValuePair("pid", pid));
        return jsonParser.getJSONFromUrl(getsearchlist, params);

    }


    public JSONObject getsearchlist(String searchtext) {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("tag", "getsearchtext"));
        params.add(new BasicNameValuePair("searchtext", searchtext));
        return jsonParser.getJSONFromUrl(getsearchlist, params);

    }


    public JSONObject getpost(String id) {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("tag", "getpost"));
        params.add(new BasicNameValuePair("id", id));
        String getpost = "http://chichi.mobi/getpost.php";
        return jsonParser.getJSONFromUrl(getpost, params);

    }


    public JSONObject getnewlistloadmore(String id, String pid) {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("tag", "getnewlistloadmore"));
        params.add(new BasicNameValuePair("id", id));
        params.add(new BasicNameValuePair("pid", pid));
       String getnewlist = "http://chichi.mobi/getnewlist1.php";
        return jsonParser.getJSONFromUrl(getnewlist, params);

    }


    public JSONObject getnewlist(String id) {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("tag", "getnewlist"));
        params.add(new BasicNameValuePair("id", id));
      String getnewlist = "http://chichi.mobi/getnewlist1.php";
        return jsonParser.getJSONFromUrl(getnewlist, params);

    }


    public JSONObject deletecomment(String comid, String postid, String lastcomment, String lastcommentname, String lastcommentuserid, String lastcommentprofilePic) {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("tag", "deletecomment"));
        params.add(new BasicNameValuePair("comid", comid));
        params.add(new BasicNameValuePair("postid", postid));
        params.add(new BasicNameValuePair("lastcomment", lastcomment));
        params.add(new BasicNameValuePair("lastcommentname", lastcommentname));
        params.add(new BasicNameValuePair("lastcommentuserid", lastcommentuserid));
        params.add(new BasicNameValuePair("lastcommentprofilePic", lastcommentprofilePic));
        String deletecomment = "http://chichi.mobi/deletecomment.php";
        return jsonParser.getJSONFromUrl(deletecomment, params);

    }


    public JSONObject deletepost(String postid, String userid) {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("tag", delete_tag));
        params.add(new BasicNameValuePair("postid", postid));
        params.add(new BasicNameValuePair("userid", userid));
        String deletepost = "http://chichi.mobi/deletepost.php";
        return jsonParser.getJSONFromUrl(deletepost, params);

    }


    public JSONObject hidepost(String postid, String tag) {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("tag", tag));
        params.add(new BasicNameValuePair("postid", postid));
        String hidepost = "http://chichi.mobi/hidepost.php";
        return jsonParser.getJSONFromUrl(hidepost, params);

    }


    public JSONObject unblock(String id, String fid) {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("tag", "unblock"));
        params.add(new BasicNameValuePair("id", id));
        params.add(new BasicNameValuePair("fid", fid));
        String unblock = "http://chichi.mobi/unblock.php";
        return jsonParser.getJSONFromUrl(unblock, params);

    }


    public JSONObject getblocklist(String id) {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<>();
        String getblocklist_tag = "getblocklist";
        params.add(new BasicNameValuePair("tag", getblocklist_tag));
        params.add(new BasicNameValuePair("id", id));
        String getblocklist = "http://chichi.mobi/getblocklist.php";
        return jsonParser.getJSONFromUrl(getblocklist, params);

    }


    public JSONObject block(String id, String fid, String fname, String fprofilepic) {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("tag", "block"));
        params.add(new BasicNameValuePair("id", id));
        params.add(new BasicNameValuePair("fid", fid));
        params.add(new BasicNameValuePair("fname", fname));
        params.add(new BasicNameValuePair("fprofilepic", fprofilepic));
        String block = "http://chichi.mobi/block.php";
        return jsonParser.getJSONFromUrl(block, params);

    }


    public JSONObject blockun(String id, String fid, String fname, String fprofilepic) {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("tag", "block"));
        params.add(new BasicNameValuePair("id", id));
        params.add(new BasicNameValuePair("fid", fid));
        params.add(new BasicNameValuePair("fname", fname));
        params.add(new BasicNameValuePair("fprofilepic", fprofilepic));
        String blockun = "http://chichi.mobi/blockun.php";
        return jsonParser.getJSONFromUrl(blockun, params);

    }


    public JSONObject delete(String id, String fid) {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("tag", delete_tag));
        params.add(new BasicNameValuePair("id", id));
        params.add(new BasicNameValuePair("fid", fid));
        String delete = "http://chichi.mobi/delete.php";
        return jsonParser.getJSONFromUrl(delete, params);

    }


    public JSONObject getfriendlist(String id) {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<>();
        String getfriendlist_tag = "getfriendlist";
        params.add(new BasicNameValuePair("tag", getfriendlist_tag));
        params.add(new BasicNameValuePair("id", id));
        return jsonParser.getJSONFromUrl(getfriendlist, params);

    }


    public JSONObject getnotificationlist(String id) {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<>();
        String getnotificationlist_tag = "getnotification";
        params.add(new BasicNameValuePair("tag", getnotificationlist_tag));
        params.add(new BasicNameValuePair("id", id));
        String notification = "http://chichi.mobi/notification1.php";
        return jsonParser.getJSONFromUrl(notification, params);

    }


    public JSONObject addordel(String id, String fid, String fname, String fprofilepic, String myname, String myprofilepic) {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("tag", "addordel"));
        params.add(new BasicNameValuePair("id", id));
        params.add(new BasicNameValuePair("fid", fid));
        params.add(new BasicNameValuePair("fname", fname));
        params.add(new BasicNameValuePair("fprofilepic", fprofilepic));
        params.add(new BasicNameValuePair("myname", myname));
        params.add(new BasicNameValuePair("myprofilepic", myprofilepic));
        String addordel = "http://chichi.mobi/addordel.php";
        return jsonParser.getJSONFromUrl(addordel, params);

    }


    public JSONObject ffcheck(String id, String userid) {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("tag", "ffusernamecheck"));
        params.add(new BasicNameValuePair("id", id));
        params.add(new BasicNameValuePair("userid", userid));
        String isfriend = "http://chichi.mobi/ffusernamecheck.php";
        return jsonParser.getJSONFromUrl(isfriend, params);

    }


    public JSONObject getbestlist(String id) {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("tag", "getbestlistn1"));
        params.add(new BasicNameValuePair("id", id));
        String getbestlist = "http://chichi.mobi/getbestlist1.php";
        return jsonParser.getJSONFromUrl(getbestlist, params);

    }


    public JSONObject comwrite(String id, String postid, String text, String profilepic, String name, String userid) {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("tag", "comwrite"));
        params.add(new BasicNameValuePair("id", id));
        params.add(new BasicNameValuePair("text", text));
        params.add(new BasicNameValuePair("profilepic", profilepic));
        params.add(new BasicNameValuePair("name", name));
        params.add(new BasicNameValuePair("postid", postid));
        params.add(new BasicNameValuePair("userid", userid));
        String comwrite = "http://chichi.mobi/comwrite.php";
        return jsonParser.getJSONFromUrl(comwrite, params);

    }


    public JSONObject getcommentlist(String id, String postid) {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("tag", "getcommentlist"));
        params.add(new BasicNameValuePair("id", id));
        params.add(new BasicNameValuePair("postid", postid));
        return jsonParser.getJSONFromUrl(getcommentlist, params);

    }


    public JSONObject getcommentlistloadmore(String id, String postid, String pid) {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("tag", "getcommentlistloadmore"));
        params.add(new BasicNameValuePair("id", id));
        params.add(new BasicNameValuePair("postid", postid));
        params.add(new BasicNameValuePair("pid", pid));
        return jsonParser.getJSONFromUrl(getcommentlist, params);

    }


    public JSONObject dislike(String id, String postid, String userid) {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<>();
        String dislike_tag = "dislike";
        params.add(new BasicNameValuePair("tag", dislike_tag));
        params.add(new BasicNameValuePair("id", id));
        params.add(new BasicNameValuePair("postid", postid));
        params.add(new BasicNameValuePair("userid", userid));
        String dislike = "http://chichi.mobi/dislike.php";
        return jsonParser.getJSONFromUrl(dislike, params);

    }


    public JSONObject like(String id, String postid, String userid, String name, String profilepic) {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<>();
        String like_tag = "like";
        params.add(new BasicNameValuePair("tag", like_tag));
        params.add(new BasicNameValuePair("id", id));
        params.add(new BasicNameValuePair("postid", postid));
        params.add(new BasicNameValuePair("userid", userid));
        params.add(new BasicNameValuePair("name", name));
        params.add(new BasicNameValuePair("profilepic", profilepic));
        String like = "http://chichi.mobi/like.php";
        return jsonParser.getJSONFromUrl(like, params);

    }


    public JSONObject getsendtolistloadmore(String id, String pid) {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("tag", "getsendtolistloadmore"));
        params.add(new BasicNameValuePair("id", id));
        params.add(new BasicNameValuePair("pid", pid));
        return jsonParser.getJSONFromUrl(getsendtolist, params);

    }


    public JSONObject gethomelistloadmore(String id, String pid) {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("tag", "gethomelistloadmore"));
        params.add(new BasicNameValuePair("id", id));
        params.add(new BasicNameValuePair("pid", pid));
        return jsonParser.getJSONFromUrl(gethomelist, params);

    }


    public JSONObject getsendtolist(String id) {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<>();
        String getsendtolist_tag = "getsendtolist";
        params.add(new BasicNameValuePair("tag", getsendtolist_tag));
        params.add(new BasicNameValuePair("id", id));
        return jsonParser.getJSONFromUrl(getsendtolist, params);

    }


    public JSONObject gethomelist(String id) {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<>();
        String gethomelist_tag = "gethomelist";
        params.add(new BasicNameValuePair("tag", gethomelist_tag));
        params.add(new BasicNameValuePair("id", id));
        return jsonParser.getJSONFromUrl(gethomelist, params);

    }


    public JSONObject addffusername(String id, String fid, String fname, String fprofilepic, String myname, String myprofilepic) {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<>();
        String ffadd_tag = "ffadd";
        params.add(new BasicNameValuePair("tag", ffadd_tag));
        params.add(new BasicNameValuePair("id", id));
        params.add(new BasicNameValuePair("fid", fid));
        params.add(new BasicNameValuePair("fname", fname));
        params.add(new BasicNameValuePair("fprofilepic", fprofilepic));
        params.add(new BasicNameValuePair("myname", myname));
        params.add(new BasicNameValuePair("myprofilepic", myprofilepic));
        String ffadd = "http://chichi.mobi/ffadd.php";
        return jsonParser.getJSONFromUrl(ffadd, params);

    }


    public JSONObject getffusername(String id) {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("tag", "ffusername"));
        params.add(new BasicNameValuePair("id", id));
        String ffusername = "http://chichi.mobi/ffusername.php";
        return jsonParser.getJSONFromUrl(ffusername, params);

    }


    public JSONObject usernamecheck(String id, String username) {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<>();
        String usernamecheck_tag = "usernamecheck";
        params.add(new BasicNameValuePair("tag", usernamecheck_tag));
        params.add(new BasicNameValuePair("id", id));
        params.add(new BasicNameValuePair("username", username));
        String usernamecheck = "http://chichi.mobi/usernamecheck.php";
        return jsonParser.getJSONFromUrl(usernamecheck, params);

    }


    public JSONObject usernameset(String id, String username) {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<>();
        String usernameset_tag = "usernameset";
        params.add(new BasicNameValuePair("tag", usernameset_tag));
        params.add(new BasicNameValuePair("id", id));
        params.add(new BasicNameValuePair("username", username));
        String usernameset = "http://chichi.mobi/usernameset.php";
        return jsonParser.getJSONFromUrl(usernameset, params);

    }


    public JSONObject getprofilelist(String id, String userid) {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<>();
        String getprofilelist_tag = "getprofilelist";
        params.add(new BasicNameValuePair("tag", getprofilelist_tag));
        params.add(new BasicNameValuePair("id", id));
        params.add(new BasicNameValuePair("userid", userid));
        return jsonParser.getJSONFromUrl(getprofilelist, params);

    }


    public JSONObject getprofilelistloadmore(String id, String userid, String pid) {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("tag", "getprofilelistloadmore"));
        params.add(new BasicNameValuePair("id", id));
        params.add(new BasicNameValuePair("userid", userid));
        params.add(new BasicNameValuePair("pid", pid));
        return jsonParser.getJSONFromUrl(getprofilelist, params);

    }


    public JSONObject editprofileimage(String id, String profileimage) {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<>();
        String editprofileimage_tag = "editprofileimage";
        params.add(new BasicNameValuePair("tag", editprofileimage_tag));
        params.add(new BasicNameValuePair("id", id));
        params.add(new BasicNameValuePair("profileimage", profileimage));
        String editprofileimage = "http://chichi.mobi/editprofileimage.php";
        return jsonParser.getJSONFromUrl(editprofileimage, params);

    }


    public JSONObject write(String id, String text, String link, String pic, String name, String imagename, String taggeduser) {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("tag", write_tag));
        params.add(new BasicNameValuePair("id", id));
        params.add(new BasicNameValuePair("text", text));
        params.add(new BasicNameValuePair("link", link));
        params.add(new BasicNameValuePair("pic", pic));
        params.add(new BasicNameValuePair("name", name));
        params.add(new BasicNameValuePair("imagename", imagename));
        params.add(new BasicNameValuePair("taggeduser", taggeduser));
        String write = "http://chichi.mobi/write.php";
        return jsonParser.getJSONFromUrl(write, params);

    }


    public JSONObject sendtowrite(String id, String text, String link, String pic, String name, String imagename, String sendtouserid, String fname, String fprofilePic) {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("tag", write_tag));
        params.add(new BasicNameValuePair("id", id));
        params.add(new BasicNameValuePair("text", text));
        params.add(new BasicNameValuePair("link", link));
        params.add(new BasicNameValuePair("pic", pic));
        params.add(new BasicNameValuePair("name", name));
        params.add(new BasicNameValuePair("imagename", imagename));
        params.add(new BasicNameValuePair("sendtouserid", sendtouserid));
        params.add(new BasicNameValuePair("fname", fname));
        params.add(new BasicNameValuePair("fprofilePic", fprofilePic));
        String sendtowrite = "http://chichi.mobi/sendtowrite.php";
        return jsonParser.getJSONFromUrl(sendtowrite, params);

    }


    public JSONObject editprofile(String id, String realname, String bio, String gender) {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<>();
        String editprofile_tag = "editprofile";
        params.add(new BasicNameValuePair("tag", editprofile_tag));
        params.add(new BasicNameValuePair("id", id));
        params.add(new BasicNameValuePair("realname", realname));
        params.add(new BasicNameValuePair("bio", bio));
        params.add(new BasicNameValuePair("gender", gender));
        String editprofile = "http://chichi.mobi/editprofile.php";
        return jsonParser.getJSONFromUrl(editprofile, params);

    }


    public JSONObject getdata(String id) {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<>();
        String getdata_tag = "getdata";
        params.add(new BasicNameValuePair("tag", getdata_tag));
        params.add(new BasicNameValuePair("id", id));
        String getdata = "http://chichi.mobi/getdata.php";
        return jsonParser.getJSONFromUrl(getdata, params);

    }


    public JSONObject realnamelogin(String id, String realname) {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<>();
        String realnamelogin_tag = "login";
        params.add(new BasicNameValuePair("tag", realnamelogin_tag));
        params.add(new BasicNameValuePair("id", id));
        params.add(new BasicNameValuePair("realname", realname));
        String realnamelogin = "http://chichi.mobi/realnamelogin.php";
        return jsonParser.getJSONFromUrl(realnamelogin, params);

    }


    public JSONObject PhoneLogin2(String contrycode, String phone, String code, String regId) {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<>();
        String phonelogin_tag = "phonelogin";
        params.add(new BasicNameValuePair("tag", phonelogin_tag));
        params.add(new BasicNameValuePair("contrycode", contrycode));
        params.add(new BasicNameValuePair("phone", phone));
        params.add(new BasicNameValuePair("code", code));
        params.add(new BasicNameValuePair("regId", regId));
        String phoneloginURL2 = "http://chichi.mobi/phonelogin2.php";
        return jsonParser.getJSONFromUrl(phoneloginURL2, params);

    }


    /**
     * Function to logout user
     * Resets the temporary data stored in SQLite Database
     */
    public boolean logoutUser(Context context) {
        DatabaseHandler db = new DatabaseHandler(context);
        db.resetTables();
        return true;
    }

}
