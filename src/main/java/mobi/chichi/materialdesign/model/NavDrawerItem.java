package mobi.chichi.materialdesign.model;


public class NavDrawerItem {
    private boolean showNotify;
    private String title;


    public NavDrawerItem() {

    }

    public boolean isShowNotify() {
        return showNotify;
    }

    public void setShowNotify(boolean showNotify) {
        this.showNotify = showNotify;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
