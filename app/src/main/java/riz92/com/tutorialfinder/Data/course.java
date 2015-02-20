package riz92.com.tutorialfinder.Data;

/**
 * Created by Rizwan Asif on 12/26/2014.
 */
public class course {
    String _name;
    int _id;
    String _courseNum;
    String _link;
    String _level;
    String _details;
    String _photo;

    public course(){
    }

    public course (int id, String name, String cNum, String link, String lev, String det, String photo){
        this._name = name;
        this._id = id;
        this._courseNum = cNum;
        this._link = link;
        this._level = lev;
        this._details = det;
        this._photo = photo;
    }

    public course (String name, String cNum, String link, String lev, String det, String photo){
        this._name = name;
        this._courseNum = cNum;
        this._link = link;
        this._level = lev;
        this._details = det;
        this._photo = photo;
    }

    public String getName(){
        return this._name;
    }

    public int getId(){
        return this._id;
    }

    public String getcourseNum(){
        return this._courseNum;
    }

    public String getLink(){
        return this._link;
    }

    public String getLevel(){
        return this._level;
    }

    public String getDetails(){
        return this._details;
    }

    public String getPhoto(){
        return this._photo;
    }

    public void setName(String x){
        _name=x;
    }

    public void setId(int x){
        _id=x;
    }

    public void setcourseNum(String x){
        _courseNum=x;
    }

    public void setLink(String x){
        _link=x;
    }

    public void setLevel(String x){
        _level=x;
    }

    public void setDetails(String x){
        _details=x;
    }

    public void setPhoto(String x){
        _photo=x;
    }
}
