package kirk.calculator;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class ShowHelp extends Activity {

        // private static final String TAG = "ShowMessage";

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_showhelp);

            // Intent in = getIntent();

            // String message = in.getStringExtra("message");

            // Log.i(TAG, "in onCreate()");


            TextView textView = (TextView) findViewById(R.id.helptext);

            //textView.setText(android.text.Html.fromHtml("<h1>Calculator Help</h1>" + "hello <br> </br>yes v"));
            textView.setText(android.text.Html.fromHtml(getString(R.string.help_text)));
        }
}
