import java.util.HashMap;
import java.util.Map;

public class CustomerStorage {
    private final Map<String, Customer> storage;

    public CustomerStorage() {
        storage = new HashMap<>();
    }

    public void addCustomer(String data) {
        final int INDEX_NAME = 0;
        final int INDEX_SURNAME = 1;
        final int INDEX_EMAIL = 2;
        final int INDEX_PHONE = 3;
        final String NUMBER_REGEX = "8|(\\+7)[0-9]{10}";
        final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@(.+)";

        String[] components = data.split("\\s+");

        if (components.length != 4)
        {
            throw new IllegalArgumentException("Недостаточно данных для создания нового клиента!");
        }
        if (!components[INDEX_PHONE].matches(NUMBER_REGEX))
        {
            throw new IllegalArgumentException("Неверный формат номера телефона");
        }
        if (!components[INDEX_EMAIL].matches(EMAIL_REGEX))
        {
            throw new IllegalArgumentException("Неверный формат адреса эл. почты");
        }

        String name = components[INDEX_NAME] + " " + components[INDEX_SURNAME];
        storage.put(name, new Customer(name, components[INDEX_PHONE], components[INDEX_EMAIL]));
    }

    public void listCustomers() {
        storage.values().forEach(System.out::println);
    }

    public void removeCustomer(String name)
    {
        storage.remove(name);
    }

    public Customer getCustomer(String name) {
        return storage.get(name);
    }

    public int getCount() {
        return storage.size();
    }
}