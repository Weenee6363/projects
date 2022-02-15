import java.sql.*;

public class Main
{
    private static final String SQL_URL = "jdbc:mysql://localhost:3306/skillbox";
    private static final String USER = "root";
    private static final String PASSWORD = "password";
    private static final String QUERY = "Select course_name,\n" +
            " count(*) / (max(month(subscription_date))- ( min(month(subscription_date)) - 1)) as month_avg\n" +
            "FROM purchaselist \n" +
            "WHERE year(subscription_date) = ?\n" +
            "group by course_name;";

    public static void main(String[] args)
    {
        try {
            Connection connection = DriverManager.getConnection(SQL_URL, USER, PASSWORD);
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(QUERY);
                try {
                    preparedStatement.setInt(1, 2018);
                    ResultSet resultSet = preparedStatement.executeQuery();
                    try {
                        while (resultSet.next()) {
                            System.out.print(resultSet.getString("course_name"));
                            System.out.println(" - " + resultSet.getString("month_avg") + " покупок в месяц");
                        }
                    } finally {
                        resultSet.close();
                    }
                } finally {
                    preparedStatement.close();
                }
            } finally {
                connection.close();
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

    }
}
