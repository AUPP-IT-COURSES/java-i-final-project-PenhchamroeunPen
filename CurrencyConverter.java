import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import org.json.JSONObject;


public class CurrencyConverter extends JFrame {
    private String currencyone;
    private String currencytwo;
    public CurrencyConverter() {
        setTitle("Real-time Currency Converter");
        setSize(500, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // create tabs
        JTabbedPane my_notebook = new JTabbedPane();

        // create frames
        JPanel currency_frame = new JPanel();
        currency_frame.setPreferredSize(new Dimension(480, 480));
        JPanel conversion_frame = new JPanel();
        conversion_frame.setPreferredSize(new Dimension(480, 480));

        currency_frame.setLayout(new BorderLayout());
        conversion_frame.setLayout(new BorderLayout());

        // add tabs
        my_notebook.addTab("Currencies", currency_frame);
        my_notebook.addTab("Convert", conversion_frame);

        // disable 2nd tab
        my_notebook.setEnabledAt(1, false);

        // currencies tab
        //currencies list
        String[] currencies_lists = {"AED","AFN","ALL","AMD","ANG","AOA","ARS","AUD","AWG","AZN","BAM","BBD","BDT","BGN","BHD","BIF","BMD","BND","BOB","BRL","BSD","BTN","BWP","BYN","BZD","CAD","CDF","CHF","CLP","CNY","COP","CRC","CUP","CVE","CZK","DJF","DKK","DOP","DZD","EGP","ERN","ETB","EUR","FJD","FKP","FOK","GBP","GEL","GGP","GHS","GIP","GMD","GNF","GTQ","GYD","HKD","HNL","HRK","HTG","HUF","IDR","ILS","IMP","INR","IQD","IRR","ISK","JEP","JMD","JOD","JPY","KES","KGS","KHR","KID","KMF","KRW","KWD","KYD","KZT","LAK","LBP","LRD","LSL","LYD","MAD","MDL","MGA","MKD","MMK","MNT","MOP","MRU","MUR","MVR","MWK","MXN","MYR","MZN","NAD","NGN","NIO","NOK","NPR","NZD","OMR","PAB","PEN","PGK","PHP","PKR","PLN","PYG","QAR","RON","RSD","RUB","RWF","SAR","SBD","SCR","SDG","SEK","SGD","SHP","SLE","SOS","SRD","SSP","STN","SYP","SZL","THB","TJS","TMT","TND","TOP","TRY","TTD","TVD","TWD","TZS","UAH","UGX","USD","UYU","UZS","VES","VND","VUV","WST","XAF","XCD","XDR","XOF","XPF","YER","ZAR","ZMW","ZWL"};

        // Your Currency panel
        JPanel your = new JPanel();
        your.setBorder(BorderFactory.createTitledBorder("Your Currency"));
        
        //Combobox1
        JComboBox<String> your_currency = new JComboBox<>(currencies_lists);
        your.add(your_currency);
        your_currency.setSelectedItem(null);
        your_currency.addActionListener(e -> {
            currencyone = (String) your_currency.getSelectedItem();
            your_currency.setSelectedItem(currencyone);
        });
        //Object currency1 = your_currency.getSelectedItem();


        //conversion panel
        JPanel conversion = new JPanel();
        conversion.setLayout(new FlowLayout(FlowLayout.CENTER, 300, 10));
        conversion.setBorder(BorderFactory.createTitledBorder("Conversion Currency"));

        JLabel convert_to_label = new JLabel("Currency To Convert To...");
        
        //combobox 2
        JComboBox<String> to_currency = new JComboBox<>(currencies_lists);
        
        to_currency.setSelectedItem(null);
        to_currency.addActionListener(e -> {
            currencytwo = (String) to_currency.getSelectedItem();
        });
        

        JLabel current_rate_label = new JLabel("Current Conversion Rate...");
        current_rate_label.setHorizontalAlignment(SwingConstants.CENTER);

        //rate textfield
        JTextField rate_entry = new JTextField(20);
        rate_entry.setEditable(false);
        conversion.add(convert_to_label);
        conversion.add(to_currency);
        conversion.add(current_rate_label);
        conversion.add(rate_entry);

        //panel for buttons
        JPanel button_frame = new JPanel();
        JButton lock_button = new JButton("Lock");
        JButton unlock_button = new JButton("Unlock");
        button_frame.add(lock_button);
        button_frame.add(unlock_button);

        //lock button function
        lock_button.addActionListener(e -> {
            if (your_currency.getSelectedItem() == null || to_currency.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(null, "You Didn't Fill Out All The Fields!", "Warning!", JOptionPane.WARNING_MESSAGE);
            } else {
                // disable entry box
                your_currency.setEnabled(false);
                to_currency.setEnabled(false);
                // enable rate entry
                rate_entry.setEnabled(true);
                // get rate
                rate_entry.setText(String.format("%.2f", getrate(currencyone, currencytwo)));
                // disable rate_entry
                rate_entry.setEnabled(false);
                // enable tab
                my_notebook.setEnabledAt(1, true);
            }
        });

        //unlock button function
        unlock_button.addActionListener(e -> {
            // enable entry box
            your_currency.setEnabled(true);
            to_currency.setEnabled(true);
            rate_entry.setEnabled(true);
            // disable tab
            my_notebook.setEnabledAt(1, false);
        });
        //add everthing
        currency_frame.add(your, BorderLayout.NORTH);
        currency_frame.add(conversion, BorderLayout.CENTER);
        currency_frame.add(button_frame, BorderLayout.SOUTH);

        // convert tab
        //convert amount panel
        JPanel amount_label = new JPanel();
        amount_label.setBorder(BorderFactory.createTitledBorder("Amount To Convert"));
        amount_label.setLayout(new FlowLayout(FlowLayout.CENTER, 300, 10));
        amount_label.setPreferredSize(new Dimension(500, 100));;
        
        //amount textfield
        JTextField amount_entry = new JTextField(20);
        JButton convert_button = new JButton("Convert");
        amount_label.add(amount_entry);
        amount_label.add(convert_button);

        //converted panel
        JPanel converted_label = new JPanel();
        converted_label.setBorder(BorderFactory.createTitledBorder("Equals To"));
        //result entry
        JTextField converted_entry = new JTextField(20);
        converted_entry.setEditable(false);
        converted_label.add(converted_entry);

        //panel for clear button
        JPanel clear_button = new JPanel();
        JButton clear = new JButton("Clear");
        clear_button.add(clear);

        //clear button function
        clear.addActionListener(e -> {
            amount_entry.setText("");
            converted_entry.setText("");
        });
        // convert button fucntion
        convert_button.addActionListener(e -> {
            // clear converted entry box
            converted_entry.setText("");

            double answer = (getrate(currencyone, currencytwo)) * Double.parseDouble(amount_entry.getText());
            // round to 2 decimals
            answer = Math.round(answer * 100.0) / 100.0;
            // add commas
            String formattedConversion = String.format("%,.2f", answer);
            // update entry box
            converted_entry.setText(formattedConversion);
        });

        conversion_frame.add(amount_label, BorderLayout.NORTH);
        conversion_frame.add(converted_label, BorderLayout.CENTER);
        conversion_frame.add(clear_button, BorderLayout.SOUTH);

        add(my_notebook);
        setVisible(true);
    }
    //Function to get exchange rate from API
    public double getrate(Object your_currency, Object to_currency) {
        String apiKey = "d1f4405ce3d8be4212bfe566";
        String yourCurrency = your_currency.toString();
        String toCurrency = to_currency.toString();
        String url = "https://v6.exchangerate-api.com/v6/" + apiKey + "/pair/" + yourCurrency + "/" + toCurrency;

        try {
            URL urlObj = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();
            connection.setRequestMethod("GET");
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            JSONObject jsonObject = new JSONObject(response.toString());
            double rate = jsonObject.getDouble("conversion_rate");
            return rate;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0.0; // Handle error case appropriately

    }

    public static void main(String[] args) {
        new CurrencyConverter();
    }
}