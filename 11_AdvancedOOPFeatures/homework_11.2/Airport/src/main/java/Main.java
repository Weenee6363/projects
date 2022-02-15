import com.skillbox.airport.Airport;
import com.skillbox.airport.Flight;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Main {
    public static void main(String[] args) {


    }

    public static List<Flight> findPlanesLeavingInTheNextTwoHours(Airport airport)
    {
        ArrayList<Flight> flights = new ArrayList<>();

        airport.getTerminals().forEach(t -> t.getFlights().stream()
                .filter(Main::checkNearestFlight)
                .forEach(flights::add));
        return flights;
    }

    //Проверка на то, что полёт произойдёт в ближайшие 2 часа
    // и что это вылет, а не прибытие.
    private static boolean checkNearestFlight(Flight flight)
    {
//        long twoHours = 1000 * 60 * 60 * 2;
//        return flight.getDate().getTime() <= System.currentTimeMillis() + twoHours &&
//                flight.getDate().getTime() >= System.currentTimeMillis() &&
//                flight.getType().equals(Flight.Type.DEPARTURE);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.HOUR_OF_DAY, 2);


        return flight.getDate().getTime() <= calendar.getTime().getTime() &&
                flight.getDate().getTime() >= System.currentTimeMillis() &&
                flight.getType().equals(Flight.Type.DEPARTURE);
    }


}