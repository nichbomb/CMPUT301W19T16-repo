package ca.rededaniskal.EntityClasses;

/**
 * Represents a book that the user has begun borrowing
 */
public class Check_In extends Book_Exchange {

    public Check_In(String owner, String borrower, String isbn, int bookid) {
        super(owner, borrower, isbn, bookid);
    }

    public void updateBookStatus(){}

    public void addBookToBorrowed(){}
    // These lists are in User profile
}
