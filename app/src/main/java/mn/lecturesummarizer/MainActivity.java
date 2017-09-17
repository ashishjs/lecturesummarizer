package mn.lecturesummarizer;

import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOError;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;


import com.google.cloud.speech.v1.RecognitionAudio;
import com.google.cloud.speech.v1.RecognitionConfig;
import com.google.cloud.speech.v1.RecognitionConfig.AudioEncoding;
import com.google.cloud.speech.v1.RecognizeResponse;
import com.google.cloud.speech.v1.SpeechClient;
import com.google.cloud.speech.v1.SpeechRecognitionAlternative;
import com.google.cloud.speech.v1.SpeechRecognitionResult;
import com.google.protobuf.ByteString;

import static android.R.attr.data;

public class MainActivity extends AppCompatActivity {

    private String class_Name = "";
    final Context context = this;
    private TextView txtSpeechInput;
    private ImageButton btnSpeak;
    private ImageButton btnStop;
    public  String mJSONURLString = "https://private.jlrickert.me/api/summarize/";
    public  String Transcript;
    public  String Summary;
    private MediaRecorder mediaRecorder;
    String voiceStoragePath;
    private MediaPlayer mediaPlayer;
    static final String AB = "abcdefghijklmnopqrstuvwxyz";
    static Random rnd = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        hasSDCard();

        voiceStoragePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        File audioVoice = new File(voiceStoragePath + File.separator + "voices");
        if(!audioVoice.exists()){
            audioVoice.mkdir();
        }
        voiceStoragePath = voiceStoragePath + File.separator + "voices/" + generateVoiceFilename(6) + ".3gp";
        System.out.println("Audio path : " + voiceStoragePath);

        txtSpeechInput = (TextView) findViewById(R.id.txtSpeechInput);
        btnSpeak = (ImageButton) findViewById(R.id.btnSpeak);
        btnStop = (ImageButton) findViewById(R.id.btnStop);
        initializeMediaRecord();
        btnSpeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Recording Started!!",
                        Toast.LENGTH_LONG).show();
                if(mediaRecorder == null){
                    initializeMediaRecord();
                }
                startAudioRecording();
                btnSpeak.setEnabled(false);
                btnStop.setEnabled(true);
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener(){
            @Override
            public  void onClick(View v){
                mediaRecorder.stop();
                btnStop.setEnabled(false);
                btnSpeak.setEnabled(false);
                Toast.makeText(MainActivity.this, "Recording Completed",
                        Toast.LENGTH_LONG).show();
                playLastStoredAudioMusic();
                getTranscriptFromGoogleCloud();
            }
        });



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


    private  void ifEverythingWorksOut(){
        MySQLiteHelper sqLiteHelper = new MySQLiteHelper(context);
        sqLiteHelper.addLecture(new LectureNotes(class_Name,Transcript,Summary));
        Log.d("Database Result",sqLiteHelper.getAllNotes().toString());
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


        public void getRequestToPythonClient()
            {
                // Initialize a new RequestQueue instance
                RequestQueue requestQueue = Volley.newRequestQueue(context);

                JSONObject LectureData = new JSONObject();
                try{
                    LectureData.put("data",Transcript);
                }
                catch (Exception e){
                    Toast.makeText(context,e.toString(),Toast.LENGTH_SHORT).show();
                }
                // Initialize a new JsonObjectRequest instance
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                        Request.Method.POST,
                        mJSONURLString,
                        LectureData,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                // Do something with response
                                //mTextView.setText(response.toString());
                                Toast.makeText(context,response.toString(),Toast.LENGTH_SHORT).show();
                                try {
                                    JSONObject my = response.getJSONObject("result");
                                    Summary = my.getJSONObject("summary").toString();
                                    ifEverythingWorksOut();
                                    }

                                catch (Exception e){
                                    Toast.makeText(context,e.toString(),Toast.LENGTH_SHORT).show();

                                }
                            }
                        },
                        new Response.ErrorListener(){
                            @Override
                            public void onErrorResponse(VolleyError error){
                                // Do something when error occurred
                                Toast.makeText(context,error.toString(),Toast.LENGTH_SHORT).show();
                            }
                        }
                );
                requestQueue.add(jsonObjectRequest);
            }



    private String generateVoiceFilename( int len ){
        StringBuilder sb = new StringBuilder( len );
        for( int i = 0; i < len; i++ )
            sb.append( AB.charAt( rnd.nextInt(AB.length()) ) );
        return sb.toString();
    }

    private void startAudioRecording(){
        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stopAudioRecording(){
        if(mediaRecorder != null){
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
        }

    }

    private void playLastStoredAudioMusic(){
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(voiceStoragePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.start();
    }

    private void stopAudioPlay(){
        if(mediaPlayer != null){
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private void hasSDCard(){
        Boolean isSDPresent = android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
        if(isSDPresent)        {
            System.out.println("There is SDCard");
        }
        else{
            System.out.println("There is no SDCard");
        }
    }

    private void mediaPlayerPlaying(){
        if(!mediaPlayer.isPlaying()){
            stopAudioPlay();
        }
    }

    private void initializeMediaRecord(){
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setOutputFile(voiceStoragePath);
    }


    private void getTranscriptFromGoogleCloud(){
        try{
            Toast.makeText(context,"Google Cloud",Toast.LENGTH_SHORT);
            SpeechClient speech = SpeechClient.create();
            String fileName = voiceStoragePath;
            Path path = Paths.get(fileName);
            byte[] data = Files.readAllBytes(path);
            ByteString audioBytes = ByteString.copyFrom(data);

            RecognitionConfig config = RecognitionConfig.newBuilder()
                    .setEncoding(AudioEncoding.LINEAR16)
                    .setSampleRateHertz(16000)
                    .setLanguageCode("en-us")
                    .build();

            RecognitionAudio audio = RecognitionAudio.newBuilder()
                    .setContent(audioBytes)
                    .build();

            RecognizeResponse response = speech.recognize(config,audio);
            List<SpeechRecognitionResult> results = response.getResultsList();

            for(SpeechRecognitionResult result:results){
                List<SpeechRecognitionAlternative> alternatives = result.getAlternativesList();
                for(SpeechRecognitionAlternative alternative:alternatives){
                    System.out.printf("Transcription: %s%n", alternative.getTranscript());

                }
            }
            speech.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }



    }


    }


