/* TYPE:
 * Activity
 *
 * PURPOSE:
 * Add a Book to your library
 *
 * ISSUES:
 *
 */
package ca.rededaniskal.Activities;
//author : Skye, Revan, Daniela

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import ca.rededaniskal.BusinessLogic.AddBookLogic;


import ca.rededaniskal.BusinessLogic.FetchBook;
import ca.rededaniskal.BusinessLogic.NetworkUtils;
import ca.rededaniskal.BusinessLogic.SearchBookDetails;
import ca.rededaniskal.BusinessLogic.UseGoogleBooksAPI;
import ca.rededaniskal.EntityClasses.Book_Instance;

import ca.rededaniskal.Barcode.Barcode_Scanner_Activity;


import ca.rededaniskal.R;

/**
 * This activity lets a user input information about a book, and then adds it to their library
 * in the database.
 *
 * Todo for part 5:
 * Make the user's photo saved in the database
 */

public class Add_Book_To_Library_Activity extends AppCompatActivity {

    private static final String TAG = "Add_Book_To_Library_Activity";

    private FirebaseAuth mAuth;
    private DatabaseReference bookRef;

    //UI stuff
    private EditText addTitle, addAuthor, addISBN, addDescription;
    private Button openScanner, addBook;
    private FloatingActionButton openCamera;
    private ImageView cover;
    private String isbn;
    private String returnString;

    private AddBookLogic businessLogic;

