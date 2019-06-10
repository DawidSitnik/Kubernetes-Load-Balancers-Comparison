import java.util.LinkedList;
import java.util.List;

import javax.swing.SwingWorker;

import org.knowm.xchart.QuickChart;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;

/**
 * Creates a real-time chart using SwingWorker
 */
public class SwingWorkerRealTime
{

	MySwingWorker mySwingWorker;
	SwingWrapper<XYChart> sw;
	XYChart chart;

	public static void main(String[] args) throws Exception
	{

		SwingWorkerRealTime swingWorkerRealTime = new SwingWorkerRealTime();
		swingWorkerRealTime.go();
	}

	private void go()
	{
		// Create Chart
		chart = QuickChart.getChart("Realtime graph", "Time", "Value", "Benchmark", new double[] { 0 },
				new double[] { 0 });
		chart.getStyler().setLegendVisible(false);
		chart.getStyler().setXAxisTicksVisible(false);
		chart.getStyler().setXAxisMax(100.0);
		chart.getStyler().setXAxisMin(0.0);
		chart.getStyler().setYAxisMax(100.0);
		chart.getStyler().setYAxisMin(0.0);

		// Show it
		sw = new SwingWrapper<XYChart>(chart);
		sw.displayChart();

		mySwingWorker = new MySwingWorker();
		mySwingWorker.execute();
	}

	private class MySwingWorker extends SwingWorker<Boolean, double[]>
	{

		LinkedList<Double> fifo = new LinkedList<Double>();

		public MySwingWorker()
		{
			fifo.add(0.0);
		}

		@Override
		protected Boolean doInBackground() throws Exception
		{
			while (!isCancelled())
			{
				fifo.add(Math.random() * 100);
				if (fifo.size() > 100)
					fifo.removeFirst();

				double[] array = new double[fifo.size()];
				for (int i = 0; i < fifo.size(); i++)
					array[i] = fifo.get(i);
				publish(array);

				try
				{
					Thread.sleep(100);
				}
				catch (InterruptedException e)
				{
					System.out.println("Error : " + e.getMessage());
					System.exit(1);
				}

			}

			return true;
		}

		@Override
		protected void process(List<double[]> chunks)
		{
			System.out.println("number of chunks: " + chunks.size());
			chart.updateXYSeries("Benchmark", null, mostRecentDataSet, null);
			sw.repaintChart();

			long start = System.currentTimeMillis();
			long duration = System.currentTimeMillis() - start;
			try
			{
				Thread.sleep(20 - duration); // 40 ms ==> 25fps
			}
			catch (InterruptedException e)
			{
				System.out.println("Error : " + e.getMessage());
				System.exit(1);
			}

		}
	}
}
