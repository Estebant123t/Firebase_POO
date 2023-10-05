package firebase;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.concurrent.CountDownLatch;

public class FirebaseSaveObject {

    public static void main(String[] args) throws FileNotFoundException {
        List<Item> itemList = new ArrayList<>();
        
        // Crear algunos objetos Item
        Item item1 = new Item();
        item1.setId(100L);
        item1.setName("Objeto1");
        item1.setPrice(50.0);
        
        Item item2 = new Item();
        item2.setId(101L);
        item2.setName("Objeto2");
        item2.setPrice(75.0);
        
        // Agregar objetos Item a la lista
        itemList.add(item1);
        itemList.add(item2);

        // Guardar la lista de objetos en Firebase
        new FirebaseSaveObject().saveListToFirebase(itemList);
    }

    private FirebaseDatabase firebaseDatabase;

    /**
     * Inicialización de Firebase.
     */
    private void initFirebase() throws FileNotFoundException {
        try {
            FirebaseOptions firebaseOptions = new FirebaseOptions.Builder()
                    .setDatabaseUrl("https://tarea3-1e4ac-default-rtdb.firebaseio.com/")
                    .setServiceAccount(new FileInputStream(new File("C:\\Users\\412DA\\Desktop\\firebase\\tarea3-1e4ac-firebase-adminsdk-ivp9o-0c8ea2f15c.json")))
                    .build();

            FirebaseApp.initializeApp(firebaseOptions);
            firebaseDatabase = FirebaseDatabase.getInstance();
            System.out.println("Conexion exitosa....");
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Guardar una lista de objetos Item en Firebase junto con su precio.
     * @param itemList La lista de objetos Item.
     */
    private void saveListToFirebase(List<Item> itemList) throws FileNotFoundException {
        if (itemList != null ) {
            initFirebase();
        
            DatabaseReference databaseReference = firebaseDatabase.getReference("/");
            DatabaseReference itemListReference = databaseReference.child("item").child("ItemList");

            CountDownLatch countDownLatch = new CountDownLatch(1);

            // Crear un mapa para almacenar objetos Item junto con su precio
            Map<String, Object> itemListMap = new HashMap<>();
            for (Item item : itemList) {
                // Crear un mapa para cada objeto Item con su precio
                Map<String, Object> itemMap = item.toMap();
                itemMap.put("price", item.getPrice()); // Agregar el precio
                
                // Agregar el objeto Item al mapa
                itemListMap.put(item.getId().toString(), itemMap);
            }

            // Guardar la lista de objetos en Firebase
            itemListReference.updateChildren(itemListMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError de, DatabaseReference dr) {
                    if (de == null) {
                        System.out.println("Lista de objetos guardada exitosamente!");
                    } else {
                        System.out.println("Error al guardar la lista de objetos: " + de.getMessage());
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
