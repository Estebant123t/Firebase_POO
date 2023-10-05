
package firebase;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.concurrent.CountDownLatch;
import java.util.Map;
import java.util.HashMap;


public class FirebaseEdit {
    
    public static void main(String[] args) {
        Item item = new Item();
        item.setId(1L);
        item.setName("celu_4");
        item.setPrice(100.156);
        
        // You can use List<Item> also.
        new FirebaseEdit().updateItem(item);
    }
    
    private FirebaseDatabase firebaseDatabase;

    /**
     * inicialización de firebase.
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

    private void updateItem(Item item) {
        if (item != null) {
            initFirebase();
            
            DatabaseReference databaseReference = firebaseDatabase.getReference("/");
            DatabaseReference childReference = databaseReference.child("item");

            CountDownLatch countDownLatch = new CountDownLatch(1);
            
            // Convierte el objeto Item a un mapa
            Map<String, Object> itemMap = item.toMap();

            // Actualiza los valores en la base de datos
            childReference.child(item.getId().toString()).updateChildren(itemMap, new DatabaseReference.CompletionListener() {

                @Override
                public void onComplete(DatabaseError de, DatabaseReference dr) {
                    if (de == null) {
                        System.out.println("Registro actualizado exitosamente!");
                    } else {
                        System.out.println("Error al actualizar el registro: " + de.getMessage());
                    }
                    
                    countDownLatch.countDown();
                }
            });
            
            try {
                countDownLatch.await();
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }
}