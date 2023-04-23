# Discord HTTP library for Java  

*We are not affiliated with Discord inc.*  
Library providing you a simple help for your Discord API written in Java!  

### Installation  
*Nothing yet*  

### Examples

1) Create a Gateway:
```java
public class Main {
    public static void main(String[] args) {
        Gateway gateway = new Gateway();
        gateway.addHandler(json -> {
            // Handle incoming stuff here
        }).run();
    }
}
```