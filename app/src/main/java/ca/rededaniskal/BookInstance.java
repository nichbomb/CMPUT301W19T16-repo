package ca.rededaniskal;

//import java.awt.image.*;

import android.app.DownloadManager;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.security.cert.TrustAnchor;
import java.util.ArrayList;
import java.util.UUID;

import static android.support.constraint.Constraints.TAG;

public class BookInstance extends Book implements Serializable {

    private String owner;
    private String possessor;
    private String condition;
    private BookID bookID;

    //private BufferedImage bookImage;

    private String status;




    public BookInstance (String newTitle, String newAuthor, String newIsbn, String newOwner, String newpossessor, String newCondition, String newStatus){
        super(newTitle, newAuthor, newIsbn);
        this.bookID = null;
        this.owner = newOwner;
        this.possessor = newpossessor;
        this.condition = newCondition;
        this.status = newStatus;




    }

    public void setBookID(String key) {
        this.bookID = BookID.getInstance(key);

    }

    public String getBookID() {
        if (bookID!=null){
        return bookID.getID();}
        else return "";
    }





    public String getOwner() {
        return owner;
    }

    public String getPossessor() {
        return possessor;
    }
    public void setPossessor(String newPosessor) {
        possessor = newPosessor;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public void setStatus(String newStatus) {
        status = newStatus;
    }

    public String getStatus(){
        return status;
    }



}
