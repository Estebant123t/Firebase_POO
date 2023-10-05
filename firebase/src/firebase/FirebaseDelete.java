
package firebase;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.concurrent.CountDownLatch;

public class FirebaseDelete {

    public static void main(String[] args) {
        String itemIdToDelete = "1";

        new FirebaseDelete().deleteItem(itemIdToDelete);
    }

    private FirebaseDatabase firebaseDatabase;

    /**
     * Inicialización de Firebase.
     */
    private void initFirebase() {
        try {
            FirebaseOptions firebaseOptions = new FirebaseOptions.Builder()
                    .setDatabaseUrl("https://tarea3-1e4ac-default-rtdb.firebaseio.com/")
                    .setServiceAccount(new FileInputStream(new File("C:\\Users\\412DA\\Desktop\\firebase\\tarea3-1e4ac-firebase-adminsdk-ivp9o-0c8ea2f15c.json")))
                    .build();

            FirebaseApp.initializeApp(firebaseOptions);
            firebaseDatabase = FirebaseDatabase.getInstance();
            System.out.println("La conexion se realizo exitosamente...");
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    private void deleteItem(String itemId) {
        if (itemId != null) {
            initFirebase();

            /* Get database root reference */
            DatabaseReference databaseReference = firebaseDatabase.getReference("/");

            /* Get a reference to the item to delete */
            DatabaseReference itemReference = databaseReference.child("item").child(itemId);

            /**
             * The Firebase Java client uses daemon threads, meaning it will not prevent a process from exiting.
             * So we'll wait(countDownLatch.await()) until Firebase completes the delete operation.
             * Then decrement `countDownLatch` value using `countDownLatch.countDown()` and application will continue its execution.
             */
            CountDownLatch countDownLatch = new CountDownLatch(1);

            itemReference.removeValue(new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError de, DatabaseReference dr) {
                    if (de == null) {
                        System.out.println("Registro eliminado exitosamente.");
                    } else {
                        System.err.println("Error al eliminar el registro: " + de.getMessage());
                    }
                    // Decrement countDownLatch value to continue application execution
                    countDownLatch.countDown();
                }
            });

            try {
                // Wait for Firebase to complete the delete operation
                countDownLatch.await();
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }
}
