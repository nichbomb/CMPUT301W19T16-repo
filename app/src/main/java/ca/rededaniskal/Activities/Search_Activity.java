/* TYPE:
 * Activity
 *
 * PURPOSE:
 * Search for books and other others
 *
 * ISSUES:
 * Needs pretty much everything
 *
 */
package ca.rededaniskal.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import ca.rededaniskal.R;

public class Search_Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }
}