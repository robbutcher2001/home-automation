package co.uk.rob.apartment.automation.utilities.logs;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.data.time.Minute;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

/**
 * @author Rob
 *
 */
public class MakeChart {
	
	private static Logger log = Logger.getLogger(MakeChart.class);
	
	public static void draw(List<LogEntry> entries) {
		TimeSeriesCollection dataset;
		
		TimeSeries temperature = new TimeSeries("Temperature (C)");
		TimeSeries luminiscence = new TimeSeries("Luminiscence (Lux)");
		TimeSeries humidity = new TimeSeries("Humidity (%)");
		
		for (LogEntry entry : entries) {
			temperature.add(new Minute(entry.getEntryDate()), Double.parseDouble(entry.getTemperature()));
			luminiscence.add(new Minute(entry.getEntryDate()), Double.parseDouble(entry.getLux()));
			humidity.add(new Minute(entry.getEntryDate()), Double.parseDouble(entry.getHumidity()));
		}
		
        dataset = new TimeSeriesCollection();
        dataset.addSeries(temperature);

        JFreeChart temperatureChart = ChartFactory.createTimeSeriesChart
        		("Apartment Temperature Report",
        		"Date",
        		"Value",
        		dataset,
        		true,
        		true,
        		false
        		);
        
        dataset = new TimeSeriesCollection();
        dataset.addSeries(luminiscence);
        
        JFreeChart luminiscenceChart = ChartFactory.createTimeSeriesChart
        		("Apartment Luminiscence Report",
        		"Date",
        		"Value",
        		dataset,
        		true,
        		true,
        		false
        		);
        
        dataset = new TimeSeriesCollection();
        dataset.addSeries(humidity);
        
        JFreeChart humidityChart = ChartFactory.createTimeSeriesChart
        		("Apartment Humidity Report",
        		"Date",
        		"Value",
        		dataset,
        		true,
        		true,
        		false
        		);
        
        dataset = new TimeSeriesCollection();
        dataset.addSeries(temperature);
        dataset.addSeries(luminiscence);
        dataset.addSeries(humidity);
        
        JFreeChart environment = ChartFactory.createTimeSeriesChart
        		("Apartment Environment Report",
        		"Date",
        		"Value",
        		dataset,
        		true,
        		true,
        		false
        		);
        
        //environment.getXYPlot().getRangeAxis().setRangeWithMargins(0.0, 500.0);
        
        try {
        	ChartUtilities.saveChartAsJPEG(new File("temperature.jpg"), temperatureChart, 1024, 1024);
        	ChartUtilities.saveChartAsJPEG(new File("luminiscence.jpg"), luminiscenceChart, 1024, 1024);
        	ChartUtilities.saveChartAsJPEG(new File("humidity.jpg"), humidityChart, 1024, 1024);
        	ChartUtilities.saveChartAsJPEG(new File("full_environment.jpg"), environment, 1024, 1024);
    	}
        catch (IOException e) {
        	log.info("Cannot make monitoring chart.");
        	e.printStackTrace();
    	}
	}
}
