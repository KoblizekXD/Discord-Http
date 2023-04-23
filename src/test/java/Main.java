import com.discord.http.Gateway;

public class Main {
    public static void main(String[] args) {
        Gateway gateway = new Gateway();
        gateway.addHandler(json -> {
            System.out.println(json);
        });
        gateway.run();
    }
}
