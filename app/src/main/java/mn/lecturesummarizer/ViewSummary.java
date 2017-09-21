package mn.lecturesummarizer;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import mn.lecturesummarizer.R;

import static mn.lecturesummarizer.R.id.Transcript;

public class ViewSummary extends Activity {
    Toolbar toolbar;
    TextView summary, transcriptext, course;
    final Context context = this;
    private String class_Name = "";
    public String Summarytext;
    Button analyze;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewsummary);
//      Bundle bundle=getIntent().getExtras();
        summary = (TextView) findViewById(R.id.summarytext);
        transcriptext = (TextView) findViewById(R.id.transcripttext);
        toolbar = (Toolbar) findViewById(R.id.courseTitle);
        analyze = (Button) findViewById(R.id.button);
        // toolbar.setTitle(transcriptext.toString());
        Bundle bd = getIntent().getExtras();
        int getName = (Integer) bd.get("name");
        MySQLiteHelper sqLiteHelper = new MySQLiteHelper(this);
        final LectureNotes myNote = sqLiteHelper.getLecture(getName + 1);
        toolbar.setTitle(myNote.getTitle().toString());
        transcriptext.setText(myNote.getTranscript());
        summary.setText(myNote.getSummary().toString());
        analyze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    final String mJSONURLString = "https://private.jlrickert.me/api/summarize/";
                    // Initialize a new RequestQueue instance
                    final RequestQueue requestQueue = Volley.newRequestQueue(context);

                    JSONObject LectureData = new JSONObject();
                    try {
                        LectureData.put("data", Transcript);
                    } catch (Exception e) {
                        Log.d("data", myNote.getTranscript().toString());
                        //Toast.makeText(this,e.toString(),Toast.LENGTH_SHORT).show();
                    }
                    // Initialize a new JsonObjectRequest instance
                    // String mJSONURLString;
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, "https://private.jlrickert.me/api/summarize/", LectureData,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    // Do something with response
                                    //mTextView.setText(response.toString());
                                    //   Toast.makeText("This",response.toString(),Toast.LENGTH_SHORT).show();
                                    try {

                                        if (response.toString() != null) {
                                            Summarytext = response.toString();
                                            //  ifEverythingWorksOut();
                                        } else {
                                            MySQLiteHelper sqLiteHelper = new MySQLiteHelper(context);
                                           // sqLiteHelper.addLecture(new LectureNotes(class_Name, Transcript, "Not enough sentences for Summary"));
                                            Log.d("Database Result", sqLiteHelper.getAllNotes().toString());
                                            Toast.makeText(context, "Sentences Too Short!! Please Try Again!!", Toast.LENGTH_SHORT).show();

                                        }
                                    } catch (Exception e) {
                                        MySQLiteHelper sqLiteHelper = new MySQLiteHelper(context);
                                       // sqLiteHelper.addLecture(new LectureNotes(class_Name, Transcript, "Not enough sentences for Summary"));
                                        Log.d("Database Result", sqLiteHelper.getAllNotes().toString());
                                        Toast.makeText(context, "Sentences Too Short!! Please Try Again!!", Toast.LENGTH_SHORT).show();

                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    // Do something when error occurred
                                    MySQLiteHelper sqLiteHelper = new MySQLiteHelper(context);
                                   // sqLiteHelper.addLecture(new LectureNotes(class_Name, Transcript, "Not enough sentences for Summary"));
                                    Log.d("Database Result", sqLiteHelper.getAllNotes().toString());
                                    Toast.makeText(context, "Sentences Too Short!! Please Try Again!!", Toast.LENGTH_SHORT).show();

                                }
                            }
                    );
                    requestQueue.add(jsonObjectRequest);

            }
        });


    }

}

