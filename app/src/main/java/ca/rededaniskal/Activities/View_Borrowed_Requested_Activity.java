package ca.rededaniskal.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Switch;

import ca.rededaniskal.BusinessLogic.BookAdapter;
import ca.rededaniskal.Database.Data_Provider;
import ca.rededaniskal.EntityClasses.Book_List;
import ca.rededaniskal.R;

public class View_Borrowed_Requested_Activity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private BookAdapter bookAdapter;
    private Switch toggleRequested;
    private Switch toggleBorrowed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view__borrowed__requested_);
        Data_Provider dp = new Data_Provider();
        dp.bookQuery(3);
        Book_List BL =dp.getBook_list();

        recyclerView = (RecyclerView) findViewById(R.id.ViewBooks);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        bookAdapter = new BookAdapter(this, BL);
        recyclerView.setAdapter(bookAdapter);
        bookAdapter.notifyDataSetChanged();
    }
}
