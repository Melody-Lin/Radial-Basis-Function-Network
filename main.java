package nn.hw2;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Scanner;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class main extends Application {
	//
	static int size = 0; // data size
	static float lr = 0; // learning rate
	static int kmeans = 0; // number of k-means clusters = number of center points = number of neural in
							// hidden layer
	static int cvg = 0; // convergence condition: training frequency
	static int[] E = new int[2]; // expected output
	static String[] fileList = { "perceptron1", "perceptron2", "2Ccircle1", "2Circle1", "2Circle2", "2CloseS",
			"2CloseS2", "2CloseS3", "2cring", "2CS", "2Hcircle1", "2ring" };
	static String fileName = "";
	static int trainFreq = 0; // training frequency
	static int testFreq = 0; // test frequency
	//
	Button btn1 = new Button("Enter");
	Label rmse = new Label("");
	Label cr1 = new Label(""); // train correct rate
	Label cr2 = new Label(""); // test correct rate
	TextField t1 = new TextField();
	TextField t2 = new TextField();
	TextField t3 = new TextField();
	TextField t4 = new TextField();
	TextArea ta = new TextArea();
	static boolean train3 = false;
	static boolean test3 = false;
	static double maxTrainX = -100;
	static double maxTrainY = -100;
	static double minTrainX = 100;
	static double minTrainY = 100;
	static double output_rmse = 0;
	static double output_trainCR = 0;
	static double output_testCR = 0;
	static String output_W = "";

	@Override
	public void start(Stage stage) {
		stage.setTitle("RBFN");
		Pane pane = new Pane();
		//
		Label lb1 = new Label("選擇檔案");
		lb1.setFont(new Font("Times New Roman", 22));
		lb1.setTranslateX(60);
		lb1.setTranslateY(100);

		ObservableList<String> options1 = FXCollections.observableArrayList("perceptron1", "perceptron2", "2Ccircle1",
				"2Circle1", "2Circle2", "2CloseS", "2CloseS2", "2CloseS3", "2cring", "2CS", "2Hcircle1", "2ring");
		ComboBox cbx1 = new ComboBox(options1);
		cbx1.setTranslateX(220);
		cbx1.setTranslateY(100);
		//
		Label lb2 = new Label("學習率");
		lb2.setFont(new Font("Times New Roman", 22));
		lb2.setTranslateX(60);
		lb2.setTranslateY(150);

		t1.setTranslateX(220);
		t1.setTranslateY(150);
		t1.setMaxWidth(115);
		//
		Label lb3 = new Label("訓練次數");
		lb3.setFont(new Font("Times New Roman", 22));
		lb3.setTranslateX(60);
		lb3.setTranslateY(200);

		t2.setTranslateX(220);
		t2.setTranslateY(200);
		t2.setMaxWidth(115);
		//
		Label lb4 = new Label("K-means群數");
		lb4.setFont(new Font("Times New Roman", 22));
		lb4.setTranslateX(60);
		lb4.setTranslateY(250);

		t3.setTranslateX(220);
		t3.setTranslateY(250);
		t3.setMaxWidth(115);
		//
		btn1.setFont(new Font("Times New Roman", 18));
		btn1.setTranslateX(160);
		btn1.setTranslateY(330);
		//
		Label lb6 = new Label("RMSE:");
		lb6.setFont(new Font("Times New Roman", 22));
		lb6.setTranslateX(60);
		lb6.setTranslateY(430);
		//
		Label lb7 = new Label("訓練辨識率:");
		lb7.setFont(new Font("Times New Roman", 22));
		lb7.setTranslateX(60);
		lb7.setTranslateY(480);
		//
		Label lb8 = new Label("測試辨識率: ");
		lb8.setFont(new Font("Times New Roman", 22));
		lb8.setTranslateX(60);
		lb8.setTranslateY(530);
		//
		Label lb9 = new Label("鍵結值:");
		lb9.setFont(new Font("Times New Roman", 22));
		lb9.setTranslateX(60);
		lb9.setTranslateY(580);
		//
		ta.setFont(new Font("Times New Roman", 22));
		ta.setTranslateX(60);
		ta.setTranslateY(630);
		ta.setMaxSize(370, 300);
		// Train Chart
		NumberAxis xAxis1 = new NumberAxis(-22, 7, 5);
		NumberAxis yAxis1 = new NumberAxis(-7.5, 25, 5);
		ScatterChart<Number, Number> trainSC = new ScatterChart<Number, Number>(xAxis1, yAxis1);
		trainSC.setTitle("Train");
		trainSC.setHorizontalZeroLineVisible(false);
		trainSC.setVerticalZeroLineVisible(false);
		trainSC.setMinSize(900, 480);
		trainSC.setTranslateX(450);
		trainSC.setTranslateY(0);
		// Test Chart
		NumberAxis xAxis2 = new NumberAxis(-22, 7, 5);
		NumberAxis yAxis2 = new NumberAxis(-7.5, 25, 5);
		ScatterChart<Number, Number> testSC = new ScatterChart<Number, Number>(xAxis2, yAxis2);
		testSC.setTitle("Test");
		testSC.setHorizontalZeroLineVisible(false);
		testSC.setVerticalZeroLineVisible(false);
		testSC.setMinSize(900, 480);
		testSC.setTranslateX(450);
		testSC.setTranslateY(500);
		//
		btn1.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				try {
					// Train Data Series
					XYChart.Series trainS1 = new XYChart.Series();
					XYChart.Series trainS2 = new XYChart.Series();
					XYChart.Series trainS3 = new XYChart.Series();
					XYChart.Series trainS4 = new XYChart.Series();
					// Test Data Series
					XYChart.Series testS1 = new XYChart.Series();
					XYChart.Series testS2 = new XYChart.Series();
					XYChart.Series testS3 = new XYChart.Series();
					XYChart.Series testS4 = new XYChart.Series();
					//
					trainS1.getData().removeAll(Collections.singleton(trainSC.getData().setAll()));
					trainS2.getData().removeAll(Collections.singleton(trainSC.getData().setAll()));
					trainS3.getData().removeAll(Collections.singleton(trainSC.getData().setAll()));
					trainS4.getData().removeAll(Collections.singleton(trainSC.getData().setAll()));
					testS1.getData().removeAll(Collections.singleton(testSC.getData().setAll()));
					testS2.getData().removeAll(Collections.singleton(testSC.getData().setAll()));
					testS3.getData().removeAll(Collections.singleton(testSC.getData().setAll()));
					testS4.getData().removeAll(Collections.singleton(testSC.getData().setAll()));
					//
					ta.setText("");
					pane.getChildren().removeAll(rmse, cr1, cr2);
					//
					train(Float.parseFloat(t1.getText()), Integer.valueOf(t2.getText()), Integer.valueOf(t3.getText()),
							cbx1.getValue(), trainS1, trainS2, trainS3, trainS4, testS1, testS2, testS3, testS4);
					trainSC.getData().addAll(trainS1, trainS2, trainS4);
					if (train3 == true) {
						trainSC.getData().addAll(trainS3);
					}
					testSC.getData().addAll(testS1, testS2, testS4);
					if (test3 == true) {
						testSC.getData().addAll(testS3);
					}
					rmse = new Label(String.valueOf(output_rmse));
					rmse.setFont(new Font("Times New Roman", 22));
					rmse.setTranslateX(220);
					rmse.setTranslateY(430);
					//
					cr1 = new Label(String.valueOf(output_trainCR) + " %");
					cr1.setFont(new Font("Times New Roman", 22));
					cr1.setTranslateX(220);
					cr1.setTranslateY(480);
					//
					cr2 = new Label(String.valueOf(output_testCR) + " %");
					cr2.setFont(new Font("Times New Roman", 22));
					cr2.setTranslateX(220);
					cr2.setTranslateY(530);
					//
					ta.setText(output_W);
					pane.getChildren().addAll(rmse, cr1, cr2);
					output_W = "";

				} catch (NumberFormatException | FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		pane.getChildren().addAll(lb1, lb2, lb3, lb4, lb6, lb7, lb8, lb9, btn1, cbx1, t1, t2, t3, trainSC, testSC, ta);
		pane.setStyle("-fx-background: #FFFFFF");
		Scene sc = new Scene(pane, 1400, 1000);
		stage.setScene(sc);
		stage.show();
	}

	public static void train(float l, int t, int k1, Object object, XYChart.Series trainS1, XYChart.Series trainS2,
			XYChart.Series trainS3, XYChart.Series trainS4, XYChart.Series testS1, XYChart.Series testS2,
			XYChart.Series testS3, XYChart.Series testS4) throws FileNotFoundException {
		lr = l; // learning rate
		cvg = t; // training frequency
		kmeans = k1; // number of k-means clusters
		fileName = (String) object;
		dataSize();
		trainFreq = size * 2 / 3; // training frequency
		testFreq = size - trainFreq; // test frequency
		if (kmeans > trainFreq) {
			kmeans = trainFreq;
		}
		readFile(trainS1, trainS2, trainS3, trainS4, testS1, testS2, testS3, testS4);
	}

	// calculate input data size
	public static void dataSize() throws FileNotFoundException {
		size = 0;
		// File file = new File("C:/Java/NN.hw2/DataSet/" + fileName + ".txt");
		File file = new File("DataSet/" + fileName + ".txt");
		Scanner scanner = new Scanner(file);
		String tmp = "";
		while (scanner.hasNextLine()) {
			tmp = scanner.nextLine();
			size++;
		}
	}

	// random assign data to train and test case
	public static void random(int size, int[] trainRand, int[] testRand) {
		int range = size * 2 / 3; // 2/3 to be train data
		Random rand = new Random();
		int rdm[] = new int[size];
		ArrayList list = new ArrayList();
		for (int i = 0; i < size; i++) {
			list.add(i);
		}
		for (int i = 0; i < size; i++) {
			rdm[i] = -1;
		}
		int cnt = range;
		while (cnt > 0) {
			int index = (int) list.remove(rand.nextInt(list.size()));
			rdm[index] = 1; // 1: random train data, -1: random test data
			cnt--;
		}
		int k = 0, t = 0;
		for (int i = 0; i < size; i++) {
			if (rdm[i] == 1) {
				trainRand[k] = i;
				k++;
			} else {
				testRand[t] = i;
				t++;
			}
		}
	}

	// set train and test data after random assigning
	public static void setData(double[][] trainData, double[][] testData, double[][] x, int[] trainRand,
			int[] testRand) {
		// train data
		for (int i = 0; i < trainFreq; i++) {
			for (int j = 0; j < 3; j++) {
				trainData[i][j] = x[trainRand[i]][j];
			}
			trainData[i][3] = -1;
		}
		// test data
		for (int i = 0; i < testFreq; i++) {
			for (int j = 0; j < 3; j++) {
				testData[i][j] = x[testRand[i]][j];
			}
			testData[i][3] = -1;
		}
	}

	public static void readFile(XYChart.Series trainS1, XYChart.Series trainS2, XYChart.Series trainS3,
			XYChart.Series trainS4, XYChart.Series testS1, XYChart.Series testS2, XYChart.Series testS3,
			XYChart.Series testS4) throws FileNotFoundException {
		double[][] x = new double[size][3]; // input data
		double[][] trainData = new double[trainFreq][4]; // train data
		double[][] testData = new double[testFreq][4]; // test data
		int[] trainRand = new int[trainFreq]; // random train data
		int[] testRand = new int[testFreq]; // random test data
		double[] w = new double[kmeans]; // weight of each neural
		double[] phi = new double[kmeans]; // Gaussian function
		double[] var = new double[kmeans]; // Standard Deviation
		double[][] m = new double[kmeans][2];
		double[][] output_m = new double[kmeans][2];
		double theta = 1;
		double phi_0 = 1;
		//
		String tmp = "";
		int row = 0;
		// File file = new File("C:/Java/NN.hw2/DataSet/" + fileName + ".txt");
		File file = new File("DataSet/" + fileName + ".txt");
		Scanner scanner = new Scanner(file);
		while (scanner.hasNextLine()) {
			if (row == size) {
				break;
			}
			tmp = scanner.nextLine();
			String[] arr = tmp.split(" ");
			for (int i = 0, j = 0; i < 3; i++, j++) {
				x[row][i] = Float.parseFloat(arr[j]);
			}
			row++;
		}
		//

		//
		random(size, trainRand, testRand);
		setData(trainData, testData, x, trainRand, testRand);
		kMeans(trainData, w, m);
		trainData(trainData, testData, m, phi, w, var, output_m, theta, phi_0);
		setTrainOutput(trainData, trainS1, trainS2, trainS3, trainS4, output_m);
		setTestOutput(testData, testS1, testS2, testS3, testS4, output_m);
	}

	// do k-means
	public static void kMeans(double[][] trainData, double[] w, double[][] m) {
		double[][] weight = new double[kmeans][2];
		double[][] center = new double[kmeans][2];
		// 取出期望輸出, E[0]<E[1]
		for (int i = 1; i < trainFreq; i++) {
			if (trainData[i][2] < trainData[i - 1][2]) {
				E[0] = (int) trainData[i][2];
				E[1] = (int) trainData[i - 1][2];
				break;
			} else if (trainData[i][2] > trainData[i - 1][2]) {
				E[0] = (int) trainData[i - 1][2];
				E[1] = (int) trainData[i][2];
				break;
			}
		}
		boolean[] find = new boolean[kmeans];
		for (int i = 0; i < kmeans; i++) {
			find[i] = false;
		}
		Random rd = new Random();
		int num = kmeans / 2;
		for (int i = 0; i < num; i++) {
			while (find[i] == false) {
				int n = rd.nextInt(trainFreq); // choose a point to be the center randomly
				if (trainData[n][2] == E[0]) {
					for (int j = 0; j < 2; j++) {
						center[i][j] = trainData[n][j];
					}
					find[i] = true;
				} else {
					find[i] = false;
				}
			}
		}
		for (int i = num; i < kmeans; i++) {
			while (find[i] == false) {
				int n = rd.nextInt(trainFreq); // choose a point to be the center randomly
				if (trainData[n][2] == E[1]) {
					for (int j = 0; j < 2; j++) {
						center[i][j] = trainData[n][j];
					}
					find[i] = true;
				} else {
					find[i] = false;
				}
			}
		}

		int time = 100; // recursive upper limit
		int t = 0;

		while (t < time) {
			int[] index = new int[kmeans];
			float[] sumX = new float[kmeans];
			float[] sumY = new float[kmeans];
			float[] meanX = new float[kmeans];
			float[] meanY = new float[kmeans];
			float[] d = new float[kmeans];
			int[][] c = new int[kmeans][trainFreq];
			double[] distance = new double[kmeans];
			double min = 1000;

			for (int i = 0; i < kmeans; i++) {
				index[i] = 0;
			}
			double x, y;
			// according to Euclidean distance, distribute every point to the nearest
			// cluster
			for (int i = 0; i < trainFreq; i++) {
				for (int j = 0; j < kmeans; j++) {
					x = center[j][0] - trainData[i][0];
					y = center[j][1] - trainData[i][1];
					distance[j] = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
					if (distance[j] < min) {
						min = distance[j];
						trainData[i][3] = j; // index 3 record which cluster trainData belongs to
					}
				}
				int n = (int) (trainData[i][3]);
				c[n][index[n]] = i;
				min = 1000;
				index[n]++; // the number of records of each cluster
			}
			// update center point's coordinate
			for (int i = 0; i < kmeans; i++) {
				for (int j = 0; j < index[i]; j++) {
					sumX[i] += trainData[c[i][j]][0];
					sumY[i] += trainData[c[i][j]][1];
				}
				meanX[i] = sumX[i] / (index[i] + 1); // center point: x-coordinate
				meanY[i] = sumY[i] / (index[i] + 1); // center point: y-coordinate
				weight[i][0] = meanX[i];
				weight[i][1] = meanY[i];
				d[i] = (float) Math
						.sqrt(Math.pow(weight[i][0] - weight[i][0], 2) + Math.pow(weight[i][1] - weight[i][1], 2));
			}
			t++;
		}

		for (int i = 0; i < kmeans; i++) {
			for (int j = 0; j < 2; j++) {
				m[i][j] = weight[i][j];
			}
		}
	}

	public static void calVar(double[][] m, double[] var) {
		// calculate the longest distance between two points
		double d_max = -100;
		double d = 0;
		for (int i = 0; i < kmeans - 1; i++) {
			for (int j = i + 1; j < kmeans; j++) {
				d = Math.sqrt(Math.pow(m[i][0] - m[j][0], 2) + Math.pow(m[i][1] - m[j][1], 2));
				if (d > d_max) {
					d_max = d;
				}
			}
		}

		for (int i = 0; i < kmeans; i++) {
			var[i] = d_max / kmeans;
		}
	}

	public static void basisFunc(double[][] data, double[][] m, int index, double[] phi, double[] var) {
		double n = 0;
		for (int i = 0; i < kmeans; i++) {
			n = Math.sqrt(Math.pow(data[index][0] - m[i][0], 2) + Math.pow(data[index][1] - m[i][1], 2));
			phi[i] = Math.exp((-n) / (2 * Math.pow(var[i], 2)));
		}
	}

	public static double testCR(double[][] testData, double[] phi, double[] w, double theta, double phi_0, double[] var,
			double[][] m) {
		double count = 0;
		int data = 0;
		for (int i = 0; i < testFreq; i++) {
			basisFunc(testData, m, i, phi, var);
			if (output(phi, w, theta, phi_0) == E[0]) {
				data = E[0];
			} else {
				data = E[1];
			}
			if (data == (int) testData[i][2]) {
				count++;
			}
		}
		double cr = (count / testFreq) * 100;
		return cr;
	}

	public static double output(double[] phi, double[] w, double theta, double phi_0) {
		double output = 0;
		output += theta * phi_0;
		for (int i = 0; i < kmeans; i++) {
			output += w[i] * phi[i];
		}
		return output;
	}

	public static double rmse(double[][] trainData, double[] phi, double[] w, double theta, double phi_0, double[] var,
			double[][] m) {
		double rmse = 0;
		for (int i = 0; i < trainFreq; i++) {
			basisFunc(trainData, m, i, phi, var);
			rmse += Math.pow(trainData[i][2] - output(phi, w, theta, phi_0), 2);
		}
		rmse = Math.sqrt(rmse / trainFreq);
		return rmse;
	}

	public static void trainData(double[][] trainData, double[][] testData, double[][] m, double[] phi, double[] w,
			double[] var, double[][] output_m, double theta, double phi_0) {
		calVar(m, var);
		// initialize w
		double count = 0;
		double quantity = 0;
		for (int i = 0; i < kmeans; i++) {
			for (int j = 0; j < trainFreq; j++) {
				if ((int) trainData[j][3] == i) {
					count += trainData[j][2];
					quantity++;
				}
			}
			w[i] = count / quantity;
			if (quantity == 0) {
				w[i] = Math.random();
			}
			count = 0;
			quantity = 0;
		}

		int totalFreq = 0;
		int t = 0;
		double countCR = 0;
		double bestCR = 0;
		double[] bestW = new double[kmeans];
		double[] bestVar = new double[kmeans];
		double bestTheta = 0;

		while (totalFreq < cvg) {
			while (t < trainFreq) {
				basisFunc(trainData, m, t, phi, var);
				double cal = 0;
				for (int i = 0; i < kmeans; i++) {
					// update theta
					theta = theta + lr * (trainData[t][2] - output(phi, w, theta, phi_0));
					// update w
					w[i] = w[i] + lr * (trainData[t][2] - output(phi, w, theta, phi_0)) * phi[i];
					// update m
					m[i][0] = m[i][0] - lr * (trainData[t][2] - output(phi, w, theta, phi_0)) * w[i] * phi[i]
							* (1 / Math.pow(var[i], 2) * (trainData[t][0] - m[i][0]));
					m[i][1] = m[i][1] - lr * (trainData[t][2] - output(phi, w, theta, phi_0)) * w[i] * phi[i]
							* (1 / Math.pow(var[i], 2) * (trainData[t][1] - m[i][1]));
					// update var
					cal = Math.pow(trainData[t][0] - m[i][0], 2) + Math.pow(trainData[t][1] - m[i][1], 2);
					var[i] = var[i] - lr * (trainData[t][2] - output(phi, w, theta, phi_0)) * w[i] * phi[i]
							* (1 / Math.pow(var[i], 3) * cal);
				}
				double ans;
				if (output(phi, w, theta, phi_0) == E[0]) {
					ans = E[0];
				} else {
					ans = E[1];
				}
				if (output(phi, w, theta, phi_0) == E[0]) {
					ans = E[0];
				} else {
					ans = E[1];
				}
				if (ans == (int) trainData[t][2]) {
					countCR++;
				}
				t++;
			}
			double CR = (countCR / trainFreq) * 100;
			// to record the best correct rate of training
			if (CR > bestCR) {
				bestCR = CR;
				for (int i = 0; i < kmeans; i++) {
					bestW[i] = w[i];
					bestVar[i] = var[i];
					bestTheta = theta;
					for (int j = 0; j < 2; j++) {
						output_m[i][j] = m[i][j];
					}
				}
			}
			// if correct rate is up to 100%, then stop training
			if ((int) bestCR == 100) {
				break;
			}
			t = 0;
			countCR = 0;
			totalFreq++;
		}
		output_rmse = rmse(trainData, phi, bestW, bestTheta, phi_0, bestVar, output_m);
		output_trainCR = bestCR;
		output_testCR = testCR(testData, phi, bestW, bestTheta, phi_0, bestVar, output_m);
		for (int i = 0; i < kmeans; i++) {
			output_W = output_W + "w" + (i + 1) + ": ";
			double d = Math.round(bestW[i] * 100.0) / 100.0;
			output_W += String.valueOf(d);
			if (i == kmeans - 1) {

			} else if (i % 3 == 2) {
				output_W += "\n";
			} else {
				output_W += ", ";
			}
		}
	}

	public static void setTrainOutput(double[][] trainData, XYChart.Series trainS1, XYChart.Series trainS2,
			XYChart.Series trainS3, XYChart.Series trainS4, double[][] output_m) {
		for (int i = 0; i < trainFreq; i++) {
			if (trainData[i][2] == E[0]) {
				trainS1.getData().add(new XYChart.Data(trainData[i][0], trainData[i][1]));
			} else if (trainData[i][2] == E[1]) {
				trainS2.getData().add(new XYChart.Data(trainData[i][0], trainData[i][1]));
			} else {
				train3 = true;
				trainS3.getData().add(new XYChart.Data(trainData[i][0], trainData[i][1]));
			}
		}
		for (int i = 0; i < kmeans; i++) {
			trainS4.getData().add(new XYChart.Data(output_m[i][0], output_m[i][1]));
		}
	}

	public static void setTestOutput(double[][] testData, XYChart.Series testS1, XYChart.Series testS2,
			XYChart.Series testS3, XYChart.Series testS4, double[][] output_m) {
		for (int i = 0; i < testFreq; i++) {
			if (testData[i][2] == E[0]) {
				testS1.getData().add(new XYChart.Data(testData[i][0], testData[i][1]));
			} else if (testData[i][2] == E[1]) {
				testS2.getData().add(new XYChart.Data(testData[i][0], testData[i][1]));
			} else {
				test3 = true;
				testS3.getData().add(new XYChart.Data(testData[i][0], testData[i][1]));
			}
		}
		for (int i = 0; i < kmeans; i++) {
			testS4.getData().add(new XYChart.Data(output_m[i][0], output_m[i][1]));
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		launch(args);
	}

}
