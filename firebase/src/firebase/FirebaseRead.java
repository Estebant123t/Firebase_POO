package firebase;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

public class FirebaseRead {

    public static void main(String[] args) {
        new FirebaseRead().readData();
    }

    private FirebaseDatabase firebaseDatabase;

    private void initFirebase() {
        try {
            FirebaseOptions firebaseOptions = new FirebaseOptions.Builder()
                    .setDatabaseUrl("https://tarea3-1e4ac-default-rtdb.firebaseio.com/")
                    .setServiceAccount(new FileInputStream(new File("C:\\Users\\412DA\\Desktop\\firebase\\tarea3-1e4ac-firebase-adminsdk-ivp9o-0c8ea2f15c.json")))
                    .build();

            FirebaseApp.initializeApp(firebaseOptions);
            firebaseDatabase = FirebaseDatabase.getInstance();
            System.out.println("La conexión se realizó exitosamente...");
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
    }

   private void readData() {
        initFirebase();
        
        DatabaseReference databaseReference = firebaseDatabase.getReference("/");
        DatabaseReference childReference = databaseReference.child("item"); 
        
        CountDownLatch countDownLatch = new CountDownLatch(1);

        childReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Este método se activará cuando los datos se lean correctamente
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Obtén los datos como un objeto Map
                    Map<String, Object> dataMap = (Map<String, Object>) snapshot.getValue();

                    // Mapea los datos a un objeto Item
                    Item item = new Item();
                    item.setId((String) dataMap.get("id"));
                    item.setName((String) dataMap.get("name"));
                    item.setPrice((Double) dataMap.get("price"));

                    // Imprime el objeto Item
                    System.out.println("ID: " + item.getId());
                    System.out.println("Nombre: " + item.getName());
                    System.out.println("Precio: " + item.getPrice());
                }
                
                countDownLatch.countDown(); 
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.err.println("Error al leer datos: " + databaseError.getMessage());
                countDownLatch.countDown(); // Liberar el bloqueo en caso de error
            }
        });

        try {
            // Esperar hasta que se complete la lectura de datos
            countDownLatch.await();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }
}
