package squarerock.track.model;

import java.io.Serializable;

/**
 * Created by pranavkonduru on 7/28/16.
 */

public class Todo implements Serializable{

    public Long _id;
    public String name;
    public String notes;
    public String status;
    public int priority;
    public String date;
    public String time;

    public Todo(){
        name = "";
        priority = 0;
        notes = "";
        status = "0";
        date = "";
        time = "";
    }

}
