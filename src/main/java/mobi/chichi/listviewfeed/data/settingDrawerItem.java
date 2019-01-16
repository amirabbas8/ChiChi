package mobi.chichi.listviewfeed.data;

public class settingDrawerItem {

    private String title;
    private int icon;
    private String count = "0";
    // boolean to set visiblity of the counter
    private boolean isCounterVisible = false;
    private int isp = 0;


    public settingDrawerItem() {
    }


    public settingDrawerItem(int isp) {

        this.isp = isp;
    }


    public settingDrawerItem(String title) {
        this.title = title;

    }


    public settingDrawerItem(String title, int icon, int isp) {
        this.title = title;
        this.icon = icon;
        this.isp = isp;
    }


    public settingDrawerItem(String title, int icon, boolean isCounterVisible, String count, int isp) {
        this.title = title;
        this.icon = icon;
        this.isCounterVisible = isCounterVisible;
        this.count = count;
        this.isp = isp;
    }


    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getIcon() {
        return this.icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getCount() {
        return this.count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public int getIsp() {
        return this.isp;
    }

    public boolean getCounterVisibility() {
        return this.isCounterVisible;
    }

    public void setCounterVisibility(boolean isCounterVisible) {
        this.isCounterVisible = isCounterVisible;
    }


    public void setisp(int isp) {
        this.isp = isp;
    }
}
