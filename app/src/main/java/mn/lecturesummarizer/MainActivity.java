//package mn.lecturesummarizer;
//
package mn.lecturesummarizer;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private String class_Name = "";
    final Context context = this;
    private TextView txtSpeechInput;
    private ImageButton btnSpeak;
    public  String mJSONURLString = "https://private.jlrickert.me/api/summarize/";
    public  String Transcript;
    public  String Summary;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    public File myFile;
    public  boolean classCreationSucessfull = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtSpeechInput = (TextView) findViewById(R.id.txtSpeechInput);
        btnSpeak = (ImageButton) findViewById(R.id.btnSpeak);
        // hide the action bar
//        getActionBar().hide();
        //createDirectoryForClass(context);
        btnSpeak.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                promptSpeechInput();
            }
        });

    }


    /**
     * Showing google speech input dialog
     * */
    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Receiving speech input
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    txtSpeechInput.setText(result.get(0));
                    Toast.makeText(getApplicationContext(),result.get(0),Toast.LENGTH_SHORT).show();
                    if(result.get(0).toString().length() > 0){
                        Transcript = result.get(0).toString();
                        renderAlertDialog();
                    }
                }
                break;
            }

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }



    private void renderAlertDialog(){
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setTitle("Save the lecture as::");
        final EditText input = new EditText(context);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        alertBuilder.setView(input);
        alertBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                class_Name = input.getText().toString();
                getRequestToPythonClient();
            }
        });

        alertBuilder.show();
    }

    public void getRequestToPythonClient()
    {
        // Initialize a new RequestQueue instance
        final RequestQueue requestQueue = Volley.newRequestQueue(context);

        JSONObject LectureData = new JSONObject();
        try{
            LectureData.put("data",Transcript);
        }
        catch (Exception e){
            Toast.makeText(context,e.toString(),Toast.LENGTH_SHORT).show();
        }
        // Initialize a new JsonObjectRequest instance
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,mJSONURLString,LectureData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Do something with response
                        //mTextView.setText(response.toString());
                        Toast.makeText(context,response.toString(),Toast.LENGTH_SHORT).show();
                        try {
                            if(response.toString() != null)
                            {
                                Summary = response.toString();
                                ifEverythingWorksOut();
                            }
                            else{
                                MySQLiteHelper sqLiteHelper = new MySQLiteHelper(context);
                                //sqLiteHelper.addLecture(new LectureNotes(class_Name,Transcript,"Not enough sentences for Summary"));
                                Log.d("Database Result",sqLiteHelper.getAllNotes().toString());
                                Toast.makeText(context,"Sentences Too Short!! Please Try Again!!",Toast.LENGTH_SHORT).show();

                            }
                        }

                        catch (Exception e){
                            MySQLiteHelper sqLiteHelper = new MySQLiteHelper(context);
                           // sqLiteHelper.addLecture(new LectureNotes(class_Name,Transcript,"Not enough sentences for Summary"));
                            Log.d("Database Result",sqLiteHelper.getAllNotes().toString());
                            Toast.makeText(context,"Sentences Too Short!! Please Try Again!!",Toast.LENGTH_SHORT).show();

                        }
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error){
                        // Do something when error occurred
                        MySQLiteHelper sqLiteHelper = new MySQLiteHelper(context);
                        //sqLiteHelper.addLecture(new LectureNotes(class_Name,Transcript,"Not enough sentences for Summary"));
                        Log.d("Database Result",sqLiteHelper.getAllNotes().toString());
                        Toast.makeText(context,"Sentences Too Short!! Please Try Again!!",Toast.LENGTH_SHORT).show();

                    }
                }
        );
        requestQueue.add(jsonObjectRequest);
    }

    private  void ifEverythingWorksOut(){
        MySQLiteHelper sqLiteHelper = new MySQLiteHelper(context);
        sqLiteHelper.addLecture(new LectureNotes(class_Name,Transcript,Summary));
        Log.d("Database Result",sqLiteHelper.getAllNotes().toString());
    }




}