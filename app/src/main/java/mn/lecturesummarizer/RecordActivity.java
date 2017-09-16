package mn.lecturesummarizer;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class RecordActivity extends Activity{

    Button add;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.record_activity);
        add= findViewById(R.id.button2);
        add.setOnClickListener(new View.OnClickListener()
                               {
                                   @Override
                                   public void onClick(View v)
                                   {
                                       //execute command
                                   }
                               }

        );
    }
}
