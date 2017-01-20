package sample.spring.chapter12.domain;
import java.io.Serializable;

public class Correo implements Serializable{
   private String id;
   private String name;
   private String email;
   public Correo(String i,String n,String e) {
       id=i;
       name=n;
       email=e;
   }
   public String getId() {
      return id;
   }
   public String getEmail() {
      return email;
   }
   public String getName() {
      return name;
   }
   public void setId(String i) {
      id=i;
   }
   public void setEmail(String e) {
      email=e;
   }
   public void setName(String n) {
       name=n;
   } 
}
