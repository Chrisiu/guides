package demo;

public class App {
    public String getGreeting() {
        // <1>
        return "Hello world.";
    }

    public static void main(String[] args) {
        System.out.println(new App().getGreeting());
    }
}
