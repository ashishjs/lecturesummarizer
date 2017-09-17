package mn.lecturesummarizer;


import android.app.Activity;
import android.os.Bundle;
import android.widget.Toolbar;

import mn.lecturesummarizer.R;

public class ViewSummary extends Activity{
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewsummary);
       // toolbar=(Toolbar)findViewById(R.id.courseTitle);

    }
}
