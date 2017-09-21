package mn.lecturesummarizer;

/**
 * Created by anand on 9/16/2017.
 */

public class LectureNotes {
    private  int id;
    private  String title;
    private  String transcript;
    private  String summary;

    public LectureNotes(){
    }

    public LectureNotes(String Title, String Transcript, String Summary){
        super();
        this.title = Title;
        this.transcript = Transcript;
        this.summary = Summary;
    }

    //Getter Function

    public String getTitle(){
        return title;
    }

    public String getTranscript(){
        return transcript;
    }
    public String getSummary(){
        return summary;
    }

    public  void setId(int id){
        this.id = id;

    }

    public  void setTitle(String title){
        this.title = title;
    }

    public  void setSummary(String summary){
        this.summary = summary;
    }

    public  void setTranscript(String transcript){
        this.transcript = transcript;
    }


}