    //For Camera
    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book_instance);

        //Set buttons and EditTexts
        addTitle = findViewById(R.id.addTitle);
        addAuthor = findViewById(R.id.addAuthor);
        addISBN = findViewById(R.id.addISBN);

        openScanner = findViewById(R.id.openScanner);
        openCamera = findViewById(R.id.openCamera);
        addBook = findViewById(R.id.addBook);

        cover = findViewById(R.id.BookCover);

        openScanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), Barcode_Scanner_Activity.class);
                startActivityForResult(intent, 1);
            }
        });

        openCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkSelfPermission(Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA},
                                MY_CAMERA_PERMISSION_CODE);
                } else {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                }
            }
        });

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               //on click, gets info from the edittext field, validates them in AddBookLogic
                // calls addBookInstance() which creates the database object to add the book
                //Once the book is added, its details are passed to View_My_Library, and the
                // view is refreshed
                getInfo();
                validateFields();
                Book_Instance book = addBookInstance();

                Intent intent = new Intent(v.getContext(), View_My_Library_Activity.class);
                //intent.putExtra("book", book);
                startActivity(intent);
                //getParent().finish();
                finish();

            }
        };

        addBook.setOnClickListener(onClickListener);

    }

    public void getInfo() {
        String Title = addTitle.getText().toString();
        String Author = addAuthor.getText().toString();
        String ISBN = addISBN.getText().toString();

        businessLogic = new AddBookLogic(Title, Author, ISBN);
    }

    public void validateFields() {

        //Currently raises no errors, businessLogic is always Valid
        //TODO: implement this when testing stages are done for basic functionality
        String error = businessLogic.validateTitle();
        if (!error.equals("")){
            addTitle.setError(error);

        }
        String error1 = businessLogic.validateAuthor();
        if (!error1.equals("")){
            addAuthor.setError(error1);

        }
    String error2 = businessLogic.validateISBN();
        if (!error2.equals("")){
            addISBN.setError(error2);
        }
    }

    //This adds the books to the database.

    public Book_Instance addBookInstance() {

        if (businessLogic.isValid()) {

            String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
            String Title = addTitle.getText().toString();
            String Author = addAuthor.getText().toString();
            String ISBN = addISBN.getText().toString();

            Book_Instance bookInstance = new Book_Instance(Title, Author, ISBN, userID, userID, "Good", "a");
            AddBookDb db = new AddBookDb();
            String id = db.addBookToDatabase(bookInstance);
            bookInstance.setBookID(id);
            return bookInstance;

        }
        return null;
    }

    //Code From https://stackoverflow.com/a/5991757
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                Intent cameraIntent = new
                        Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            } else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }

        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            cover.setImageBitmap(photo);
        }
        else if (requestCode == 1 && resultCode == Activity.RESULT_OK){
            isbn = data.getStringExtra("ISBN");
            ArrayList<String> strings;
            searchBooks(isbn);
            /*addTitle.setText(strings.get(0));
            addAuthor.setText(strings.get(1));*/
            addISBN.setText(isbn);
        }
    }


    public void searchBooks(String isbn) {
        // Get the search string from the input field.
        // String queryString = mBookInput.getText().toString();
        ArrayList strings = new ArrayList<String>();
        // Hide the keyboard when the button is pushed.
        /*InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);*/

        // Check the status of the network connection.
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // If the network is active and the search field is not empty,
        // add the search term to the arguments Bundle and start the loader.
        if (networkInfo != null && networkInfo.isConnected() && isbn!= null) {
            /*Bundle queryBundle = new Bundle();
            queryBundle.putString("isbn", isbn);
            LoaderManager.getInstance(this).restartLoader(0, queryBundle, this);*/
            new UseGoogleBooksAPI(this, addTitle, addAuthor).execute("9780316015844");
            //return stringGetter(returnString);
        }
        // Otherwise update the TextView to tell the user there is no connection or no search term.
        /*else {
            strings.add("Here is title text");
            strings.add("Here is author text");
        }*/
        //return strings;
    }





    /*public ArrayList<String> searchBooks(String isbn) {
        // Get the search string from the input field.
        // String queryString = mBookInput.getText().toString();
        ArrayList strings = new ArrayList<String>();
        // Hide the keyboard when the button is pushed.
        /*InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);

        // Check the status of the network connection.
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // If the network is active and the search field is not empty,
        // add the search term to the arguments Bundle and start the loader.
        if (networkInfo != null && networkInfo.isConnected() && isbn!= null) {
            /*Bundle queryBundle = new Bundle();
            queryBundle.putString("isbn", isbn);
            LoaderManager.getInstance(this).restartLoader(0, queryBundle, this);
            returnString = NetworkUtils.getBookInfo("9780316015844");
            return stringGetter(returnString);
        }
        // Otherwise update the TextView to tell the user there is no connection or no search term.
        else {
            strings.add("Here is title text");
            strings.add("Here is author text");
        }
        return strings;
    }*/


    /**
     * Loader Callbacks
     */

    /**
     * The LoaderManager calls this method when the loader is created.
     *
     * @param id ID integer to id   entify the instance of the loader.
     * @param args The bundle that contains the search parameter.
     * @return Returns a new BookLoader containing the search term.
     */
    /*
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new BookLoader(parent, isbn);
    }*/

    /**
     * Called when the data has been loaded. Gets the desired information from
     * the JSON and updates the Views.
     *
     The loader that has finished.
     * @param data The JSON response from the Books API.
     */

    public ArrayList<String> stringGetter(String data) {
        ArrayList<String> strings = new ArrayList<String>();
        try {
            // Convert the response into a JSON object.
            JSONObject jsonObject = new JSONObject(data);
            // Get the JSONArray of book items.
            JSONArray itemsArray = jsonObject.getJSONArray("items");

            // Initialize iterator and results fields.
            int i = 0;
            String title = null;
            String authors = null;

            // Look for results in the items array, exiting when both the title and author
            // are found or when all items have been checked.
            while (i < itemsArray.length() || (authors == null && title == null)) {
                // Get the current item information.
                JSONObject book = itemsArray.getJSONObject(i);
                JSONObject volumeInfo = book.getJSONObject("volumeInfo");

                // Try to get the author and title from the current item,
                // catch if either field is empty and move on.
                try {
                    strings.add(volumeInfo.getString("title"));
                    strings.add(volumeInfo.getString("authors"));
                } catch (Exception e){
                    e.printStackTrace();
                }

                // Move to the next item.
                i++;
            }

            // If both are found, display the result.
            /*if (title != null && authors != null){
                mAuthorText.setText(authors);
            } else {
                // If none are found, update the UI to show failed results.
                mTitleText.setText("NoResult");
                mAuthorText.setText("");
            }*/

        } catch (Exception e){
            // If onPostExecute does not receive a proper JSON string, update the UI to show failed results.
            strings.add("Error with try");
            strings.add("Error with attempt");
            e.printStackTrace();
        }
        return strings;


    }

    /**
     * In this case there are no variables to clean up when the loader is reset.
     *
     * @paramThe loader that was reset.
     */






//-------------------EMBEDDED DATABASE CLASS----------------//
    //TODO: Improve encapsulation?

private class AddBookDb {

    FirebaseDatabase db;
    DatabaseReference bookRef;
    String success;

    public AddBookDb() {
        //Creates a new reference to the correct path in the Firebase
        //Book instances are stored under there unique id, under my-books,
        //under unique user Uid, under book-instnces.

        String user = FirebaseAuth.getInstance().getCurrentUser().getUid();
        this.db = FirebaseDatabase.getInstance();
        this.bookRef = db.getReference().child("book-instances")
                .child(user)
        .child("my-books");

    }

    public String addBookToDatabase(Book_Instance bookInstance) throws NullPointerException{


        success =bookRef.push().getKey();
        bookInstance.setBookID(success);
        Log.d(TAG, "***********---->" +bookInstance.getBookID());

        DatabaseReference m = FirebaseDatabase.getInstance().getReference("all_books");
        //Gets key and sets unique book id;

        String key = m.push().getKey();
        m.child(key).setValue(bookInstance);
        //Stores value
        //TODO: update master-book

        if (bookRef.child(success).setValue(bookInstance).isSuccessful()){
            return success;

        }
        else return null;





    }


}
}