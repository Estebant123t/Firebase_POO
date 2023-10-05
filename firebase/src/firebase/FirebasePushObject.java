
package firebase;

import java.util.Scanner;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
public class FirebasePushObject {
    public static void main(String[] args) {
        process process1 = new process();
        process1.login();
    }
}
class process{
    static Scanner s = new Scanner(System.in);
    public void login() {
        System.out.println("Hola, ¿Esta usted registrado en el sistema?");
        System.out.println("Responda 's' para si y 'n' para no.");
        String answer = s.next();
        if (answer.equals('s')){
            Item item = new Item();
            System.out.println("Listo, ingrese su nombre de usuario.");
            String Nameuser = s.next();
            System.out.println("Listo, ingrese su contraseña.");
            String password = s.next();
            item.setId(password);
            item.setName(Nameuser);
            new process().saveUsingPush(item);
            workers();
        } else{
            System.out.println("¿Desea ingresar al sistema?.");
            System.out.println("Responda 's' para si y 'n' para no.");
            String answer1 = s.next();
            if (answer1.equals('s')){
                System.out.println("Listo, ingrese su nombre de usuario.");
                String Nameuser = s.next();
                
                System.out.println("Listo, ingrese su contraseña.");
                String password = s.next();
                ProveUser(Nameuser, password);
            }
        }
        
    }
    private FirebaseDatabase firebaseDatabase;

    
    private void initFirebase() {
        try {
            
            FirebaseOptions firebaseOptions = new FirebaseOptions.Builder()
                    .setDatabaseUrl("https://parcial-6395e-default-rtdb.firebaseio.com/") 
                    .setServiceAccount(new FileInputStream(new File("C:\\Users\\412DA\\Desktop\\firebase\\parcial-6395e-firebase-adminsdk-6mk9z-03e39a0a98.json")))
                   
                    .build();

            FirebaseApp.initializeApp(firebaseOptions);
            firebaseDatabase = FirebaseDatabase.getInstance();
            System.out.println("La conexion se realizo exitosamente...");
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
    }

   
    private void saveUsingPush(Item item) {
        if (item != null) {
            initFirebase();
            DatabaseReference databaseReference = firebaseDatabase.getReference("/");
            DatabaseReference childReference = databaseReference.child("Usuarios");
            CountDownLatch countDownLatch = new CountDownLatch(1);
            childReference.push().setValue(item, new DatabaseReference.CompletionListener() {

                @Override
                public void onComplete(DatabaseError de, DatabaseReference dr) {
                    System.out.println("Registro guardado!");
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
    private void ProveUser(String name, String pass) {
        initFirebase();
        
        DatabaseReference databaseReference = firebaseDatabase.getReference("/");
        DatabaseReference childReference = databaseReference.child("Usuarios"); 
        
        CountDownLatch countDownLatch = new CountDownLatch(1);

        childReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Map<String, Object> dataMap = (Map<String, Object>) snapshot.getValue();
                    Item item = new Item();
                    item.setId((String) dataMap.get("id"));
                    item.setName((String) dataMap.get("name"));
                    if (pass.equals(item.getId()) && name.equals(item.getName())){
                        System.out.println("Usted puede ingresar a las funciones de trabajador.");
                        workers();
                    }
                }
                
                countDownLatch.countDown(); 
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.err.println("Error al leer datos: " + databaseError.getMessage());
                countDownLatch.countDown();
            }
        });

        try {
            countDownLatch.await();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }
    private void workers(){
        System.out.println("¿Usted desea verificar los elementos que se encuentran en la bodega? o ¿Desea agregar elementos a esta");
        System.out.println("Si desea verificar ingrese 'v', si desea agregar ingrese 'a'.");
        String answer2 = s.next();
        if (answer2.equals("v") || answer2.equals("a")){
            if (answer2.equals("v")){
                Readitems();
            } else {
                Additems();
            }
        }
    }
     private void Readitems() {
        initFirebase();
        
        DatabaseReference databaseReference = firebaseDatabase.getReference("/");
        DatabaseReference childReference = databaseReference.child("items"); 
        
        CountDownLatch countDownLatch = new CountDownLatch(1);

        childReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Map<String, Object> dataMap = (Map<String, Object>) snapshot.getValue();
                    Item item = new Item();
                    item.setId((String) dataMap.get("id"));
                    item.setName((String) dataMap.get("name"));
                    item.setPrice((Double) dataMap.get("price"));
                    System.out.println("ID: " + item.getId());
                    System.out.println("Nombre: " + item.getName());
                    System.out.println("Precio: " + item.getPrice());
                }
                
                countDownLatch.countDown(); 
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.err.println("Error al leer datos: " + databaseError.getMessage());
                countDownLatch.countDown(); 
            }
        });

        try {
            countDownLatch.await();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }
     private void Additems(){
         System.out.println("Ingrese la cantidad de objetos a añadir");
         int number = s.nextInt();
         for (int x=1; x<=number; x++){
            Item item = new Item();
            System.out.println("Ingrese el nombre del item a agregar.");
            String nameitem = s.next();
            item.setName(nameitem);
            System.out.println("Ingrese el precio del item a agregar.");
            double priceitem = s.nextDouble();
            item.setPrice(priceitem);
             
         }
     }
     private void saveUsingPush1(Item item) {
        if (item != null) {
            initFirebase();
            DatabaseReference databaseReference = firebaseDatabase.getReference("/");
            
            
            DatabaseReference childReference = databaseReference.child("items");
            CountDownLatch countDownLatch = new CountDownLatch(1);
            childReference.push().setValue(item, new DatabaseReference.CompletionListener() {

                @Override
                public void onComplete(DatabaseError de, DatabaseReference dr) {
                    System.out.println("Registro guardado!");
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