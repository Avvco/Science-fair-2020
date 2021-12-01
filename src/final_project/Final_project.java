package final_project;

import java.io.File;
import java.io.FileInputStream;//for reading file
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;//for delay function

import org.apache.commons.lang3.StringUtils;//to process string

import jxl.Sheet;
import jxl.Workbook;
import jxl.format.Colour;
import jxl.read.biff.BiffException;
//to access excel
//to write on excel
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

/*
 * It is a program that separate one large excel to several small excel, one month a file ,supported .xls file format only.
 * output format:
 * 	A .xls file contains all the calculation result including y=ax+b and R^2
 * 	Several .xls file that separated from the original .xls that came from input location
 * 
 * Regression-Analysis data format:
 *  x axis=pm2.5
 * 	y axis=pm10
 *  
 * input format
 *  A .xls file that has ONLY 1 work-sheet and sorted by date, and date in column A
 *  Only English letter is being allowed.
 *  Date format: MM DD YYYY , months require full name and the first letter must be upper-case. ex:March 31, 2017
 *  
 */

public class Final_project {
	
	public static void main(String[] args) throws RowsExceededException, WriteException, IOException, InterruptedException {	
		ImageProc imgProc = new ImageProc();//static unavailable
		DataRelevance dataRelevance = new DataRelevance();
		Extension extension = new Extension();
		Final_project  Fp = new Final_project();
		
		String FileInput;
		String FileOutput;
		int rounding_off_place=2;	//round off to the 2nd decimal place
		String externalDataRoute="D:\\science fair\\minitor report";
		
		String imageProcDataIn="D:\\science fair\\IMAGE_IN\\IMAGE";
		
		String imageProcData2PM10in="D:\\science fair\\IMAGE_IN\\PM10";//SO2, CO, O3, NOx, THC
		String imageProcData2PM25in="D:\\science fair\\IMAGE_IN\\PM25";
		
		String imageProcData2PM10in2="D:\\science fair\\IMAGE_IN\\PM10_2";//RH, AVG_TEMP, CUMULATIVE_RAINFALL, RAINY_DAYS, SUNSHINE_HOURS
		String imageProcData2PM25in2="D:\\science fair\\IMAGE_IN\\PM25_2";
		
		Scanner scanner1 = new Scanner(System.in);
		Scanner scanner2 = new Scanner(System.in);
		// Scanner scanner3 = new Scanner(System.in);
		// Scanner scanner4 = new Scanner(System.in);
		
		System.out.println("**Make sure your original .xls has ONLY 1 worksheet and already sorted by date**");
		System.out.println("Please input file location");
		System.out.println("The route is being pointed to entire directory. Every excel in this directory and below it will be convert into several smaller excel");
		FileInput = new String();
		
		FileInput=scanner1.nextLine();
		
		System.out.println("Please enter file-output directory");
		FileOutput=scanner2.nextLine();
		
		/* System.out.println("Please entry the month you want. All months will be outputted if input is 'all'.\nex:December");
		Month=scanner3.next();
		
		System.out.println("Please entry the year you want. All years will be outputted if input is 'all'");
		Year=scanner4.next(); */
		
		scanner1.close();
		scanner2.close();
		// scanner3.close();
		// scanner4.close();
		
		File file = new File(FileInput);
		File fileOut= new File(FileOutput);
		List dataList = imgProc.createYear();//for standardization 
		List originalData = new ArrayList();//original input
		List bigRealData = new ArrayList<OuterCompare>();//AVG for year, each month
		
		String nonStandardizationRoute=FileOutput+"/Non_Standardization";
		File dir_file = new File(nonStandardizationRoute);
		dir_file.mkdir();
		
		if(file.isDirectory()) {
			File[] files = file.listFiles();
			for(File f:files) {
				if(f.isDirectory()) {
					File[] subfiles = f.listFiles();
					for(File fi:subfiles) {
						if(fi.getName().indexOf(".xls") >0) {
							List<OriginalData> oriData = extension.createList();
							OriginalData od = new OriginalData();
							Fp.readExcel(fi,fileOut,rounding_off_place, dataList, nonStandardizationRoute,externalDataRoute,od,bigRealData);
							oriData.add(od);
							originalData.add(oriData);
							System.out.println("\n"+fi.getName().replace(".xls", "")+"\tDone\n");
						}
					}
				}
				else {
					if(f.getName().indexOf(".xls")>0) {
						List<OriginalData> oriData = extension.createList();
						OriginalData od = new OriginalData();
						Fp.readExcel(f,fileOut,rounding_off_place, dataList, nonStandardizationRoute,externalDataRoute,od,bigRealData);
						oriData.add(od);
						originalData.add(oriData);
						System.out.println("\n"+f.getName().replace(".xls", "")+"\tDone\n");
					}
				}
			}
			dataRelevance.readData(FileOutput,dataList);
			extension.extensionCenter(FileOutput,originalData,rounding_off_place,bigRealData);
			
			imgProc.proceedImageDATA(FileOutput,dataList,imageProcDataIn);
			imgProc.proceedImageDATA2(FileOutput, dataList, imageProcData2PM10in,imageProcData2PM25in,imageProcData2PM10in2,imageProcData2PM25in2);
		}
		System.out.println("\n\nAll Done");
	}
	
	//main end
	
	
	public void readExcel(File file, File fileOutput ,int rounding_off_place, List<Position> dataList, String nonStandardizationRoute, String externalDataRoute, OriginalData od, List bigRealData) {
		DataRelevance_Non_Standardization dns = new DataRelevance_Non_Standardization();
		
		OuterCompare outC = new OuterCompare();
		List<Compare> realData = dns.createYear();
		String fileNameTxt = file.getName().replace(".xls", "");
		outC.setLocation(fileNameTxt);
		od.setLocation(fileNameTxt);
		try {
			//make new folder
			String newFileRoute=fileOutput+"/"+fileNameTxt;
			File dir_file = new File(newFileRoute);
			dir_file.mkdir();
			
			String newDataRoute=newFileRoute+"/"+"DATA_"+fileNameTxt;
			File dir_file2 = new File(newDataRoute);
			dir_file2.mkdir();
			
			
			String filename = "all";
			
				
			//read input excel
			InputStream is = new FileInputStream(file.getAbsolutePath());
			
			Workbook wb = Workbook.getWorkbook(is);
			
			//size of excel
			//int insheetLocation[];
			//insheetLocation=new int[] {2,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30};//input data location
			
			
			//read excel to an array, format:string
			Sheet Insheet = wb.getSheet(0);//support the first sheet only
			String cellinfo[][];
			
			cellinfo = new String[Insheet.getRows()][Insheet.getColumns()];
		
			for(int i=0;i<Insheet.getRows();i++) {
				for(int j=0;j < Insheet.getColumns();j++){
					cellinfo[i][j] =Insheet.getCell(j,i).getContents();//getCell(Columns,Rows)
				}
			}
			
			//to separate "cellinfo" to several string
			String DataName[];
			DataName=new String[Insheet.getColumns()];
			for(int i=0;i<Insheet.getColumns();i++)
			{
				DataName[i]=cellinfo[0][i];
			}
			//System.out.println("Rows="+Insheet.getRows());
			//System.out.println("Columns="+Insheet.getColumns());
			int RowAmounts = Insheet.getRows()-1;
			
			String DATE[];
			double SO2[]={0};
			double CO[]={0};
			double CO2[]={0};
			double O3[]={0};
			int PM10[]={0};
			int PM25[]={0};
			double NOx[]={0};
			double NO[]={0};
			double NO2[]={0};
			double THC[]={0};
			double NMHC[]={0};
			double CH4[]={0};
			double WIND_SPEED[]={0};
			double WS_HR[]={0};
			double AMB_TEMP[]={0};
			double RAIN_INT[]={0};
			double PH_RAIN[]={0};
			double RH[]={0};
			double RAIN_COND[]={0};
			
			DATE=new String[RowAmounts];
			SO2=new double[RowAmounts];
			CO=new double[RowAmounts];
			CO2=new double[RowAmounts];
			O3=new double[RowAmounts];
			PM10=new int[RowAmounts];
			PM25=new int[RowAmounts];
			NOx=new double[RowAmounts];
			NO=new double[RowAmounts];
			NO2=new double[RowAmounts];
			THC=new double[RowAmounts];
			NMHC=new double[RowAmounts];
			CH4=new double[RowAmounts];
			WIND_SPEED=new double[RowAmounts];
			WS_HR=new double[RowAmounts];
			AMB_TEMP=new double[RowAmounts];
			RAIN_INT=new double[RowAmounts];
			PH_RAIN=new double[RowAmounts];
			RH=new double[RowAmounts];
			RAIN_COND=new double[RowAmounts];
			
			//input data counter to prevent any repeated input 
			int numSO2=0;
			int numCO=0;
			int numCO2=0;
			int numO3=0;
			int numPM10=0;
			int numPM25=0;
			int numNOx=0;
			int numNO=0;
			int numNO2=0;
			int numTHC=0;
			int numNMHC=0;
			int numCH4=0;
			int numWIND_SPEED=0;
			int numWS_HR=0;
			int numAMB_TEMP=0;
			int numRAIN_INT=0;
			int numPH_RAIN=0;
			int numRH=0;
			int numRAIN_COND=0;
			
			
			//serach for each data's location and pair to its name, 0 means not found
			int SO2Location=0;
			int COLocation=0;
			int CO2Location=0;
			int O3Location=0;
			int PM10Location=0;
			int PM25Location=0;
			int NOxLocation=0;
			int NOLocation=0;
			int NO2Location=0;
			int THCLocation=0;
			int NMHCLocation=0;
			int CH4Location=0;
			int WIND_SPEEDLocation=0;
			int WS_HRLocation=0;
			int AMB_TEMPLocation=0;
			int RAIN_INTLocation=0;
			int PH_RAINLocation=0;
			int RHLocation=0;
			int RAIN_CONDLocation=0;
			
			
			//attach data to its name column and check if any repeated name
			for(int i=0;i<Insheet.getColumns();i++){
				String dateName=StringUtils.substringBetween(DataName[i], "<br />", "<br />");
				if(dateName==null){
					dateName="asdf";
				}

				switch(dateName) { //omit column "DATA"
				case "SO2":
					numSO2++;
					SO2Location=i;
					break;
				case "CO":
					numCO++;
					COLocation=i;
					break;
				case "CO2":
					numCO2++;
					CO2Location=i;
					break;
				case "O3":
					numO3++;
					O3Location=i;
					break;
				case "PM 10 ":
					numPM10++;
					PM10Location=i;
					break;
				case "PM 2.5 ":
					numPM25++;
					PM25Location=i;
					break;
				case "NOx":
					numNOx++;
					NOxLocation=i;
					break;
				case "NO":
					numNO++;
					NOLocation=i;
					break;
				case "NO2":
					numNO2++;
					NO2Location=i;
					break;
				case "THC":
					numTHC++;
					THCLocation=i;
					break;
				case "NMHC":
					numNMHC++;
					NMHCLocation=i;
					break;
				case "CH4":
					numCH4++;
					CH4Location=i;
					break;
				case "WIND_SPEED":
					numWIND_SPEED++;
					WIND_SPEEDLocation=i;
					break;
				case "WS_HR":
					numWS_HR++;
					WS_HRLocation=i;
					break;
				case "AMB_TEMP":
					numAMB_TEMP++;
					AMB_TEMPLocation=i;
					break;
				case "RAIN_INT":
					numRAIN_INT++;
					RAIN_INTLocation=i;
					break;
				case "PH_RAIN":
					numPH_RAIN++;
					PH_RAINLocation=i;
					break;
				case "RH":
					numRH++;
					RHLocation=i;
					break;
				case "RAIN_COND":
					numRAIN_COND++;
					RAIN_CONDLocation=i;
					break;
				default:
					break;
					
				}
			}
			
			System.out.println(fileNameTxt);
			System.out.println("SO2\tCO\tCO2\tO3\tPM10\tPM25\tNOx\tNO\tNO2\tTHC\tNMHC\tCH4\tWIND_SPEED\tWS_HR\tAMB_TEMP\tRAIN_INT\tPH_RAIN\t\tRH\tRAIN_COND");
			System.out.println(numSO2+"\t"+numCO+"\t"+numCO2+"\t"+numO3+"\t"+numPM10+"\t"+numPM25+"\t"+numNOx+"\t"+numNO+"\t"+numNO2+"\t"+numTHC+"\t"+numNMHC+"\t"+numCH4+"\t"+numWIND_SPEED+"\t\t"+numWS_HR+"\t"+numAMB_TEMP+"\t\t"+numRAIN_INT+"\t\t"+numPH_RAIN+"\t\t"+numRH+"\t"+numRAIN_COND);
			
			//check if repeated data exist
			if(numSO2>1||numCO>1||numCO2>1||numO3>1||numPM10>1||numPM25>1||numNOx>1||numNO>1||numNO2>1||numTHC>1||numNMHC>1||numCH4>1||numWIND_SPEED>1||numWS_HR>1||numAMB_TEMP>1||numRAIN_INT>1||numPH_RAIN>1||numRH>1||numRAIN_COND>1)
			{
				System.out.println(fileNameTxt+" Input error, Something is greater than 1");
				System.exit(0);
			}
			
			//separate data from cellinfo
			int ContainLocation=0;
			for(int i=1;i<Insheet.getRows();i++) {	//convert string to int or double
				DATE[ContainLocation]=cellinfo[i][0];
				if(numSO2==1){
					SO2[ContainLocation]=Double.valueOf(cellinfo[i][SO2Location]).doubleValue();
				}
				if(numCO==1){
					CO[ContainLocation]=Double.valueOf(cellinfo[i][COLocation]).doubleValue();
				}
				if(numCO2==1){
					CO2[ContainLocation]=Double.valueOf(cellinfo[i][CO2Location]).doubleValue();
				}
				if(numO3==1){
					O3[ContainLocation]=Double.valueOf(cellinfo[i][O3Location]).doubleValue();
				}
				if(numPM10==1){
					PM10[ContainLocation]=Integer.valueOf(cellinfo[i][PM10Location]).intValue();
				}
				if(numPM25==1){
					PM25[ContainLocation]=Integer.valueOf(cellinfo[i][PM25Location]).intValue();
				}
				if(numNOx==1){
					NOx[ContainLocation]=Double.valueOf(cellinfo[i][NOxLocation]).doubleValue();
				}
				if(numNO==1){
					NO[ContainLocation]=Double.valueOf(cellinfo[i][NOLocation]).doubleValue();
				}
				if(numNO2==1){
					NO2[ContainLocation]=Double.valueOf(cellinfo[i][NO2Location]).doubleValue();
				}
				if(numTHC==1){
					THC[ContainLocation]=Double.valueOf(cellinfo[i][THCLocation]).doubleValue();
				}
				if(numNMHC==1){
					NMHC[ContainLocation]=Double.valueOf(cellinfo[i][NMHCLocation]).doubleValue();
				}
				if(numCH4==1){
					CH4[ContainLocation]=Double.valueOf(cellinfo[i][CH4Location]).doubleValue();
				}
				if(numWIND_SPEED==1){
					WIND_SPEED[ContainLocation]=Double.valueOf(cellinfo[i][WIND_SPEEDLocation]).doubleValue();
				}
				if(numWS_HR==1){
					WS_HR[ContainLocation]=Double.valueOf(cellinfo[i][WS_HRLocation]).doubleValue();
				}					
				if(numAMB_TEMP==1){
					AMB_TEMP[ContainLocation]=Double.valueOf(cellinfo[i][AMB_TEMPLocation]).doubleValue();
				}
				if(numRAIN_INT==1){
					RAIN_INT[ContainLocation]=Double.valueOf(cellinfo[i][RAIN_INTLocation]).doubleValue();
				}
				if(numPH_RAIN==1){
					PH_RAIN[ContainLocation]=Double.valueOf(cellinfo[i][PH_RAINLocation]).doubleValue();
				}
				if(numRH==1){
					RH[ContainLocation]=Double.valueOf(cellinfo[i][RHLocation]).doubleValue();
				}
				if(numRAIN_COND==1){
					RAIN_COND[ContainLocation]=Double.valueOf(cellinfo[i][RAIN_CONDLocation]).doubleValue();
				}				
					ContainLocation++;
			}
			
			//make PM10 = original PM10-PM25
			if(numPM10==1&&numPM25==1) {
				for(int i=0;i<PM10.length;i++) {
					if(PM10[i]!=0) {
						if(PM25[i]!=0) {
							PM10[i] = PM10[i]-PM25[i];
						}else {
							if(DATE[i].equals(DATE[i-1])) {
								PM10[i] = PM10[i]-PM25[i-1];
							}else if(DATE[i].equals(DATE[i+1])) {
								PM10[i] = PM10[i]-PM25[i+1];
							}
						}
					}
				}
			}
			
			
			od.setContainLocation(ContainLocation);
			od.setDate(DATE);
			if(numSO2==1){
				od.setSO2(SO2);
			}
			if(numCO==1){
				od.setCO(CO);
			}
			if(numCO2==1){
				od.setCO2(CO2);
			}
			if(numO3==1){
				od.setO3(O3);
			}
			if(numPM10==1){
				od.setPM10(PM10);
			}
			if(numPM25==1){
				od.setPM25(PM25);
			}
			if(numNOx==1){
				od.setNOx(NOx);
			}
			if(numNO==1){
				od.setNO(NO);
			}
			if(numNO2==1){
				od.setNO2(NO2);
			}
			if(numTHC==1){
				od.setTHC(THC);
			}
			if(numNMHC==1){
				od.setNMHC(NMHC);
			}
			if(numCH4==1){
				od.setCH4(CH4);
			}
			if(numWIND_SPEED==1){
				od.setWIND_SPEED(WIND_SPEED);
			}
			if(numWS_HR==1){
				od.setWS_HR(WS_HR);
			}					
			if(numAMB_TEMP==1){
				od.setAMB_TEMP(AMB_TEMP);
			}
			if(numRAIN_INT==1){
				od.setRAIN_INT(RAIN_INT);
			}
			if(numPH_RAIN==1){
				od.setPH_RAIN(PH_RAIN);
			}
			if(numRH==1){
				od.setRH(RH);
			}
			if(numRAIN_COND==1){
				od.setRAIN_COND(RAIN_COND);
			}				
			System.out.println("Total data inputted ="+ContainLocation+"\n");
			TimeUnit.SECONDS.sleep(3);//delay for 3 seconds
			
			//Data file output
			WriteExcel(DATE,SO2,CO,CO2,O3,PM10,PM25,NOx,NO,NO2,THC,NMHC,CH4,WIND_SPEED,WS_HR,AMB_TEMP,RAIN_INT,PH_RAIN,RH,RAIN_COND
				, newFileRoute,newDataRoute,fileNameTxt,filename,ContainLocation,rounding_off_place,   numSO2,numCO,numCO2,numO3,numPM10
				 ,numPM25,numNOx,numNO,numNO2,numTHC,numNMHC,numCH4,numWIND_SPEED,numWS_HR,numAMB_TEMP,numRAIN_INT,numPH_RAIN,numRH,numRAIN_COND,RowAmounts,dataList,nonStandardizationRoute,fileNameTxt,externalDataRoute,od,realData);
			outC.setList(realData);
			bigRealData.add(outC);
		}
		catch (Exception e)
		{
			System.out.println(fileNameTxt+" readExcel "+e);
		}
		
	}
	
	
	
	/*
	 * x axis=pm2.5 
	 * y axis=pm10 
	 * 
	 * From IBM document:
	 * http://infolib.lotus.com/resources/symphony/3.0.0/sym20abd014/zh_tw/text/schart/01/04050100.html
	 * 
	 * From Microsoft excel document:
	 * SLOPE		https://support.office.com/zh-tw/article/slope-%E5%87%BD%E6%95%B8-11fb8f97-3117-4813-98aa-61d7e01276b9
	 * INTERCEPT	https://support.office.com/zh-tw/article/intercept-%E5%87%BD%E6%95%B8-2a9b74e2-9d47-4772-b663-3bca70bf63ef
	 * RSQ			https://support.office.com/zh-tw/article/rsq-%E5%87%BD%E6%95%B8-d7161715-250d-4a01-b80d-a8364f2be08f
	 * STDEV		https://support.office.com/zh-hk/article/stdev-%E5%87%BD%E6%95%B8-51fecaaa-231e-4bbb-9230-33650a72c9b0
	 * CORREL		https://support.office.com/zh-tw/article/correl-%E5%87%BD%E6%95%B8-995dcef7-0c0a-4bed-a3fb-239d7b68ca92
	 * 
	 */
	public double SLOPE(int[] PM10, int[] PM25,String[] DATE, int CL,int PreviousCL,int rounding_off_place) {//SLOPE(known_y's, known_x's) 
		int sumPM10=0;
		int sumPM25=0;
		int PM10counter=0;
		int PM25counter=0;
		
		for(int i=PreviousCL;i<CL;i++) {
			if(PM10[i]!=0) {
				sumPM10=sumPM10+PM10[i];
				PM10counter++;
			}
			
		}
		for(int i=PreviousCL;i<CL;i++) {
			if(PM25[i]!=0) {
				sumPM25=sumPM25+PM25[i];
				PM25counter++;
			}
			
		}
		
		if(sumPM10==0||sumPM25==0) {//return 0 if all data equal 0
			return 0;
		}
		
		double avgPM10=((double) sumPM10)/PM10counter;
		double avgPM25=((double) sumPM25)/PM25counter;
		double Molecule=0;//up
		double Denominator=0;//bottom
		for(int i=PreviousCL;i<CL;i++) {
			if(PM25[i]!=0&&PM10[i]!=0){
				Molecule=Molecule+((PM25[i]-avgPM25)*(PM10[i]-avgPM10));
				Denominator=Denominator+Math.pow((PM25[i]-avgPM25), 2);
			}else {//deal for the data that not aligned
				if(i+1<DATE.length) {
					if(DATE[i].equals(DATE[i+1])) {
						int correctPM25=0;
						int correctPM10=0;
						for(int j=i;j<=i+1;j++) {
							if(PM25[j]!=0) {
								correctPM25=PM25[j];
							}
							if(PM10[j]!=0) {
								correctPM10=PM10[j];
							}
						}
						Molecule=Molecule+((correctPM25-avgPM25)*(correctPM10-avgPM10));
						Denominator=Denominator+Math.pow((correctPM25-avgPM25), 2);
					}
				}
			}
		}
		
		if(Molecule==0||Denominator==0) {//return 0 if all was not aligned
			return 0;
		}
		return rounding(Molecule/Denominator ,rounding_off_place);
	}
	
	public double INTERCEPT(int[] PM10, int[] PM25, String[] DATE, int CL,int PreviousCL, int rounding_off_place) {//INTERCEPT(known_y's, known_x's)
		int sumPM10=0;
		int sumPM25=0;
		int PM10counter=0;
		int PM25counter=0;
	
		for(int i=PreviousCL;i<CL;i++) {
			if(PM10[i]!=0) {
				sumPM10=sumPM10+PM10[i];
				PM10counter++;
			}
			
		}
		for(int i=PreviousCL;i<CL;i++) {
			if(PM25[i]!=0) {
				sumPM25=sumPM25+PM25[i];
				PM25counter++;
			}
			
		}
		
		if(sumPM25==0||sumPM10==0) {//return 0 if all data equal 0
			return 0;
		}

		double avgPM10=((double) sumPM10)/PM10counter;
		double avgPM25=((double) sumPM25)/PM25counter;
		
		if(SLOPE(PM10,PM25,DATE,CL,PreviousCL,25)==0) {//return 0 if all was not aligned
			return 0;
		}else {
			return rounding(avgPM10-(SLOPE(PM10,PM25,DATE,CL,PreviousCL,25)*avgPM25) ,rounding_off_place);
		}
		
		
	}

	public double RSQ(int[] PM10,int[] PM25, String[] DATE, int CL,int PreviousCL, int rounding_off_place)  {//RSQ(known_y's,known_x's)
		int sumPM10=0;
		int sumPM25=0;
		int PM10counter=0;
		int PM25counter=0;
	
		for(int i=PreviousCL;i<CL;i++) {
			if(PM10[i]!=0) {
				sumPM10=sumPM10+PM10[i];
				PM10counter++;
			}
			if(PM25[i]!=0) {
				sumPM25=sumPM25+PM25[i];
				PM25counter++;
			}
		}
		if(sumPM25==0||sumPM10==0) {//return 0 if all data equal 0
			return 0;
		}
		
		double avgPM10=((double) sumPM10)/PM10counter;
		double avgPM25=((double) sumPM25)/PM25counter;
		double Molecule=0;//up
		
		double Denominator=0;//bottom
		double DenominatorX=0;//PM25
		double DenominatorY=0;//PM10
		
		for(int i=PreviousCL;i<CL;i++) {
			if(PM25[i]!=0&&PM10[i]!=0) {
				Molecule=Molecule+((PM25[i]-avgPM25)*(PM10[i]-avgPM10));
				DenominatorX=DenominatorX+Math.pow(PM25[i]-avgPM25, 2);
				DenominatorY=DenominatorY+Math.pow(PM10[i]-avgPM10, 2);
			}
			else {//deal for the data that not aligned 
				if(i+1<DATE.length) {
					if(DATE[i].equals(DATE[i+1])) {
						int correctPM25=0;
						int correctPM10=0;
						for(int j=i;j<=i+1;j++) {
							if(PM25[j]!=0) {
								correctPM25=PM25[j];
							}
							if(PM10[j]!=0) {
								correctPM10=PM10[j];
							}
						}
						Molecule=Molecule+((correctPM25-avgPM25)*(correctPM10-avgPM10));
						DenominatorX=DenominatorX+Math.pow(correctPM25-avgPM25, 2);
						DenominatorY=DenominatorY+Math.pow(correctPM10-avgPM10, 2);
					}
				}
			}
			
		}
		Denominator=Math.pow(DenominatorX, 0.5)*Math.pow(DenominatorY, 0.5);
		if(Molecule==0||Denominator==0) {//return 0 if all was not aligned
			return 0;
		}else {
			return rounding(Math.pow(Molecule/Denominator, 2) ,rounding_off_place);
		}
		
	}
	
	public double STDEV(double[] Integer, int Location, int PreviousLocation, int rounding_off_place) {
		//calculate average
		double sumInteger=0;
		int counter=0;//to calculate total data amount
		for(int i=PreviousLocation;i<Location;i++) {
			if(Integer[i]!=0) {
				sumInteger=sumInteger+Integer[i];
				counter++;
			}
			
		}
		
		if(sumInteger==0) {//return 0 if all data equal 0
			return 0;
		}
		
		double AverageInteger=sumInteger/counter;
		
		//each element minus the average then add them together
		double Molecule=0;//up
		for(int i=PreviousLocation;i<Location;i++) {
			if(Integer[i]!=0) {
				Molecule=Molecule+Math.pow(AverageInteger-Integer[i], 2);
			}
		}
		if(Molecule==0||counter-1==0) {
			return 0;
		}else {		
			return rounding(Math.pow(Molecule/(counter-1), 0.5) ,rounding_off_place);
		}
		
		
	}
	
	public double CORREL(double data[], double comparedData[]) { //CORREL = RSQ^0.5
		double avgData=0;
		double avgComparedData=0;
		
		for(int i=0;i<data.length;i++) {
			avgData=data[i]+avgData;
			avgComparedData=comparedData[i]+avgComparedData;
		}
		avgData=avgData/data.length;
		avgComparedData=avgComparedData/data.length;
		
		double denominatorX=0;
		double denominatorY=0;
		
		double molecule=0;
		double denominator=0;
		
		for(int i=0;i<data.length;i++) {
			molecule=molecule+(data[i]-avgData)*(comparedData[i]-avgComparedData);
			denominatorX=denominatorX+Math.pow(data[i]-avgData,2);
			denominatorY=denominatorY+Math.pow(comparedData[i]-avgComparedData,2);
		}
		denominator=Math.pow(denominatorX*denominatorY,0.5);
		return (molecule/denominator);
	}
	
	public double ALL_STDEV(double[] Integer, String[] DATE, String Month, int rounding_off_place, int ContainLocation) {
		//calculate average
		double sumInteger=0;
		int counter=0;
		for(int i=0;i<ContainLocation;i++) {
			if(StringUtils.substringBefore(DATE[i], " ").equals(Month)) {
				if(Integer[i]!=0) {
					sumInteger=sumInteger+Integer[i];
					counter++;
				}
			}
		}
		if(sumInteger==0) {//return 0 if all data equal 0
			return 0;
		}
		
		double AverageInteger=sumInteger/counter;
		
		//each element minus the average then add them together
		double Molecule=0;//up
		for(int i=0;i<ContainLocation;i++) {
			if(StringUtils.substringBefore(DATE[i], " ").equals(Month)) {
				if(Integer[i]!=0) {
					Molecule=Molecule+Math.pow(AverageInteger-Integer[i], 2);
				}
				
			}
		}
		return rounding(Math.pow(Molecule/(counter-1), 0.5) ,rounding_off_place);
	}
	
	
	
	
	public double AVERAGE(double[] Integer, int Location, int PreviousLocation, int rounding_off_place) {
		double sumInteger=0;
		int counter=0;
		for(int i=PreviousLocation;i<Location;i++) {
			if(Integer[i]!=0) {
				sumInteger=Integer[i]+sumInteger;
				counter++;
			}
		}
		if(sumInteger==0||counter==0) {//return 0 if all data equal 0
			return 0;
		}
		return rounding(sumInteger/counter ,rounding_off_place);
	}
	public double AVERAGE_INT(int[] Integer, int Location, int PreviousLocation, int rounding_off_place) {
		double sumInteger=0;
		int counter=0;
		for(int i=PreviousLocation;i<Location;i++) {
			if(Integer[i]!=0) {
				sumInteger=Integer[i]+sumInteger;
				counter++;
			}
		}
		if(sumInteger==0||counter==0) {//return 0 if all data equal 0
			return 0;
		}
		return rounding(sumInteger/counter ,rounding_off_place);
	}
	
	public double ALL_AVERAGE(double[] Integer, String[] DATE, String Month, int rounding_off_place, int ContainLocation) {
		double sumInteger=0;
		int counter=0;
		for(int i=0;i<ContainLocation;i++) {
			if(StringUtils.substringBefore(DATE[i], " ").equals(Month)) {
				if(Integer[i]!=0) {
					sumInteger=sumInteger+Integer[i];
					counter++;
				}
			}
		}
		if(sumInteger==0) {//return 0 if all data equal 0
			return 0;
		}
		rounding(sumInteger/counter ,rounding_off_place);	
		return rounding(sumInteger/counter ,rounding_off_place);
	}
	
	public double rounding(double data ,int rounding_off_place) {
		BigDecimal r;
		try {
			r = new BigDecimal(data);
		}catch(NumberFormatException e) {
			return 0;
		}
		BigDecimal value = r.setScale(rounding_off_place,RoundingMode.HALF_UP);
		return value.doubleValue();
	}
	
	public int DATA_AMOUNT(String[] DATE, String Month,  int ContainLocation)//not used
	{
		int counter=0;
		for(int i=0;i<ContainLocation;i++)
		{
			if(StringUtils.substringBefore(DATE[i], " ").equals(Month))
			{
				counter++;
			}
		}
		return counter;
	}
	
	public int DATE_CALCULATION(int CL, int ContainLocation, String OutMonthFirst, String OutYearFirst, String[] DATE)//calculate for the beginning and the ending of the month
	{
		int a=CL;
		while(a<ContainLocation-1) //calculate for the beginning and the ending of the month
		{
			String OutMonthCurrent = StringUtils.substringBefore(DATE[a], " ");
			String OutYearCurrent = DATE[a].substring(DATE[a].length()-4, DATE[a].length());
			
			//current month or year ended
			if(!OutMonthFirst.equals(OutMonthCurrent) || !OutYearFirst.equals(OutYearCurrent))
			{
				return a;
			}
			a++;
		}
		return a;
	}
	
	public double[] double_AmountReduction(double[] data ,int doubleAvgCellCounter) {
		double[] newdouble;
		newdouble=new double[doubleAvgCellCounter];
		boolean ifAllZero=true;
		for(int i=0;i<doubleAvgCellCounter;i++) {
			newdouble[i]=data[i];
			if(data[i]!=0) {
				ifAllZero=false;
			}
		}
		if(ifAllZero) {
			double[] allZero= {0};
			return allZero;
		}
		return newdouble;
	}
	
	public String[] string_AmountReduction(String[] data ,int doubleAvgCellCounter) {
		String[] newstring;
		newstring=new String[doubleAvgCellCounter];
		
		for(int i=0;i<doubleAvgCellCounter;i++) {
			newstring[i]=data[i];
		}
		return newstring;
	}
	
	public void OtherdataOutput_Total_sheet(double[] DATA, WritableSheet OtherDataOutputMonthAvgTotal, WritableSheet OtherDataOutputMonthStdevTotal, int CL, int PreviousCL, int rounding_off_place, String name, int CurrentColumn, int CurrentRow) 
			throws RowsExceededException, WriteException  {
		double AVG=AVERAGE(DATA, CL, PreviousCL, rounding_off_place);
		double STDEV=STDEV(DATA, CL, PreviousCL,rounding_off_place);
		jxl.write.Number data = new jxl.write.Number(CurrentColumn,CurrentRow,AVG);
		jxl.write.Label data_1 = new jxl.write.Label(CurrentColumn,0,name);
		jxl.write.Number data_2 = new jxl.write.Number(CurrentColumn,CurrentRow,STDEV);
		jxl.write.Label data_3 = new jxl.write.Label(CurrentColumn,0,name);
		
		((WritableSheet) OtherDataOutputMonthAvgTotal).addCell(data);
		((WritableSheet) OtherDataOutputMonthAvgTotal).addCell(data_1);
		((WritableSheet) OtherDataOutputMonthStdevTotal).addCell(data_2);
		((WritableSheet) OtherDataOutputMonthStdevTotal).addCell(data_3);
	}
	
	public void OtherdataOutput_Separated_sheet(double[] DATA, WritableSheet OtherDataOutputMonthAvg, WritableSheet OtherDataOutputMonthStdev, int CL, int PreviousCL, int rounding_off_place, String name, int CurrentColumn, int CurrentRow)
			throws RowsExceededException, WriteException {
		double AVG=AVERAGE(DATA, CL, PreviousCL, rounding_off_place);
		double STDEV=STDEV(DATA, CL, PreviousCL,rounding_off_place);
		jxl.write.Number data = new jxl.write.Number(CurrentColumn,CurrentRow,AVG);
		jxl.write.Label data_1 = new jxl.write.Label(CurrentColumn,0,name);
		jxl.write.Number data_2 = new jxl.write.Number(CurrentColumn,CurrentRow,STDEV);
		jxl.write.Label data_3 = new jxl.write.Label(CurrentColumn,0,name);
		
		CurrentColumn++;
		OtherDataOutputMonthAvg.addCell(data);
		OtherDataOutputMonthAvg.addCell(data_1);
		
		OtherDataOutputMonthStdev.addCell(data_2);
		OtherDataOutputMonthStdev.addCell(data_3);
	}
	
	public void OtherdataOutput_Each_Month(double[] DATA, WritableSheet OtherdataoutputAllAvg, WritableSheet OtherdataoutputAllStdev, String[] DATE, String OutputDate1, int rounding_off_place, int ContainLocation, int OtherdataOutputAllCurrentColumn, int OtherdataOutputAllCurrentRow, String name)
			throws RowsExceededException, WriteException {
		double AVG=ALL_AVERAGE(DATA,DATE,OutputDate1,rounding_off_place, ContainLocation);
		double STDEV=ALL_STDEV(DATA,DATE,OutputDate1,rounding_off_place, ContainLocation);
		jxl.write.Number data = new jxl.write.Number(OtherdataOutputAllCurrentColumn,OtherdataOutputAllCurrentRow,AVG);
		jxl.write.Label data_1 = new jxl.write.Label(OtherdataOutputAllCurrentColumn,0,name);
		jxl.write.Number data_2 = new jxl.write.Number(OtherdataOutputAllCurrentColumn,OtherdataOutputAllCurrentRow,STDEV);
		jxl.write.Label data_3 = new jxl.write.Label(OtherdataOutputAllCurrentColumn,0,name);
		//jxl.write.Number data_4 = new jxl.write.Number(OtherdataOutputAllCurrentColumn,15,AVERAGE(DATA,ContainLocation,0, rounding_off_place));
		//jxl.write.Number data_5 = new jxl.write.Number(OtherdataOutputAllCurrentColumn,15,STDEV(DATA,ContainLocation,0, rounding_off_place));
		
		OtherdataoutputAllAvg.addCell(data);
		OtherdataoutputAllAvg.addCell(data_1);
		//OtherdataoutputAllAvg.addCell(data_4);
		
		OtherdataoutputAllStdev.addCell(data_2);
		OtherdataoutputAllStdev.addCell(data_3);
		//OtherdataoutputAllStdev.addCell(data_5);
	}
	
	public void WriteExcel_Write(double DATA, WritableSheet Outsheet, int CurrentColumn, int CurrentRow, String name) 
			throws RowsExceededException, WriteException {
		jxl.write.Number data = new jxl.write.Number(CurrentColumn,CurrentRow,DATA);
		jxl.write.Label data_1 = new jxl.write.Label(CurrentColumn,0,name);
		Outsheet.addCell(data);
		Outsheet.addCell(data_1);
	}
	
	public void DoubleCalculation(double[] DATA_AVG_AVG, double[] DATA_STDEV_AVG, int doubleAvgCellCounter, int CurrentColumn, WritableSheet Avg, WritableSheet Stdev, int rounding_off_place) 
			throws RowsExceededException, WriteException {
		jxl.write.Number data = new jxl.write.Number(CurrentColumn,19, AVERAGE(DATA_AVG_AVG,doubleAvgCellCounter,0,rounding_off_place));
		jxl.write.Number data_1 = new jxl.write.Number(CurrentColumn,20,STDEV(DATA_AVG_AVG,doubleAvgCellCounter,0,rounding_off_place));
		jxl.write.Number data_2 = new jxl.write.Number(CurrentColumn,21,AVERAGE(DATA_AVG_AVG,doubleAvgCellCounter,0,rounding_off_place)+2*STDEV(DATA_AVG_AVG,doubleAvgCellCounter,0,rounding_off_place));
		jxl.write.Number data_3 = new jxl.write.Number(CurrentColumn,22,AVERAGE(DATA_AVG_AVG,doubleAvgCellCounter,0,rounding_off_place)+STDEV(DATA_AVG_AVG,doubleAvgCellCounter,0,rounding_off_place));
		jxl.write.Number data_4 = new jxl.write.Number(CurrentColumn,23,AVERAGE(DATA_AVG_AVG,doubleAvgCellCounter,0,rounding_off_place)-STDEV(DATA_AVG_AVG,doubleAvgCellCounter,0,rounding_off_place));
		jxl.write.Number data_5 = new jxl.write.Number(CurrentColumn,24,AVERAGE(DATA_AVG_AVG,doubleAvgCellCounter,0,rounding_off_place)-2*STDEV(DATA_AVG_AVG,doubleAvgCellCounter,0,rounding_off_place));
		
		jxl.write.Number data_6 = new jxl.write.Number(CurrentColumn,19,AVERAGE(DATA_STDEV_AVG,doubleAvgCellCounter,0,rounding_off_place));
		jxl.write.Number data_7 = new jxl.write.Number(CurrentColumn,20,STDEV(DATA_STDEV_AVG,doubleAvgCellCounter,0,rounding_off_place));
		jxl.write.Number data_8 = new jxl.write.Number(CurrentColumn,21,AVERAGE(DATA_STDEV_AVG,doubleAvgCellCounter,0,rounding_off_place)+2*STDEV(DATA_STDEV_AVG,doubleAvgCellCounter,0,rounding_off_place));
		jxl.write.Number data_9 = new jxl.write.Number(CurrentColumn,22,AVERAGE(DATA_STDEV_AVG,doubleAvgCellCounter,0,rounding_off_place)+STDEV(DATA_STDEV_AVG,doubleAvgCellCounter,0,rounding_off_place));
		jxl.write.Number data_10 = new jxl.write.Number(CurrentColumn,23,AVERAGE(DATA_STDEV_AVG,doubleAvgCellCounter,0,rounding_off_place)-STDEV(DATA_STDEV_AVG,doubleAvgCellCounter,0,rounding_off_place));
		jxl.write.Number data_11 = new jxl.write.Number(CurrentColumn,24,AVERAGE(DATA_STDEV_AVG,doubleAvgCellCounter,0,rounding_off_place)-2*STDEV(DATA_STDEV_AVG,doubleAvgCellCounter,0,rounding_off_place));
		
		Avg.addCell(data);
		Avg.addCell(data_1);
		Avg.addCell(data_2);
		Avg.addCell(data_3);
		Avg.addCell(data_4);
		Avg.addCell(data_5);
		
		Stdev.addCell(data_6);
		Stdev.addCell(data_7);
		Stdev.addCell(data_8);
		Stdev.addCell(data_9);
		Stdev.addCell(data_10);
		Stdev.addCell(data_11);
	}
	
	public void DoubleCalculation_Regression_Analysis(double[] R2, double[] a, double[] b, int R2Column, int aColumn, int bColumn, WritableSheet PM10PM25BookRegressionAnalysisOutput, int doubleAvgCellCounter, int rounding_off_place) 
			throws RowsExceededException, WriteException {
		jxl.write.Number data = new jxl.write.Number(R2Column,19, AVERAGE(R2,doubleAvgCellCounter,0,rounding_off_place));
		jxl.write.Number data_1 = new jxl.write.Number(R2Column,20,STDEV(R2,doubleAvgCellCounter,0,rounding_off_place));
		jxl.write.Number data_2 = new jxl.write.Number(R2Column,21,AVERAGE(R2,doubleAvgCellCounter,0,rounding_off_place)+2*STDEV(R2,doubleAvgCellCounter,0,rounding_off_place));
		jxl.write.Number data_3 = new jxl.write.Number(R2Column,22,AVERAGE(R2,doubleAvgCellCounter,0,rounding_off_place)+STDEV(R2,doubleAvgCellCounter,0,rounding_off_place));
		jxl.write.Number data_4 = new jxl.write.Number(R2Column,23,AVERAGE(R2,doubleAvgCellCounter,0,rounding_off_place)-STDEV(R2,doubleAvgCellCounter,0,rounding_off_place));
		jxl.write.Number data_5 = new jxl.write.Number(R2Column,24,AVERAGE(R2,doubleAvgCellCounter,0,rounding_off_place)-2*STDEV(R2,doubleAvgCellCounter,0,rounding_off_place));
		
		jxl.write.Number data_6 = new jxl.write.Number(aColumn,19, AVERAGE(a,doubleAvgCellCounter,0,rounding_off_place));
		jxl.write.Number data_7 = new jxl.write.Number(aColumn,20,STDEV(a,doubleAvgCellCounter,0,rounding_off_place));
		jxl.write.Number data_8 = new jxl.write.Number(aColumn,21,AVERAGE(a,doubleAvgCellCounter,0,rounding_off_place)+2*STDEV(a,doubleAvgCellCounter,0,rounding_off_place));
		jxl.write.Number data_9 = new jxl.write.Number(aColumn,22,AVERAGE(a,doubleAvgCellCounter,0,rounding_off_place)+STDEV(a,doubleAvgCellCounter,0,rounding_off_place));
		jxl.write.Number data_10 = new jxl.write.Number(aColumn,23,AVERAGE(a,doubleAvgCellCounter,0,rounding_off_place)-STDEV(a,doubleAvgCellCounter,0,rounding_off_place));
		jxl.write.Number data_11 = new jxl.write.Number(aColumn,24,AVERAGE(a,doubleAvgCellCounter,0,rounding_off_place)-2*STDEV(a,doubleAvgCellCounter,0,rounding_off_place));
		
		jxl.write.Number data_12 = new jxl.write.Number(bColumn,19, AVERAGE(b,doubleAvgCellCounter,0,rounding_off_place));
		jxl.write.Number data_13 = new jxl.write.Number(bColumn,20,STDEV(b,doubleAvgCellCounter,0,rounding_off_place));
		jxl.write.Number data_14 = new jxl.write.Number(bColumn,21,AVERAGE(b,doubleAvgCellCounter,0,rounding_off_place)+2*STDEV(b,doubleAvgCellCounter,0,rounding_off_place));
		jxl.write.Number data_15 = new jxl.write.Number(bColumn,22,AVERAGE(b,doubleAvgCellCounter,0,rounding_off_place)+STDEV(b,doubleAvgCellCounter,0,rounding_off_place));
		jxl.write.Number data_16 = new jxl.write.Number(bColumn,23,AVERAGE(b,doubleAvgCellCounter,0,rounding_off_place)-STDEV(b,doubleAvgCellCounter,0,rounding_off_place));
		jxl.write.Number data_17 = new jxl.write.Number(bColumn,24,AVERAGE(b,doubleAvgCellCounter,0,rounding_off_place)-2*STDEV(b,doubleAvgCellCounter,0,rounding_off_place));
		
		jxl.write.Label label = new jxl.write.Label(0,19,"AVERAGE");
		jxl.write.Label label_1 = new jxl.write.Label(0,20,"STDEV");
		jxl.write.Label label_2 = new jxl.write.Label(0,21,"2 STDEV greater than AVERAGE");
		jxl.write.Label label_3 = new jxl.write.Label(0,22,"1 STDEV greater than AVERAGE");
		jxl.write.Label label_4 = new jxl.write.Label(0,23,"1 STDEV smaller than AVERAGE");
		jxl.write.Label label_5 = new jxl.write.Label(0, 24,"2 STDEV samller than AVERAGE");
		
		PM10PM25BookRegressionAnalysisOutput.addCell(data);
		PM10PM25BookRegressionAnalysisOutput.addCell(data_1);
		PM10PM25BookRegressionAnalysisOutput.addCell(data_2);
		PM10PM25BookRegressionAnalysisOutput.addCell(data_3);
		PM10PM25BookRegressionAnalysisOutput.addCell(data_4);
		PM10PM25BookRegressionAnalysisOutput.addCell(data_5);
		
		PM10PM25BookRegressionAnalysisOutput.addCell(data_6);
		PM10PM25BookRegressionAnalysisOutput.addCell(data_7);
		PM10PM25BookRegressionAnalysisOutput.addCell(data_8);
		PM10PM25BookRegressionAnalysisOutput.addCell(data_9);
		PM10PM25BookRegressionAnalysisOutput.addCell(data_10);
		PM10PM25BookRegressionAnalysisOutput.addCell(data_11);
		
		PM10PM25BookRegressionAnalysisOutput.addCell(data_12);
		PM10PM25BookRegressionAnalysisOutput.addCell(data_13);
		PM10PM25BookRegressionAnalysisOutput.addCell(data_14);
		PM10PM25BookRegressionAnalysisOutput.addCell(data_15);
		PM10PM25BookRegressionAnalysisOutput.addCell(data_16);
		PM10PM25BookRegressionAnalysisOutput.addCell(data_17);
		
		PM10PM25BookRegressionAnalysisOutput.addCell(label);
		PM10PM25BookRegressionAnalysisOutput.addCell(label_1);
		PM10PM25BookRegressionAnalysisOutput.addCell(label_2);
		PM10PM25BookRegressionAnalysisOutput.addCell(label_3);
		PM10PM25BookRegressionAnalysisOutput.addCell(label_4);
		PM10PM25BookRegressionAnalysisOutput.addCell(label_5);
	}
	
	
	public void ModifyExcel(String route) throws IOException, WriteException, BiffException { //For Coloring
		File file = new File(route);
		if(file.isDirectory()) {
			File[] files = file.listFiles();
			for(File f:files) {
				if(f.getName().indexOf(".xls")>0) {
					String fileRoute=f.getAbsolutePath();
					//read only
					 jxl.Workbook Workbook_in =null;
					 InputStream is = new FileInputStream(fileRoute);
					 Workbook_in = Workbook.getWorkbook(is);
			         
					 //for write out
					 jxl.write.WritableWorkbook Workbook_out= Workbook.createWorkbook(new File(fileRoute), Workbook_in);
					 
					 WritableCellFormat red = new WritableCellFormat();
			         red.setBackground(Colour.RED);
			         
			         WritableCellFormat yellow = new WritableCellFormat();
			         yellow.setBackground(Colour.YELLOW);
			         
			         WritableCellFormat green = new WritableCellFormat();
			         green.setBackground(Colour.BRIGHT_GREEN);
			         
			         WritableCellFormat blue = new WritableCellFormat();
			         blue.setBackground(Colour.SKY_BLUE);
					 
					 for(int i=0;i<Workbook_out.getNumberOfSheets();i++) {
						 Sheet sheet = Workbook_out.getSheet(i);
						 if(sheet.getName().equals("Total")) {
							 continue;
						 }
						 if(f.getName().indexOf("Regression_Analysis")>=0) {
							 int[] culumnLoc= {4,10,13};
							 for(int a=0;a<3;a++) {
								 String Condition_IN[]=null;
								 Condition_IN =new String[6];
								 
								 double Condition[]= {0};
								 Condition = new double[6];
								 
								 String[] DATA_IN= null;
								 DATA_IN=new String[20];
								 
								 double[] DATA= {0};
								 DATA=new double[20];
								 int z=1;
								 while(true) {
									 if(sheet.getCell(culumnLoc[a],z).getContents().isBlank()) {
										 break;
									 }
									 DATA_IN[z]=sheet.getCell(culumnLoc[a],z).getContents();//getCell(Columns,Rows)
									 DATA[z]=Double.valueOf(DATA_IN[z]).doubleValue();
									 z++;
								 }
								 int counter=0;
								 for(int c=19;c<=24;c++) {
									 Condition_IN[counter]=sheet.getCell(culumnLoc[a],c).getContents();//getCell(Columns,Rows)
									 Condition[counter]=Double.valueOf(Condition_IN[counter]).doubleValue();
									 counter++;					 
								 }
								 for(int b=1;b<z;b++) {
									 if(DATA[b]!=0) {
										 if(DATA[b]>Condition[2]) {
												jxl.write.Number number=new jxl.write.Number(culumnLoc[a],b,DATA[b]);
												number.setCellFormat(red);
												((WritableSheet) sheet).addCell(number);
											}
											else if(DATA[b]<=Condition[2] && DATA[b]>=Condition[3]) {
												jxl.write.Number number=new jxl.write.Number(culumnLoc[a],b,DATA[b]);
												number.setCellFormat(yellow);
												((WritableSheet) sheet).addCell(number);
											}
											else if(DATA[b]<=Condition[4] && DATA[b]>=Condition[5]) {
												jxl.write.Number number=new jxl.write.Number(culumnLoc[a],b,DATA[b]);
												number.setCellFormat(green);
												((WritableSheet) sheet).addCell(number);
											}
											else if(DATA[b]<Condition[5]) {
												jxl.write.Number number=new jxl.write.Number(culumnLoc[a],b,DATA[b]);
												number.setCellFormat(blue);
												((WritableSheet) sheet).addCell(number);
											}
									 }
								 }
							 }
						 }
						 else {
							 for(int a=1;a<sheet.getColumns();a++) {
								 String Condition_IN[]=null;
								 Condition_IN =new String[6];
								 
								 double Condition[]= {0};
								 Condition = new double[6];
								 
								 String[] DATA_IN= null;
								 DATA_IN=new String[20];
								 
								 double[] DATA= {0};
								 DATA=new double[20];
								 int z=1;
								 while(true)
								 {
									 if(sheet.getCell(a,z).getContents().isBlank())
									 {
										 break;
									 }
									 DATA_IN[z]=sheet.getCell(a,z).getContents();//getCell(Columns,Rows)
									 DATA[z]=Double.valueOf(DATA_IN[z]).doubleValue();
									 z++;
								 }
								 int counter=0;
								 for(int c=19;c<=24;c++)
								 {
									 Condition_IN[counter]=sheet.getCell(a,c).getContents();//getCell(Columns,Rows)
									 Condition[counter]=Double.valueOf(Condition_IN[counter]).doubleValue();
									 counter++;					 
								 }
								 
								 for(int b=1;b<z;b++)
								 {
									 if(DATA[b]!=0)
									 {
										 if(DATA[b]>Condition[2])
											{
												jxl.write.Number number=new jxl.write.Number(a,b,DATA[b]);
												number.setCellFormat(red);
												((WritableSheet) sheet).addCell(number);
											}
											else if(DATA[b]<=Condition[2] && DATA[b]>=Condition[3])
											{
												jxl.write.Number number=new jxl.write.Number(a,b,DATA[b]);
												number.setCellFormat(yellow);
												((WritableSheet) sheet).addCell(number);
											}
											else if(DATA[b]<=Condition[4] && DATA[b]>=Condition[5]) 
											{
												jxl.write.Number number=new jxl.write.Number(a,b,DATA[b]);
												number.setCellFormat(green);
												((WritableSheet) sheet).addCell(number);
											}
											else if(DATA[b]<Condition[5])
											{
												jxl.write.Number number=new jxl.write.Number(a,b,DATA[b]);
												number.setCellFormat(blue);
												((WritableSheet) sheet).addCell(number);
											}
									 }
									 
								 }
								 
							 }
						 }
						
						 
						 
						 
					 }
					 
					 Workbook_out.write();
					 Workbook_out.close();
					 System.gc();
				}
					System.out.println(f.getName().replace(".xls", "")+"\tModified\n");
			}
				
		}
	}
	
	public void readModifiedExcel_PM10PM25(String PM10PM25Route, List dataList) throws BiffException, IOException {
		File fileLocation= new File(PM10PM25Route);
		
		if(fileLocation.isDirectory()) {
			File[] files = fileLocation.listFiles();
			for(File f:files) {
				if(f.getName().indexOf("All__")>=0) {
					InputStream is = new FileInputStream(f.getAbsolutePath());
					
					Workbook wb = Workbook.getWorkbook(is);
					for(int i=0;i<wb.getNumberOfSheets();i++) {
						Sheet sheet=wb.getSheet(i);
						if(sheet.getName().equals("AVG")) {
							for(int j=1;j<13;j++){
								String DATE=sheet.getCell(0,j).getContents();
								double Current_PM10=Double.valueOf(sheet.getCell(1,j).getContents()).doubleValue();
								double Current_PM25=Double.valueOf(sheet.getCell(2,j).getContents()).doubleValue();
								double PM10_2sg=Double.valueOf(sheet.getCell(1,21).getContents()).doubleValue();
								double PM10_sg=Double.valueOf(sheet.getCell(1,22).getContents()).doubleValue();
								double PM10_sb=Double.valueOf(sheet.getCell(1,23).getContents()).doubleValue();
								double PM10_2sb=Double.valueOf(sheet.getCell(1,24).getContents()).doubleValue();
								double PM25_2sg=Double.valueOf(sheet.getCell(2,21).getContents()).doubleValue();
								double PM25_sg=Double.valueOf(sheet.getCell(2,22).getContents()).doubleValue();
								double PM25_sb=Double.valueOf(sheet.getCell(2,23).getContents()).doubleValue();
								double PM25_2sb=Double.valueOf(sheet.getCell(2,24).getContents()).doubleValue();
								for(int k=0;k<dataList.size();k++) {
									boolean ifFound=false;
									List<Position> mon = (List<Position>) dataList.get(k);
									
									for(int l=0;l<mon.size();l++) {
										Position pos = (Position) mon.get(l);
										if(f.getName().indexOf(pos.getName())>=0) {
											if(pos.getMonth().equals(DATE)) {
												
												//writeListData(int CurrentData, int CurrentData_2sg, int CurrentData_sg, int CurrentData_sb, int CurrentData_2sb)
												pos.setPM10(writeListData(Current_PM10, PM10_2sg, PM10_sg, PM10_sb, PM10_2sb));
												pos.setPM25(writeListData(Current_PM25, PM25_2sg, PM25_sg, PM25_sb, PM25_2sb));
												//ifFound=true;
												//break;
											}
										}	
									}
									/*if(ifFound) {
										break;
									}*/
								}
							}	
						}	
					}
				}	
			}
		}
		System.gc();
	}
		
	public void readModifiedExcel_Other(String otherDataRoute, List dataList) throws BiffException, IOException {

		File fileLocation= new File(otherDataRoute);
		
		if(fileLocation.isDirectory())
		{
			File[] files = fileLocation.listFiles();
			for(File f:files)
			{
				if(f.getName().indexOf("All")>=0)
				{
					InputStream is = new FileInputStream(f.getAbsolutePath());
					
					Workbook wb = Workbook.getWorkbook(is);
					for(int i=0;i<wb.getNumberOfSheets();i++)
					{
						Sheet sheet=wb.getSheet(i);
						if(sheet.getName().equals("AVG"))
						{
							for(int jj=1;jj<sheet.getColumns();jj++) {//Column
								String name=sheet.getCell(jj, 0).getContents();//getCell(Columns,Rows)
								for(int j=1;j<13;j++){//Row
									String DATE=sheet.getCell(0,j).getContents();
									double CurrentData=Double.valueOf(sheet.getCell(jj,j).getContents()).doubleValue();
									double CurrentData_2sg=Double.valueOf(sheet.getCell(jj,21).getContents()).doubleValue();
									double CurrentData_sg=Double.valueOf(sheet.getCell(jj,22).getContents()).doubleValue();
									double CurrentData_sb=Double.valueOf(sheet.getCell(jj,23).getContents()).doubleValue();
									double CurrentData_2sb=Double.valueOf(sheet.getCell(jj,24).getContents()).doubleValue();
									for(int k=0;k<dataList.size();k++) {
										boolean ifFound=false;
										List<Position> mon = (List<Position>) dataList.get(k);
										
										for(int l=0;l<mon.size();l++) {
											Position pos = (Position) mon.get(l);
											if(f.getName().indexOf(pos.getName())>=0) {
												if(pos.getMonth().equals(DATE)) {
													if(name.indexOf("SO2")>=0) {
														pos.setSO2(writeListData(CurrentData, CurrentData_2sg, CurrentData_sg, CurrentData_sb, CurrentData_2sb));
													}else if(name.indexOf("CO")>=0) {
														pos.setCO(writeListData(CurrentData, CurrentData_2sg, CurrentData_sg, CurrentData_sb, CurrentData_2sb));
													}else if(name.indexOf("CO2")>=0) {
														pos.setCO2(writeListData(CurrentData, CurrentData_2sg, CurrentData_sg, CurrentData_sb, CurrentData_2sb));
													}else if(name.indexOf("O3")>=0) {
														pos.setO3(writeListData(CurrentData, CurrentData_2sg, CurrentData_sg, CurrentData_sb, CurrentData_2sb));
													}else if(name.indexOf("NOx")>=0) {
														pos.setNOx(writeListData(CurrentData, CurrentData_2sg, CurrentData_sg, CurrentData_sb, CurrentData_2sb));
													}else if(name.indexOf("NO")>=0) {
														pos.setNO(writeListData(CurrentData, CurrentData_2sg, CurrentData_sg, CurrentData_sb, CurrentData_2sb));
													}else if(name.indexOf("NO2")>=0) {
														pos.setNO2(writeListData(CurrentData, CurrentData_2sg, CurrentData_sg, CurrentData_sb, CurrentData_2sb));
													}else if(name.indexOf("THC")>=0) {
														pos.setTHC(writeListData(CurrentData, CurrentData_2sg, CurrentData_sg, CurrentData_sb, CurrentData_2sb));
													}else if(name.indexOf("NMHC")>=0) {
														pos.setNMHC(writeListData(CurrentData, CurrentData_2sg, CurrentData_sg, CurrentData_sb, CurrentData_2sb));
													}else if(name.indexOf("CH4")>=0) {
														pos.setCH4(writeListData(CurrentData, CurrentData_2sg, CurrentData_sg, CurrentData_sb, CurrentData_2sb));
													}else if(name.indexOf("WIND_SPEED")>=0) {
														pos.setWIND_SPEED(writeListData(CurrentData, CurrentData_2sg, CurrentData_sg, CurrentData_sb, CurrentData_2sb));
													}else if(name.indexOf("WS_HR")>=0) {
														pos.setWS_HR(writeListData(CurrentData, CurrentData_2sg, CurrentData_sg, CurrentData_sb, CurrentData_2sb));
													}else if(name.indexOf("AMB_TEMP")>=0) {
														pos.setAMB_TEMP(writeListData(CurrentData, CurrentData_2sg, CurrentData_sg, CurrentData_sb, CurrentData_2sb));
													}else if(name.indexOf("RAIN_INT")>=0) {
														pos.setRAIN_INT(writeListData(CurrentData, CurrentData_2sg, CurrentData_sg, CurrentData_sb, CurrentData_2sb));
													}else if(name.indexOf("PH_RAIN")>=0) {
														pos.setPH_RAIN(writeListData(CurrentData, CurrentData_2sg, CurrentData_sg, CurrentData_sb, CurrentData_2sb));
													}else if(name.indexOf("RH")>=0) {
														pos.setRH(writeListData(CurrentData, CurrentData_2sg, CurrentData_sg, CurrentData_sb, CurrentData_2sb));
													}else if(name.indexOf("RAIN_COND")>=0) {
														pos.setRAIN_COND(writeListData(CurrentData, CurrentData_2sg, CurrentData_sg, CurrentData_sb, CurrentData_2sb));
													}
													
													//ifFound=true;
													//break;
												}
											}	
										}
										/*if(ifFound) {
											break;
										}*/
									}
								}
							}	
						}	
					}
				}	
			}
		}
		System.gc();
	}
	
	public int writeListData(double current, double current_2sg, double current_sg, double current_sb, double current_2sb) {
		if(current==0) {
			return 0;
		}else if(current>current_2sg) {
			return 3;
		}else if(current<=current_2sg&&current>=current_sg) {
			return 2;
		}else if(current>=current_2sb&&current<=current_sb) {
			return -1;
		}
		else if(current<current_2sb) {
			return -2;
		}
		return 1;
	}
	
	public void PM10PM25Output(int[] PM10, int[] PM25, String DATE[], String newDataRoute, String fileNameTxt, String filename,int ContainLocation,  int rounding_off_place ,List dataList, List realData) {
		try {
			//make new folder
			String PM10PM25Route=newDataRoute+"/"+"PM10_PM25_"+fileNameTxt;
			File dir_file = new File(PM10PM25Route);
			dir_file.mkdir();
			
			WritableWorkbook PM10PM25BookRegressionAnalysis = Workbook.createWorkbook(new File(PM10PM25Route + "/"+ "Regression_Analysis__"+fileNameTxt+ ".xls"));
			WritableWorkbook PM10PM25BookAvg = Workbook.createWorkbook(new File(PM10PM25Route + "/"+ "AVG__"+fileNameTxt+ ".xls"));
			WritableWorkbook PM10PM25BookStdev = Workbook.createWorkbook(new File(PM10PM25Route + "/"+ "STDEV__"+fileNameTxt+ ".xls"));
			WritableWorkbook PM10PM25BookAll = Workbook.createWorkbook(new File(PM10PM25Route + "/"+ "All__"+fileNameTxt+ ".xls"));
			
			//convert int to double for other calculation
			double DoublePM10[];
			double DoublePM25[];
			DoublePM10=new double[PM10.length];
			DoublePM25=new double[PM25.length];
			for(int i=0;i<ContainLocation;i++) {
				DoublePM10[i]=(double)PM10[i];
				DoublePM25[i]=(double)PM25[i];
			}
			
			int RegressionAnalysisCurrentDataRowMonth=0;//output excel row counter
			int Regression_AnalysisCurrentDataRowTotal = 0;
			
			////store further data from calculated data
			int cellAmouunt=20;
			int doubleAvgCellCounter=0;
			double[] PM10_AVG_AVG= {0};
			double[] PM25_AVG_AVG= {0};
			double[] R2= {0};
			double[] a= {0};
			double[] b= {0};
			
			PM10_AVG_AVG=new double[cellAmouunt];
			PM25_AVG_AVG=new double[cellAmouunt];
			R2=new double[cellAmouunt];
			a=new double[cellAmouunt];
			b=new double[cellAmouunt];
			
			double[] PM10_STDEV_AVG={0};
			double[] PM25_STDEV_AVG={0};
			
			PM10_STDEV_AVG=new double[cellAmouunt];
			PM25_STDEV_AVG=new double[cellAmouunt];
			
			
			String OutMonthFirst;
			String OutYearFirst;
			OutMonthFirst = StringUtils.substringBefore(DATE[0], " ");
			OutYearFirst = DATE[0].substring(DATE[0].length()-4, DATE[0].length());
			
			String month[]= {"December", "November", "October", "September", "August", "July", "June", "May", "April", "March", "February", "January"};
			
			int CL=0;
			int PreviousCL=0;
			int PM10PM25BookRegressionAnalysisSheetCounter=1; //y=ax+b,R^2
			int PM10PM25BookAvgSheetCounter=1;//Monthly AVERAGE
			int PM10PM25BookStdevSheetCounter=1;//Monthly STDEV
			
			

			WritableSheet PM10PM25BookRegressionAnalysisOutputTotal = PM10PM25BookRegressionAnalysis.createSheet("Total",0);//Monthly y=ax+b,R^2
			WritableSheet PM10PM25BookAvgOutputTotal = PM10PM25BookAvg.createSheet("Total",0);//Monthly AVERAGE
			WritableSheet PM10PM25BookStdevOutputTotal = PM10PM25BookStdev.createSheet("Total",0);//Monthly STDEV
			
			WritableSheet PM10PM25BookAllOutputAvg = PM10PM25BookAll.createSheet("AVG",0);//Yearly AVERAGE
			WritableSheet PM10PM25BookAllOutputStdev = PM10PM25BookAll.createSheet("STDEV",1);//Yearly STDEV
			
			
			
			while(true) {//calculate for the beginning and the ending of the month	for total-sheet
				if(CL>=ContainLocation-1) {
					break;
				}
				
				CL=DATE_CALCULATION(CL, ContainLocation, OutMonthFirst, OutYearFirst, DATE);
				
	
				OutMonthFirst = StringUtils.substringBefore(DATE[CL], " ");
				OutYearFirst = DATE[CL].substring(DATE[CL].length()-4, DATE[CL].length());
				
				String OutputDate =StringUtils.substringBefore(DATE[PreviousCL], " ")+" ,"+DATE[PreviousCL].substring(DATE[PreviousCL].length()-4, DATE[PreviousCL].length());
				double SLOPE=SLOPE(PM10,PM25,DATE,CL,PreviousCL,rounding_off_place);
				double INTERCEPT=INTERCEPT(PM10,PM25,DATE,CL,PreviousCL,rounding_off_place);
				double RSQ=RSQ(PM10,PM25,DATE,CL,PreviousCL,rounding_off_place);
				double PM10AVERAGE=AVERAGE(DoublePM10,CL,PreviousCL,rounding_off_place);
				double PM25AVERAGE=AVERAGE(DoublePM25,CL,PreviousCL,rounding_off_place);
				double PM10STDEV=STDEV(DoublePM10, CL, PreviousCL, rounding_off_place);
				double PM25STDEV=STDEV(DoublePM25, CL, PreviousCL, rounding_off_place);
				//int DATA_AMOUNT=CL-PreviousCL;
				//System.out.println(OutputDate+"  "+"y="+SLOPE+"*x+"+INTERCEPT+"   "+"R^2="+RSQ);
				
				//DATA1-20 for Regression_Analysis_TOTAL
				jxl.write.Label DATA1 = new jxl.write.Label(0,Regression_AnalysisCurrentDataRowTotal+1,OutputDate);
				jxl.write.Label DATA2 = new jxl.write.Label(1,Regression_AnalysisCurrentDataRowTotal+1,"y="+SLOPE+"*x+"+INTERCEPT);
				jxl.write.Number DATA4 = new jxl.write.Number(4,Regression_AnalysisCurrentDataRowTotal+1,RSQ);
				//jxl.write.Number DATA6 = new jxl.write.Number(7,Regression_AnalysisCurrentDataRowTotal+1,(CL-PreviousCL));
				jxl.write.Number DATA8 = new jxl.write.Number(10,Regression_AnalysisCurrentDataRowTotal+1,SLOPE);
				jxl.write.Number DATA10 = new jxl.write.Number(13,Regression_AnalysisCurrentDataRowTotal+1,INTERCEPT);
				
				jxl.write.Label DATA11 = new jxl.write.Label(0,0,"DATE");
				jxl.write.Label DATA12 = new jxl.write.Label(1,0,"y= a*x+ b");
				jxl.write.Label DATA3 = new jxl.write.Label(4,0,"R^2");
				//jxl.write.Label DATA5 = new jxl.write.Label(7,0,"DATA_AMOUNT");
				jxl.write.Label DATA7 = new jxl.write.Label(10,0,"a");
				jxl.write.Label DATA9 = new jxl.write.Label(13,0,"b");
				
				
				//DATA21-30 for AVERAGE_TOTAL
				jxl.write.Label DATA21 = new jxl.write.Label(0,Regression_AnalysisCurrentDataRowTotal+1,OutputDate);
				jxl.write.Number DATA22 = new jxl.write.Number(1,Regression_AnalysisCurrentDataRowTotal+1,PM10AVERAGE);
				jxl.write.Number DATA23 = new jxl.write.Number(2,Regression_AnalysisCurrentDataRowTotal+1,PM25AVERAGE);
				jxl.write.Label DATA24 = new jxl.write.Label(0,0,"DATE");
				jxl.write.Label DATA25 = new jxl.write.Label(1,0,"PM10");
				jxl.write.Label DATA26 = new jxl.write.Label(2,0,"PM2.5");
				//jxl.write.Label DATA27 = new jxl.write.Label(3,0,"DATA_AMOUNT");
				//jxl.write.Number DATA28 = new jxl.write.Number(3,Regression_AnalysisCurrentDataRowTotal+1,DATA_AMOUNT);
				
				
				//DATA31-40 for STDEV_TOTAL
				jxl.write.Label DATA31 = new jxl.write.Label(0,Regression_AnalysisCurrentDataRowTotal+1,OutputDate);
				jxl.write.Number DATA32 = new jxl.write.Number(1,Regression_AnalysisCurrentDataRowTotal+1,PM10STDEV);
				jxl.write.Number DATA33 = new jxl.write.Number(2,Regression_AnalysisCurrentDataRowTotal+1,PM25STDEV);
				jxl.write.Label DATA34 = new jxl.write.Label(0,0,"DATE");
				jxl.write.Label DATA35 = new jxl.write.Label(1,0,"PM10");
				jxl.write.Label DATA36 = new jxl.write.Label(2,0,"PM2.5");
				//jxl.write.Label DATA37 = new jxl.write.Label(3,0,"DATA_AMOUNT");
				//jxl.write.Number DATA38 = new jxl.write.Number(3,Regression_AnalysisCurrentDataRowTotal+1,DATA_AMOUNT);
				
					
				PM10PM25BookRegressionAnalysisOutputTotal.addCell(DATA1);
				PM10PM25BookRegressionAnalysisOutputTotal.addCell(DATA2);
				PM10PM25BookRegressionAnalysisOutputTotal.addCell(DATA4);
				//PM10PM25BookRegressionAnalysisOutputTotal.addCell(DATA6);
				PM10PM25BookRegressionAnalysisOutputTotal.addCell(DATA8);
				PM10PM25BookRegressionAnalysisOutputTotal.addCell(DATA10);
				
				PM10PM25BookRegressionAnalysisOutputTotal.addCell(DATA3);
				//PM10PM25BookRegressionAnalysisOutputTotal.addCell(DATA5);
				PM10PM25BookRegressionAnalysisOutputTotal.addCell(DATA7);
				PM10PM25BookRegressionAnalysisOutputTotal.addCell(DATA9);
				PM10PM25BookRegressionAnalysisOutputTotal.addCell(DATA11);
				PM10PM25BookRegressionAnalysisOutputTotal.addCell(DATA12);
				
				PM10PM25BookAvgOutputTotal.addCell(DATA21);
				PM10PM25BookAvgOutputTotal.addCell(DATA22);
				PM10PM25BookAvgOutputTotal.addCell(DATA23);
				PM10PM25BookAvgOutputTotal.addCell(DATA24);
				PM10PM25BookAvgOutputTotal.addCell(DATA25);
				PM10PM25BookAvgOutputTotal.addCell(DATA26);
				//PM10PM25BookAvgOutputTotal.addCell(DATA27);
				//PM10PM25BookAvgOutputTotal.addCell(DATA28);
				
				PM10PM25BookStdevOutputTotal.addCell(DATA31);
				PM10PM25BookStdevOutputTotal.addCell(DATA32);
				PM10PM25BookStdevOutputTotal.addCell(DATA33);
				PM10PM25BookStdevOutputTotal.addCell(DATA34);
				PM10PM25BookStdevOutputTotal.addCell(DATA35);
				PM10PM25BookStdevOutputTotal.addCell(DATA36);
				//PM10PM25BookStdevOutputTotal.addCell(DATA37);
				//PM10PM25BookStdevOutputTotal.addCell(DATA38);
				
				Regression_AnalysisCurrentDataRowTotal++;
					
				
				PreviousCL=CL;
			}
		
			
			
			for(int i=0;i<month.length;i++) {//for separated-sheet
				//CL, PreviousCL, DataRow, doubleAvgCellCounter reset to default
				CL=0;
				PreviousCL=0;
				doubleAvgCellCounter=0;
				RegressionAnalysisCurrentDataRowMonth=0;
				//output excel row counter reset to default
				
				boolean ifexist=false;
				String OutputDate12=month[i];
				for(int j=0;j<ContainLocation;j++)	{//check whether the selecting month exist
					if(DATE[j].indexOf(OutputDate12)>=0) {
						ifexist=true;
						break;
					}
				}
				
				if(ifexist)	{
					
					WritableSheet PM10PM25BookRegressionAnalysisOutput = PM10PM25BookRegressionAnalysis.createSheet(OutputDate12,PM10PM25BookRegressionAnalysisSheetCounter);//Monthly y=ax+b,R^2
					WritableSheet PM10PM25BookAvgOutput = PM10PM25BookAvg.createSheet(OutputDate12,PM10PM25BookAvgSheetCounter);//Monthly AVERAGE
					WritableSheet PM10PM25BookStdevOutput = PM10PM25BookStdev.createSheet(OutputDate12,PM10PM25BookStdevSheetCounter);//Monthly AVERAGE
					
					
					//OutMonthFirst,OutYearFirst reset to default
					OutMonthFirst = StringUtils.substringBefore(DATE[0], " ");
					OutYearFirst = DATE[0].substring(DATE[0].length()-4, DATE[0].length());
					
					while(true) {//calculate for the beginning and the ending of the month 
						if(CL>=ContainLocation-1) {
							break;
						}
						
						CL=DATE_CALCULATION(CL, ContainLocation, OutMonthFirst, OutYearFirst, DATE);
						
						
						
						OutMonthFirst = StringUtils.substringBefore(DATE[CL], " ");
						OutYearFirst = DATE[CL].substring(DATE[CL].length()-4, DATE[CL].length());
						if(DATE[PreviousCL].indexOf(OutputDate12)>=0) {
							String OutputDate = DATE[PreviousCL].substring(DATE[PreviousCL].length()-4, DATE[PreviousCL].length());

							double SLOPE=SLOPE(PM10,PM25,DATE,CL,PreviousCL,rounding_off_place);
							double INTERCEPT=INTERCEPT(PM10,PM25,DATE,CL,PreviousCL,rounding_off_place);
							double RSQ=RSQ(PM10,PM25,DATE,CL,PreviousCL,rounding_off_place);
							double PM10AVERAGE=AVERAGE(DoublePM10,CL,PreviousCL,rounding_off_place);
							double PM25AVERAGE=AVERAGE(DoublePM25,CL,PreviousCL,rounding_off_place);
							double PM10STDEV=STDEV(DoublePM10, CL, PreviousCL, rounding_off_place);
							double PM25STDEV=STDEV(DoublePM25, CL, PreviousCL, rounding_off_place);
							
							PM10_AVG_AVG[doubleAvgCellCounter]= PM10AVERAGE;
							PM25_AVG_AVG[doubleAvgCellCounter]= PM25AVERAGE;
							PM10_STDEV_AVG[doubleAvgCellCounter]=PM10STDEV;
							PM25_STDEV_AVG[doubleAvgCellCounter]=PM25STDEV;
							R2[doubleAvgCellCounter]= RSQ;
							a[doubleAvgCellCounter]= SLOPE;
							b[doubleAvgCellCounter]= INTERCEPT;
							
							//int DATA_AMOUNT=CL-PreviousCL;
							//System.out.println(OutputDate+"  "+"y="+SLOPE+"*x+"+INTERCEPT+"   "+"R^2="+RSQ);
							
							//DATA1-20 for Regression_Analysis_MONTH
							jxl.write.Label DATA1 = new jxl.write.Label(0,RegressionAnalysisCurrentDataRowMonth+1,OutputDate);
							jxl.write.Label DATA2 = new jxl.write.Label(1,RegressionAnalysisCurrentDataRowMonth+1,"y="+SLOPE+"*x+"+INTERCEPT);
							jxl.write.Number DATA4 = new jxl.write.Number(4,RegressionAnalysisCurrentDataRowMonth+1,RSQ);
							//jxl.write.Number DATA6 = new jxl.write.Number(7,RegressionAnalysisCurrentDataRowMonth+1,(CL-PreviousCL));
							jxl.write.Number DATA8 = new jxl.write.Number(10,RegressionAnalysisCurrentDataRowMonth+1,SLOPE);
							jxl.write.Number DATA10 = new jxl.write.Number(13,RegressionAnalysisCurrentDataRowMonth+1,INTERCEPT);
							
							jxl.write.Label DATA11 = new jxl.write.Label(0,0,"DATE");
							jxl.write.Label DATA12 = new jxl.write.Label(1,0,"y= a*x+ b");
							jxl.write.Label DATA3 = new jxl.write.Label(4,0,"R^2");
							//jxl.write.Label DATA5 = new jxl.write.Label(7,0,"DATA_AMOUNT");
							jxl.write.Label DATA7 = new jxl.write.Label(10,0,"a");
							jxl.write.Label DATA9 = new jxl.write.Label(13,0,"b");
							
							//DATA21-30 for AVERAGE_MONTH
							jxl.write.Label DATA21 = new jxl.write.Label(0,RegressionAnalysisCurrentDataRowMonth+1,OutputDate);
							jxl.write.Number DATA22 = new jxl.write.Number(1,RegressionAnalysisCurrentDataRowMonth+1,PM10AVERAGE);
							jxl.write.Number DATA23 = new jxl.write.Number(2,RegressionAnalysisCurrentDataRowMonth+1,PM25AVERAGE);
							jxl.write.Label DATA24 = new jxl.write.Label(0,0,"DATE");
							jxl.write.Label DATA25 = new jxl.write.Label(1,0,"PM10");
							jxl.write.Label DATA26 = new jxl.write.Label(2,0,"PM2.5");
							//jxl.write.Label DATA27 = new jxl.write.Label(3,0,"DATA_AMOUNT");
							//jxl.write.Number DATA28 = new jxl.write.Number(3,RegressionAnalysisCurrentDataRowMonth+1,DATA_AMOUNT);
							
							//DATA31-40 for STDEV_MONTH
							jxl.write.Label DATA31 = new jxl.write.Label(0,RegressionAnalysisCurrentDataRowMonth+1,OutputDate);
							jxl.write.Number DATA32 = new jxl.write.Number(1,RegressionAnalysisCurrentDataRowMonth+1,PM10STDEV);
							jxl.write.Number DATA33 = new jxl.write.Number(2,RegressionAnalysisCurrentDataRowMonth+1,PM25STDEV);
							jxl.write.Label DATA34 = new jxl.write.Label(0,0,"DATE");
							jxl.write.Label DATA35 = new jxl.write.Label(1,0,"PM10");
							jxl.write.Label DATA36 = new jxl.write.Label(2,0,"PM2.5");
							//jxl.write.Label DATA37 = new jxl.write.Label(3,0,"DATA_AMOUNT");
							//jxl.write.Number DATA38 = new jxl.write.Number(3,RegressionAnalysisCurrentDataRowMonth+1,DATA_AMOUNT);

							
							PM10PM25BookRegressionAnalysisOutput.addCell(DATA1);
							PM10PM25BookRegressionAnalysisOutput.addCell(DATA2);
							PM10PM25BookRegressionAnalysisOutput.addCell(DATA4);
							//PM10PM25BookRegressionAnalysisOutput.addCell(DATA6);
							PM10PM25BookRegressionAnalysisOutput.addCell(DATA8);
							PM10PM25BookRegressionAnalysisOutput.addCell(DATA10);
							
							PM10PM25BookRegressionAnalysisOutput.addCell(DATA3);
							//PM10PM25BookRegressionAnalysisOutput.addCell(DATA5);
							PM10PM25BookRegressionAnalysisOutput.addCell(DATA7);
							PM10PM25BookRegressionAnalysisOutput.addCell(DATA9);
							PM10PM25BookRegressionAnalysisOutput.addCell(DATA11);
							PM10PM25BookRegressionAnalysisOutput.addCell(DATA12);
							
							PM10PM25BookAvgOutput.addCell(DATA21);
							PM10PM25BookAvgOutput.addCell(DATA22);
							PM10PM25BookAvgOutput.addCell(DATA23);
							PM10PM25BookAvgOutput.addCell(DATA24);
							PM10PM25BookAvgOutput.addCell(DATA25);
							PM10PM25BookAvgOutput.addCell(DATA26);
							//PM10PM25BookAvgOutput.addCell(DATA27);
							//PM10PM25BookAvgOutput.addCell(DATA28);
							
							PM10PM25BookStdevOutput.addCell(DATA31);
							PM10PM25BookStdevOutput.addCell(DATA32);
							PM10PM25BookStdevOutput.addCell(DATA33);
							PM10PM25BookStdevOutput.addCell(DATA34);
							PM10PM25BookStdevOutput.addCell(DATA35);
							PM10PM25BookStdevOutput.addCell(DATA36);
							//PM10PM25BookStdevOutput.addCell(DATA37);
							//PM10PM25BookStdevOutput.addCell(DATA38);
			
							RegressionAnalysisCurrentDataRowMonth++;
							doubleAvgCellCounter++;
						}
						PreviousCL=CL;
					}
					
					do//write AVG after AVG and AVG after STDEV, usefulness unknown
					{
						DoubleCalculation(PM10_AVG_AVG, PM10_STDEV_AVG, doubleAvgCellCounter, 1, PM10PM25BookAvgOutput, PM10PM25BookStdevOutput,  rounding_off_place);
						DoubleCalculation(PM25_AVG_AVG, PM25_STDEV_AVG, doubleAvgCellCounter, 2, PM10PM25BookAvgOutput, PM10PM25BookStdevOutput,  rounding_off_place);
						DoubleCalculation_Regression_Analysis(R2,a,b,4,10,13,PM10PM25BookRegressionAnalysisOutput,doubleAvgCellCounter,rounding_off_place);
						
						
						
						for(int j=0;j<realData.size();j++) {
							List<Compare> month1 = (List<Compare>) realData.get(j);
							for(int k=0;k<month1.size();k++) {
								Compare com = month1.get(k);
								if(com.getMonth().indexOf(OutputDate12)>=0) {
									com.setPM10(double_AmountReduction(PM10_AVG_AVG, doubleAvgCellCounter));
									com.setPM25(double_AmountReduction(PM25_AVG_AVG,doubleAvgCellCounter));
									com.setA(double_AmountReduction(a,doubleAvgCellCounter));
									com.setB(double_AmountReduction(b,doubleAvgCellCounter));
									com.setR2(double_AmountReduction(R2,doubleAvgCellCounter));
								}
								
							}
						}
						
						jxl.write.Label label = new jxl.write.Label(0,19,"AVERAGE");
						jxl.write.Label label_1 = new jxl.write.Label(0,20,"STDEV");
						jxl.write.Label label_2 = new jxl.write.Label(0,21,"AVERAGE above 2 STDEVs");
						jxl.write.Label label_3 = new jxl.write.Label(0,22,"AVERAGE above 1 STDEV");
						jxl.write.Label label_4 = new jxl.write.Label(0,23,"AVERAGE below 1 STDEV");
						jxl.write.Label label_5 = new jxl.write.Label(0,24,"AVERAGE below 2 STDEVs");
						
						jxl.write.Label label_6 = new jxl.write.Label(0,19,"AVERAGE");
						jxl.write.Label label_7 = new jxl.write.Label(0,20,"STDEV");
						jxl.write.Label label_8 = new jxl.write.Label(0,21,"AVERAGE above 2 STDEVs");
						jxl.write.Label label_9 = new jxl.write.Label(0,22,"AVERAGE above 1 STDEV");
						jxl.write.Label label_10 = new jxl.write.Label(0,23,"AVERAGE below 1 STDEV");
						jxl.write.Label label_11 = new jxl.write.Label(0,24,"AVERAGE below 2 STDEVs");
						
						PM10PM25BookAvgOutput.addCell(label);
						PM10PM25BookAvgOutput.addCell(label_1);
						PM10PM25BookAvgOutput.addCell(label_2);
						PM10PM25BookAvgOutput.addCell(label_3);
						PM10PM25BookAvgOutput.addCell(label_4);
						PM10PM25BookAvgOutput.addCell(label_5);
						
						PM10PM25BookStdevOutput.addCell(label_6);
						PM10PM25BookStdevOutput.addCell(label_7);
						PM10PM25BookStdevOutput.addCell(label_8);
						PM10PM25BookStdevOutput.addCell(label_9);
						PM10PM25BookStdevOutput.addCell(label_10);
						PM10PM25BookStdevOutput.addCell(label_11);
					
					}while(false);
					
					
					PM10PM25BookRegressionAnalysisSheetCounter++;
					PM10PM25BookAvgSheetCounter++;
					PM10PM25BookStdevSheetCounter++;
					
				}
				
				
				
			}
			
			
			int PM10PM25OutputAllCurrentRow = 1;
			doubleAvgCellCounter=0;
			//calculate data of all the each month in entire input file 
			for(int i=0;i<month.length;i++)
			{
				//OtherdataOutputAllCurrentColumn reset to default
				boolean ifexist=false;
				String OutputDate120= month[i];
				
				
				for(int j=0;j<ContainLocation;j++)//check whether the selected month exist
				{
					if(DATE[j].indexOf(OutputDate120)>=0)
					{
						ifexist=true;
						break;
					}
				}
				
				if(ifexist)
				{
					jxl.write.Label label = new jxl.write.Label(0,PM10PM25OutputAllCurrentRow,OutputDate120);
					jxl.write.Label label_1 = new jxl.write.Label(0,0,"DATE");
					jxl.write.Label label_2 = new jxl.write.Label(0,PM10PM25OutputAllCurrentRow,OutputDate120);
					jxl.write.Label label_3 = new jxl.write.Label(0,0,"DATE");
					PM10PM25BookAllOutputAvg.addCell(label);
					PM10PM25BookAllOutputAvg.addCell(label_1);
					
					PM10PM25BookAllOutputStdev.addCell(label_2);
					PM10PM25BookAllOutputStdev.addCell(label_3);
				
				
					double PM10AVG=ALL_AVERAGE(DoublePM10,DATE,OutputDate120,rounding_off_place, ContainLocation);
					double PM10STDEV=ALL_STDEV(DoublePM10,DATE,OutputDate120,rounding_off_place, ContainLocation);
					jxl.write.Number pm10 = new jxl.write.Number(1,PM10PM25OutputAllCurrentRow,PM10AVG);
					jxl.write.Label pm10_1 = new jxl.write.Label(1,0,"PM 10");
					jxl.write.Number pm10_2 = new jxl.write.Number(1,PM10PM25OutputAllCurrentRow,PM10STDEV);
					jxl.write.Label pm10_3 = new jxl.write.Label(1,0,"PM 10");
				
					PM10PM25BookAllOutputAvg.addCell(pm10);
					PM10PM25BookAllOutputAvg.addCell(pm10_1);
					
					PM10PM25BookAllOutputStdev.addCell(pm10_2);
					PM10PM25BookAllOutputStdev.addCell(pm10_3);
				
					PM10_AVG_AVG[doubleAvgCellCounter]= PM10AVG;
					PM10_STDEV_AVG[doubleAvgCellCounter]=PM10STDEV;
					
					
					double PM25AVG=ALL_AVERAGE(DoublePM25,DATE,OutputDate120,rounding_off_place, ContainLocation);
					double PM25STDEV=ALL_STDEV(DoublePM25,DATE,OutputDate120,rounding_off_place, ContainLocation);
					jxl.write.Number pm25 = new jxl.write.Number(2,PM10PM25OutputAllCurrentRow,PM25AVG);
					jxl.write.Label pm25_1 = new jxl.write.Label(2,0,"PM 2.5");
					jxl.write.Number pm25_2 = new jxl.write.Number(2,PM10PM25OutputAllCurrentRow,PM25STDEV);
					jxl.write.Label pm25_3 = new jxl.write.Label(2,0,"PM 2.5");
					
					PM25_AVG_AVG[doubleAvgCellCounter]= PM25AVG;
					PM25_STDEV_AVG[doubleAvgCellCounter]=PM25STDEV;
					
					PM10PM25BookAllOutputAvg.addCell(pm25);
					PM10PM25BookAllOutputAvg.addCell(pm25_1);
					
					PM10PM25BookAllOutputStdev.addCell(pm25_2);
					PM10PM25BookAllOutputStdev.addCell(pm25_3);
				
					
					/*
					int DATA_AMOUNT=DATA_AMOUNT(DATE, OutputDate120, ContainLocation);
					jxl.write.Number data_amount = new jxl.write.Number(3,PM10PM25OutputAllCurrentRow,DATA_AMOUNT);
					jxl.write.Label data_amount_1 = new jxl.write.Label(3,0,"DATA_AMOUNT");
					jxl.write.Number data_amount_2 = new jxl.write.Number(3,PM10PM25OutputAllCurrentRow,DATA_AMOUNT);
					jxl.write.Label data_amount_3 = new jxl.write.Label(3,0,"DATA_AMOUNT");
					
					PM10PM25BookAllOutputAvg.addCell(data_amount);
					PM10PM25BookAllOutputAvg.addCell(data_amount_1);
					
					PM10PM25BookAllOutputStdev.addCell(data_amount_2);
					PM10PM25BookAllOutputStdev.addCell(data_amount_3);
				
					*/
				
					PM10PM25OutputAllCurrentRow++;
					doubleAvgCellCounter++;
					
				}
				
				do//write AVG after AVG and AVG after STDEV, usefulness unknown
				{
					DoubleCalculation(PM10_AVG_AVG, PM10_STDEV_AVG, doubleAvgCellCounter, 1, PM10PM25BookAllOutputAvg, PM10PM25BookAllOutputStdev, rounding_off_place);
					DoubleCalculation(PM25_AVG_AVG, PM25_STDEV_AVG, doubleAvgCellCounter, 2, PM10PM25BookAllOutputAvg, PM10PM25BookAllOutputStdev, rounding_off_place);
					
					jxl.write.Label label = new jxl.write.Label(0,19,"AVERAGE");
					jxl.write.Label label_1 = new jxl.write.Label(0,20,"STDEV");
					jxl.write.Label label_2 = new jxl.write.Label(0,21,"AVERAGE above 2 STDEVs");
					jxl.write.Label label_3 = new jxl.write.Label(0,22,"AVERAGE above  STDEV");
					jxl.write.Label label_4 = new jxl.write.Label(0,23,"AVERAGE below 1 STDEV");
					jxl.write.Label label_5 = new jxl.write.Label(0,24,"AVERAGE below 2 STDEVs");
					
					jxl.write.Label label_6 = new jxl.write.Label(0,19,"AVERAGE");
					jxl.write.Label label_7 = new jxl.write.Label(0,20,"STDEV");
					jxl.write.Label label_8 = new jxl.write.Label(0,21,"AVERAGE above 2 STDEVs");
					jxl.write.Label label_9 = new jxl.write.Label(0,22,"AVERAGE above 1 STDEV");
					jxl.write.Label label_10 = new jxl.write.Label(0,23,"AVERAGE below 1 STDEV");
					jxl.write.Label label_11 = new jxl.write.Label(0,24,"AVERAGE below 2 STDEVs");
					
					PM10PM25BookAllOutputAvg.addCell(label);
					PM10PM25BookAllOutputAvg.addCell(label_1);
					PM10PM25BookAllOutputAvg.addCell(label_2);
					PM10PM25BookAllOutputAvg.addCell(label_3);
					PM10PM25BookAllOutputAvg.addCell(label_4);
					PM10PM25BookAllOutputAvg.addCell(label_5);
					
					PM10PM25BookAllOutputStdev.addCell(label_6);
					PM10PM25BookAllOutputStdev.addCell(label_7);
					PM10PM25BookAllOutputStdev.addCell(label_8);
					PM10PM25BookAllOutputStdev.addCell(label_9);
					PM10PM25BookAllOutputStdev.addCell(label_10);
					PM10PM25BookAllOutputStdev.addCell(label_11);
					
				}while(false);
				
			}
			
			
			//calculate average ,standard_deviation for all data
			/*
			 * It wont completely equal the calculation it done front.
			 * Because there were done with rounding off several time, might occur with deviation 
			 */
			/*
			double PM10AVG_ALL=AVERAGE(DoublePM10,ContainLocation,0,rounding_off_place);
			double PM25AVG_ALL=AVERAGE(DoublePM25,ContainLocation,0,rounding_off_place);
			double PM10STDEV_ALL=STDEV(DoublePM10,ContainLocation,0,rounding_off_place);
			double PM25STDEV_ALL=STDEV(DoublePM25,ContainLocation,0,rounding_off_place);
			
			jxl.write.Label label_avg_all = new jxl.write.Label(0,PM10PM25OutputAllCurrentRow+1,"ALL_AVG");
			jxl.write.Label label_stdev_all = new jxl.write.Label(0,PM10PM25OutputAllCurrentRow+1,"ALL_STDEV");
			jxl.write.Number pm10_all = new jxl.write.Number(1,PM10PM25OutputAllCurrentRow+1,PM10AVG_ALL);
			jxl.write.Number pm10_2_all = new jxl.write.Number(1,PM10PM25OutputAllCurrentRow+1,PM10STDEV_ALL);
			jxl.write.Number pm25_all = new jxl.write.Number(2,PM10PM25OutputAllCurrentRow+1,PM25AVG_ALL);
			jxl.write.Number pm25_2_all = new jxl.write.Number(2,PM10PM25OutputAllCurrentRow+1,PM25STDEV_ALL);
			//jxl.write.Number data_amount_all = new jxl.write.Number(3,PM10PM25OutputAllCurrentRow+1,ContainLocation);
			//jxl.write.Number data_amount_1_all = new jxl.write.Number(3,PM10PM25OutputAllCurrentRow+1,ContainLocation);
			
			PM10PM25BookAllOutputAvg.addCell(label_avg_all);
			PM10PM25BookAllOutputAvg.addCell(pm10_all);
			PM10PM25BookAllOutputAvg.addCell(pm25_all);
			//PM10PM25BookAllOutputAvg.addCell(data_amount_all);
			
			PM10PM25BookAllOutputStdev.addCell(label_stdev_all);
			PM10PM25BookAllOutputStdev.addCell(pm10_2_all);
			PM10PM25BookAllOutputStdev.addCell(pm25_2_all);
			//PM10PM25BookAllOutputStdev.addCell(data_amount_1_all);
			*/
			
			//write into excel
			PM10PM25BookRegressionAnalysis.write();
			PM10PM25BookRegressionAnalysis.close();
			
			PM10PM25BookAvg.write();
			PM10PM25BookAvg.close();

			PM10PM25BookStdev.write();
			PM10PM25BookStdev.close();
			
			PM10PM25BookAll.write();
			PM10PM25BookAll.close();
			System.gc();
			
			ModifyExcel(PM10PM25Route);
			readModifiedExcel_PM10PM25(PM10PM25Route,dataList);
			
		}
		catch (Exception e)
		{
			System.out.println(fileNameTxt+" PM10PM25Output "+e);
		}
		
	
	
		
	}

	public void OtherdataOutput(String[] DATE, double[] SO2, double[] CO, double[] CO2, double[] O3, double[] NOx, double[] NO, double[] NO2, double[] THC, double[] NMHC, double[] CH4, double[] WIND_SPEED, double[] WS_HR, double[] AMB_TEMP, double[] RAIN_INT, double[] PH_RAIN, double[] RH, double[] RAIN_COND,  String newDataRoute, String fileNameTxt, String filename,int ContainLocation, int rounding_off_place,
			int numSO2, int numCO, int numCO2, int numO3, int numNOx, int numNO, int numNO2, int numTHC, int numNMHC, int numCH4, int numWIND_SPEED, int numWS_HR, int numAMB_TEMP, int numRAIN_INT, int numPH_RAIN, int numRH, int numRAIN_COND, List dataList ,List realData) {
		try	{	
			//make new folder
			String OtherdataRoute=newDataRoute+"/"+"Otherdata_"+fileNameTxt;
			File dir_file = new File(OtherdataRoute);
			dir_file.mkdir();
			
			//create excel 
			WritableWorkbook OtherdataBookAll = Workbook.createWorkbook(new File(OtherdataRoute + "/"+ "All_" + filename +"_" +fileNameTxt+ ".xls"));
			
			WritableSheet OtherdataoutputAllAvg = OtherdataBookAll.createSheet("AVG",0);
			WritableSheet OtherdataoutputAllStdev = OtherdataBookAll.createSheet("STDEV",1);
			
			
			//check whether still in a month
			
			
			String OutMonthFirst;
			String OutYearFirst;
			
			OutMonthFirst = StringUtils.substringBefore(DATE[0], " ");
			OutYearFirst = DATE[0].substring(DATE[0].length()-4, DATE[0].length());
			
			int CL=0;
			int PreviousCL=0;
			String month[]= {"December", "November", "October", "September", "August", "July", "June", "May", "April", "March", "February", "January"};
			
			//create excel
			WritableWorkbook OtherDataBookMonthAvg = Workbook.createWorkbook(new File(OtherdataRoute + "/"+"AVG"+"_" +fileNameTxt+ ".xls"));
			WritableWorkbook OtherDataBookMonthStdev = Workbook.createWorkbook(new File(OtherdataRoute + "/"+"STDEV"+"_" +fileNameTxt+ ".xls"));
			int OtherdataBookMonthSheetCounter = 1;
			
			int CurrentRow=0;
			WritableSheet OtherDataOutputMonthAvgTotal = OtherDataBookMonthAvg.createSheet("Total",0);
			WritableSheet OtherDataOutputMonthStdevTotal = OtherDataBookMonthStdev.createSheet("Total",0);
			
			//calculate data of each month for Total-sheet
			while(true)	{
				if(CL>=ContainLocation-1) {
					break;
				}
				
				CL=DATE_CALCULATION(CL, ContainLocation, OutMonthFirst, OutYearFirst, DATE);
				
				OutMonthFirst = StringUtils.substringBefore(DATE[CL], " ");
				OutYearFirst = DATE[CL].substring(DATE[CL].length()-4, DATE[CL].length());
				
				String OutputDate =StringUtils.substringBefore(DATE[PreviousCL], " ")+" ,"+ DATE[PreviousCL].substring(DATE[PreviousCL].length()-4, DATE[PreviousCL].length());
				int CurrentColumn=0;
				
				if(true) {//DATE
					jxl.write.Label label = new jxl.write.Label(CurrentColumn,CurrentRow,OutputDate);
					jxl.write.Label label_1 = new jxl.write.Label(CurrentColumn,0,"DATE");
					jxl.write.Label label_2 = new jxl.write.Label(CurrentColumn,CurrentRow,OutputDate);
					jxl.write.Label label_3 = new jxl.write.Label(CurrentColumn,0,"DATE");
					CurrentColumn++;
					OtherDataOutputMonthAvgTotal.addCell(label);
					OtherDataOutputMonthAvgTotal.addCell(label_1);
					
					OtherDataOutputMonthStdevTotal.addCell(label_2);
					OtherDataOutputMonthStdevTotal.addCell(label_3);
				}					
				if(numSO2==1) {
					String name = "SO2";
					OtherdataOutput_Total_sheet(SO2,OtherDataOutputMonthAvgTotal, OtherDataOutputMonthStdevTotal,CL,PreviousCL,rounding_off_place,name,CurrentColumn,CurrentRow);
					CurrentColumn++;
				}
				if(numCO==1) {
					String name = "CO";
					OtherdataOutput_Total_sheet(CO,OtherDataOutputMonthAvgTotal, OtherDataOutputMonthStdevTotal,CL,PreviousCL,rounding_off_place,name,CurrentColumn,CurrentRow);
					CurrentColumn++;
				}
				if(numCO2==1) {
					String name = "CO2";
					OtherdataOutput_Total_sheet(CO2,OtherDataOutputMonthAvgTotal, OtherDataOutputMonthStdevTotal,CL,PreviousCL,rounding_off_place,name,CurrentColumn,CurrentRow);
					CurrentColumn++;
				}
				if(numO3==1) {
					String name = "O3";
					OtherdataOutput_Total_sheet(O3,OtherDataOutputMonthAvgTotal, OtherDataOutputMonthStdevTotal,CL,PreviousCL,rounding_off_place,name,CurrentColumn,CurrentRow);
					CurrentColumn++;
				}
				if(numNOx==1) {
					String name = "NOx";
					OtherdataOutput_Total_sheet(NOx,OtherDataOutputMonthAvgTotal, OtherDataOutputMonthStdevTotal,CL,PreviousCL,rounding_off_place,name,CurrentColumn,CurrentRow);
					CurrentColumn++;
				}
				if(numNO==1) {
					String name = "NO";
					OtherdataOutput_Total_sheet(NO,OtherDataOutputMonthAvgTotal, OtherDataOutputMonthStdevTotal,CL,PreviousCL,rounding_off_place,name,CurrentColumn,CurrentRow);
					CurrentColumn++;
				}
				if(numNO2==1) {
					String name = "NO2";
					OtherdataOutput_Total_sheet(NO2,OtherDataOutputMonthAvgTotal, OtherDataOutputMonthStdevTotal,CL,PreviousCL,rounding_off_place,name,CurrentColumn,CurrentRow);
					CurrentColumn++;
				}
				if(numTHC==1) {
					String name = "THC";
					OtherdataOutput_Total_sheet(THC,OtherDataOutputMonthAvgTotal, OtherDataOutputMonthStdevTotal,CL,PreviousCL,rounding_off_place,name,CurrentColumn,CurrentRow);
					CurrentColumn++;
				}
				if(numNMHC==1) {
					String name = "NMHC";
					OtherdataOutput_Total_sheet(NMHC,OtherDataOutputMonthAvgTotal, OtherDataOutputMonthStdevTotal,CL,PreviousCL,rounding_off_place,name,CurrentColumn,CurrentRow);
					CurrentColumn++;
				}
				if(numCH4==1) {
					String name = "CH4";
					OtherdataOutput_Total_sheet(CH4,OtherDataOutputMonthAvgTotal, OtherDataOutputMonthStdevTotal,CL,PreviousCL,rounding_off_place,name,CurrentColumn,CurrentRow);
					CurrentColumn++;
				}
				if(numWIND_SPEED==1) {
					String name = "WIND_SPEED";
					OtherdataOutput_Total_sheet(WIND_SPEED,OtherDataOutputMonthAvgTotal, OtherDataOutputMonthStdevTotal,CL,PreviousCL,rounding_off_place,name,CurrentColumn,CurrentRow);
					CurrentColumn++;
				}
				if(numWS_HR==1) {
					String name = "WS_HR";
					OtherdataOutput_Total_sheet(WS_HR,OtherDataOutputMonthAvgTotal, OtherDataOutputMonthStdevTotal,CL,PreviousCL,rounding_off_place,name,CurrentColumn,CurrentRow);
					CurrentColumn++;
				}					
				if(numAMB_TEMP==1) {
					String name = "AMB_TEMP";
					OtherdataOutput_Total_sheet(AMB_TEMP,OtherDataOutputMonthAvgTotal, OtherDataOutputMonthStdevTotal,CL,PreviousCL,rounding_off_place,name,CurrentColumn,CurrentRow);
					CurrentColumn++;
				}
				if(numRAIN_INT==1) {
					String name = "RAIN_INT";
					OtherdataOutput_Total_sheet(RAIN_INT,OtherDataOutputMonthAvgTotal, OtherDataOutputMonthStdevTotal,CL,PreviousCL,rounding_off_place,name,CurrentColumn,CurrentRow);
					CurrentColumn++;
				}
				if(numPH_RAIN==1) {
					String name = "PH_RAIN";
					OtherdataOutput_Total_sheet(PH_RAIN,OtherDataOutputMonthAvgTotal, OtherDataOutputMonthStdevTotal,CL,PreviousCL,rounding_off_place,name,CurrentColumn,CurrentRow);
					CurrentColumn++;
				}
				if(numRH==1) {
					String name = "RH";
					OtherdataOutput_Total_sheet(RH,OtherDataOutputMonthAvgTotal, OtherDataOutputMonthStdevTotal,CL,PreviousCL,rounding_off_place,name,CurrentColumn,CurrentRow);
					CurrentColumn++;
				}
				if(numRAIN_COND==1) {
					String name = "RAIN_COND";
					OtherdataOutput_Total_sheet(RAIN_COND,OtherDataOutputMonthAvgTotal, OtherDataOutputMonthStdevTotal,CL,PreviousCL,rounding_off_place,name,CurrentColumn,CurrentRow);
					CurrentColumn++;
				}
				/*if(true)//calculate data amount in the month
				{
					int DATA_AMOUNT=CL-PreviousCL;
					jxl.write.Number data_amount = new jxl.write.Number(CurrentColumn,CurrentRow,DATA_AMOUNT);
					jxl.write.Label data_amount_1 = new jxl.write.Label(CurrentColumn,0,"DATA_AMOUNT");
					jxl.write.Number data_amount_2 = new jxl.write.Number(CurrentColumn,CurrentRow,DATA_AMOUNT);
					jxl.write.Label data_amount_3 = new jxl.write.Label(CurrentColumn,0,"DATA_AMOUNT");
					
					CurrentColumn++;
					OtherDataOutputMonthAvgTotal.addCell(data_amount);
					OtherDataOutputMonthAvgTotal.addCell(data_amount_1);
					
					OtherDataOutputMonthStdevTotal.addCell(data_amount_2);
					OtherDataOutputMonthStdevTotal.addCell(data_amount_3);
				}*/
				CurrentRow++;
				PreviousCL=CL;
			}
			//total-sheet end
			
			
			
			
			//store further data from calculated data
			int cellAmouunt=20;
			int doubleAvgCellCounter=0;
			
			double SO2_AVG_AVG[]={0};
			double CO_AVG_AVG[]={0};
			double CO2_AVG_AVG[]={0};
			double O3_AVG_AVG[]={0};
			double NOx_AVG_AVG[]={0};
			double NO_AVG_AVG[]={0};
			double NO2_AVG_AVG[]={0};
			double THC_AVG_AVG[]={0};
			double NMHC_AVG_AVG[]={0};
			double CH4_AVG_AVG[]={0};
			double WIND_SPEED_AVG_AVG[]={0};
			double WS_HR_AVG_AVG[]={0};
			double AMB_TEMP_AVG_AVG[]={0};
			double RAIN_INT_AVG_AVG[]={0};
			double PH_RAIN_AVG_AVG[]={0};
			double RH_AVG_AVG[]={0};
			double RAIN_COND_AVG_AVG[]={0};
			
			
			SO2_AVG_AVG=new double[cellAmouunt];
			CO_AVG_AVG=new double[cellAmouunt];
			CO2_AVG_AVG=new double[cellAmouunt];
			O3_AVG_AVG=new double[cellAmouunt];
			NOx_AVG_AVG=new double[cellAmouunt];
			NO_AVG_AVG=new double[cellAmouunt];
			NO2_AVG_AVG=new double[cellAmouunt];
			THC_AVG_AVG=new double[cellAmouunt];
			NMHC_AVG_AVG=new double[cellAmouunt];
			CH4_AVG_AVG=new double[cellAmouunt];
			WIND_SPEED_AVG_AVG=new double[cellAmouunt];
			WS_HR_AVG_AVG=new double[cellAmouunt];
			AMB_TEMP_AVG_AVG=new double[cellAmouunt];
			RAIN_INT_AVG_AVG=new double[cellAmouunt];
			PH_RAIN_AVG_AVG=new double[cellAmouunt];
			RH_AVG_AVG=new double[cellAmouunt];
			RAIN_COND_AVG_AVG=new double[cellAmouunt];
			
			double SO2_STDEV_AVG[]={0};
			double CO_STDEV_AVG[]={0};
			double CO2_STDEV_AVG[]={0};
			double O3_STDEV_AVG[]={0};
			double NOx_STDEV_AVG[]={0};
			double NO_STDEV_AVG[]={0};
			double NO2_STDEV_AVG[]={0};
			double THC_STDEV_AVG[]={0};
			double NMHC_STDEV_AVG[]={0};
			double CH4_STDEV_AVG[]={0};
			double WIND_SPEED_STDEV_AVG[]={0};
			double WS_HR_STDEV_AVG[]={0};
			double AMB_TEMP_STDEV_AVG[]={0};
			double RAIN_INT_STDEV_AVG[]={0};
			double PH_RAIN_STDEV_AVG[]={0};
			double RH_STDEV_AVG[]={0};
			double RAIN_COND_STDEV_AVG[]={0};
			
			
			SO2_STDEV_AVG=new double[cellAmouunt];
			CO_STDEV_AVG=new double[cellAmouunt];
			CO2_STDEV_AVG=new double[cellAmouunt];
			O3_STDEV_AVG=new double[cellAmouunt];
			NOx_STDEV_AVG=new double[cellAmouunt];
			NO_STDEV_AVG=new double[cellAmouunt];
			NO2_STDEV_AVG=new double[cellAmouunt];
			THC_STDEV_AVG=new double[cellAmouunt];
			NMHC_STDEV_AVG=new double[cellAmouunt];
			CH4_STDEV_AVG=new double[cellAmouunt];
			WIND_SPEED_STDEV_AVG=new double[cellAmouunt];
			WS_HR_STDEV_AVG=new double[cellAmouunt];
			AMB_TEMP_STDEV_AVG=new double[cellAmouunt];
			RAIN_INT_STDEV_AVG=new double[cellAmouunt];
			PH_RAIN_STDEV_AVG=new double[cellAmouunt];
			RH_STDEV_AVG=new double[cellAmouunt];
			RAIN_COND_STDEV_AVG=new double[cellAmouunt];
			
			
			//calculate data of each month	for separated-sheet			
			for(int i=0;i<month.length;i++)	{
				//CL,PreviousCL,doubleAvgCellCounter reset to default
				CL=0;
				PreviousCL=0;
				doubleAvgCellCounter=0;
				
				//output excel row counter reset to default
				CurrentRow=1;
				boolean ifexist=false;
				String OutputDate2=month[i];
				for(int j=0;j<ContainLocation;j++) {//check whether the selected month does exist
					if(DATE[j].indexOf(OutputDate2)>=0)	{
						ifexist=true;
						break;
					}
				}
				
				if(ifexist)	{					
					WritableSheet OtherDataOutputMonthAvg = OtherDataBookMonthAvg.createSheet(month[i],OtherdataBookMonthSheetCounter);
					WritableSheet OtherDataOutputMonthStdev = OtherDataBookMonthStdev.createSheet(month[i],OtherdataBookMonthSheetCounter);
					
					OutMonthFirst = StringUtils.substringBefore(DATE[0], " ");
					OutYearFirst = DATE[0].substring(DATE[0].length()-4, DATE[0].length());
					
					CL=0;//CL reset to default
					PreviousCL=0;//PreviousCK reset to default
					
					//OutMonthFirst,OutYearFirst reset to default
					OutMonthFirst = StringUtils.substringBefore(DATE[0], " ");
					OutYearFirst = DATE[0].substring(DATE[0].length()-4, DATE[0].length());
					
					while(true)	{
						if(CL>=ContainLocation-1) {
							break;
						}
						
						CL=DATE_CALCULATION(CL, ContainLocation, OutMonthFirst, OutYearFirst, DATE);
						
						OutMonthFirst = StringUtils.substringBefore(DATE[CL], " ");
						OutYearFirst = DATE[CL].substring(DATE[CL].length()-4, DATE[CL].length());
						String OutputDate = DATE[PreviousCL].substring(DATE[PreviousCL].length()-4, DATE[PreviousCL].length());
						int CurrentColumn=0;
						if(DATE[PreviousCL].indexOf(OutputDate2)>=0) {
							if(true) {//DATE
								jxl.write.Label label = new jxl.write.Label(CurrentColumn,CurrentRow,OutputDate);
								jxl.write.Label label_1 = new jxl.write.Label(CurrentColumn,0,"DATE");
								jxl.write.Label label_2 = new jxl.write.Label(CurrentColumn,CurrentRow,OutputDate);
								jxl.write.Label label_3 = new jxl.write.Label(CurrentColumn,0,"DATE");
								
								CurrentColumn++;
								OtherDataOutputMonthAvg.addCell(label);
								OtherDataOutputMonthAvg.addCell(label_1);
								
								OtherDataOutputMonthStdev.addCell(label_2);
								OtherDataOutputMonthStdev.addCell(label_3);
							}					
							if(numSO2==1) {	
								String name="SO2";
								OtherdataOutput_Separated_sheet(SO2,OtherDataOutputMonthAvg, OtherDataOutputMonthStdev,CL,PreviousCL,rounding_off_place,name,CurrentColumn,CurrentRow);
								SO2_AVG_AVG[doubleAvgCellCounter]=AVERAGE(SO2, CL, PreviousCL, rounding_off_place);
								SO2_STDEV_AVG[doubleAvgCellCounter]=STDEV(SO2, CL, PreviousCL, rounding_off_place);
								CurrentColumn++;
							}
							if(numCO==1) {
								String name="CO";
								OtherdataOutput_Separated_sheet(CO,OtherDataOutputMonthAvg, OtherDataOutputMonthStdev,CL,PreviousCL,rounding_off_place,name,CurrentColumn,CurrentRow);
								CO_AVG_AVG[doubleAvgCellCounter]=AVERAGE(CO, CL, PreviousCL, rounding_off_place);
								CO_STDEV_AVG[doubleAvgCellCounter]=STDEV(CO, CL, PreviousCL, rounding_off_place);
								CurrentColumn++;
							}
							if(numCO2==1) {
								String name="CO2";
								OtherdataOutput_Separated_sheet(CO2,OtherDataOutputMonthAvg, OtherDataOutputMonthStdev,CL,PreviousCL,rounding_off_place,name,CurrentColumn,CurrentRow);
								CO2_AVG_AVG[doubleAvgCellCounter]=AVERAGE(CO2, CL, PreviousCL, rounding_off_place);
								CO2_STDEV_AVG[doubleAvgCellCounter]=STDEV(CO2, CL, PreviousCL, rounding_off_place);
								CurrentColumn++;
							}
							if(numO3==1) {
								String name="O3";
								OtherdataOutput_Separated_sheet(O3,OtherDataOutputMonthAvg, OtherDataOutputMonthStdev,CL,PreviousCL,rounding_off_place,name,CurrentColumn,CurrentRow);
								O3_AVG_AVG[doubleAvgCellCounter]=AVERAGE(O3, CL, PreviousCL, rounding_off_place);
								O3_STDEV_AVG[doubleAvgCellCounter]=STDEV(O3, CL, PreviousCL, rounding_off_place);
								CurrentColumn++;
							}
							if(numNOx==1) {
								String name="NOx";
								OtherdataOutput_Separated_sheet(NOx,OtherDataOutputMonthAvg, OtherDataOutputMonthStdev,CL,PreviousCL,rounding_off_place,name,CurrentColumn,CurrentRow);
								NOx_AVG_AVG[doubleAvgCellCounter]=AVERAGE(NOx, CL, PreviousCL, rounding_off_place);
								NOx_STDEV_AVG[doubleAvgCellCounter]=STDEV(NOx, CL, PreviousCL, rounding_off_place);
								CurrentColumn++;
							}
							if(numNO==1) {
								String name="NO";
								OtherdataOutput_Separated_sheet(NO,OtherDataOutputMonthAvg, OtherDataOutputMonthStdev,CL,PreviousCL,rounding_off_place,name,CurrentColumn,CurrentRow);
								NO_AVG_AVG[doubleAvgCellCounter]=AVERAGE(NO, CL, PreviousCL, rounding_off_place);
								NO_STDEV_AVG[doubleAvgCellCounter]=STDEV(NO, CL, PreviousCL, rounding_off_place);
								CurrentColumn++;
							}
							if(numNO2==1) {
								String name="NO2";
								OtherdataOutput_Separated_sheet(NO2,OtherDataOutputMonthAvg, OtherDataOutputMonthStdev,CL,PreviousCL,rounding_off_place,name,CurrentColumn,CurrentRow);
								NO2_AVG_AVG[doubleAvgCellCounter]=AVERAGE(NO2, CL, PreviousCL, rounding_off_place);
								NO2_STDEV_AVG[doubleAvgCellCounter]=STDEV(NO2, CL, PreviousCL, rounding_off_place);
								CurrentColumn++;
							}
							if(numTHC==1) {
								String name="THC";
								OtherdataOutput_Separated_sheet(THC,OtherDataOutputMonthAvg, OtherDataOutputMonthStdev,CL,PreviousCL,rounding_off_place,name,CurrentColumn,CurrentRow);
								THC_AVG_AVG[doubleAvgCellCounter]=AVERAGE(THC, CL, PreviousCL, rounding_off_place);
								THC_STDEV_AVG[doubleAvgCellCounter]=STDEV(THC, CL, PreviousCL, rounding_off_place);
								CurrentColumn++;
							}
							if(numNMHC==1) {
								String name="NMHC";
								OtherdataOutput_Separated_sheet(NMHC,OtherDataOutputMonthAvg, OtherDataOutputMonthStdev,CL,PreviousCL,rounding_off_place,name,CurrentColumn,CurrentRow);
								NMHC_AVG_AVG[doubleAvgCellCounter]=AVERAGE(NMHC, CL, PreviousCL, rounding_off_place);
								NMHC_STDEV_AVG[doubleAvgCellCounter]=STDEV(NMHC, CL, PreviousCL, rounding_off_place);
								CurrentColumn++;
							}
							if(numCH4==1) {
								String name="CH4";
								OtherdataOutput_Separated_sheet(CH4,OtherDataOutputMonthAvg, OtherDataOutputMonthStdev,CL,PreviousCL,rounding_off_place,name,CurrentColumn,CurrentRow);
								CH4_AVG_AVG[doubleAvgCellCounter]=AVERAGE(CH4, CL, PreviousCL, rounding_off_place);
								CH4_STDEV_AVG[doubleAvgCellCounter]=STDEV(CH4, CL, PreviousCL, rounding_off_place);
								CurrentColumn++;
							}
							if(numWIND_SPEED==1) {
								String name="WIND_SPEED";
								OtherdataOutput_Separated_sheet(WIND_SPEED,OtherDataOutputMonthAvg, OtherDataOutputMonthStdev,CL,PreviousCL,rounding_off_place,name,CurrentColumn,CurrentRow);
								WIND_SPEED_AVG_AVG[doubleAvgCellCounter]=AVERAGE(WIND_SPEED, CL, PreviousCL, rounding_off_place);
								WIND_SPEED_STDEV_AVG[doubleAvgCellCounter]=STDEV(WIND_SPEED, CL, PreviousCL, rounding_off_place);
								CurrentColumn++;
							}
							if(numWS_HR==1)	{
								String name="WS_HR";
								OtherdataOutput_Separated_sheet(WS_HR,OtherDataOutputMonthAvg, OtherDataOutputMonthStdev,CL,PreviousCL,rounding_off_place,name,CurrentColumn,CurrentRow);
								WS_HR_AVG_AVG[doubleAvgCellCounter]=AVERAGE(WS_HR, CL, PreviousCL, rounding_off_place);
								WS_HR_STDEV_AVG[doubleAvgCellCounter]=STDEV(WS_HR, CL, PreviousCL, rounding_off_place);
								CurrentColumn++;
							}					
							if(numAMB_TEMP==1) {
								String name="AMB_TEMP";
								OtherdataOutput_Separated_sheet(AMB_TEMP,OtherDataOutputMonthAvg, OtherDataOutputMonthStdev,CL,PreviousCL,rounding_off_place,name,CurrentColumn,CurrentRow);
								AMB_TEMP_AVG_AVG[doubleAvgCellCounter]=AVERAGE(AMB_TEMP, CL, PreviousCL, rounding_off_place);
								AMB_TEMP_STDEV_AVG[doubleAvgCellCounter]=STDEV(AMB_TEMP, CL, PreviousCL, rounding_off_place);
								CurrentColumn++;
							}
							if(numRAIN_INT==1) {
								String name="RAIN_INT";
								OtherdataOutput_Separated_sheet(RAIN_INT,OtherDataOutputMonthAvg, OtherDataOutputMonthStdev,CL,PreviousCL,rounding_off_place,name,CurrentColumn,CurrentRow);
								RAIN_INT_AVG_AVG[doubleAvgCellCounter]=AVERAGE(RAIN_INT, CL, PreviousCL, rounding_off_place);
								RAIN_INT_STDEV_AVG[doubleAvgCellCounter]=STDEV(RAIN_INT, CL, PreviousCL, rounding_off_place);
								CurrentColumn++;
							}
							if(numPH_RAIN==1) {
								String name="PH_RAIN";
								OtherdataOutput_Separated_sheet(PH_RAIN,OtherDataOutputMonthAvg, OtherDataOutputMonthStdev,CL,PreviousCL,rounding_off_place,name,CurrentColumn,CurrentRow);
								PH_RAIN_AVG_AVG[doubleAvgCellCounter]=AVERAGE(PH_RAIN, CL, PreviousCL, rounding_off_place);
								PH_RAIN_STDEV_AVG[doubleAvgCellCounter]=STDEV(PH_RAIN, CL, PreviousCL, rounding_off_place);
								CurrentColumn++;
							}
							if(numRH==1) {
								String name="RH";
								OtherdataOutput_Separated_sheet(RH,OtherDataOutputMonthAvg, OtherDataOutputMonthStdev,CL,PreviousCL,rounding_off_place,name,CurrentColumn,CurrentRow);
								RH_AVG_AVG[doubleAvgCellCounter]=AVERAGE(RH, CL, PreviousCL, rounding_off_place);
								RH_STDEV_AVG[doubleAvgCellCounter]=STDEV(RH, CL, PreviousCL, rounding_off_place);
								CurrentColumn++;
							}
							if(numRAIN_COND==1)	{
								String name="RAIN_COND";
								OtherdataOutput_Separated_sheet(RAIN_COND,OtherDataOutputMonthAvg, OtherDataOutputMonthStdev,CL,PreviousCL,rounding_off_place,name,CurrentColumn,CurrentRow);
								RAIN_COND_AVG_AVG[doubleAvgCellCounter]=AVERAGE(RAIN_COND, CL, PreviousCL, rounding_off_place);
								RAIN_COND_STDEV_AVG[doubleAvgCellCounter]=STDEV(RAIN_COND, CL, PreviousCL, rounding_off_place);
								CurrentColumn++;
							}
							/*if(true)//calculate data amount in the month
							{
								int DATA_AMOUNT=CL-PreviousCL;
								jxl.write.Number data_amount = new jxl.write.Number(CurrentColumn,CurrentRow,DATA_AMOUNT);
								jxl.write.Label data_amount_1 = new jxl.write.Label(CurrentColumn,0,"DATA_AMOUNT");
								jxl.write.Number data_amount_2 = new jxl.write.Number(CurrentColumn,CurrentRow,DATA_AMOUNT);
								jxl.write.Label data_amount_3 = new jxl.write.Label(CurrentColumn,0,"DATA_AMOUNT");
								
								CurrentColumn++;
								OtherDataOutputMonthAvg.addCell(data_amount);
								OtherDataOutputMonthAvg.addCell(data_amount_1);
								
								OtherDataOutputMonthStdev.addCell(data_amount_2);
								OtherDataOutputMonthStdev.addCell(data_amount_3);
							}*/
							
							CurrentRow++;
							doubleAvgCellCounter++;
						}
						PreviousCL=CL;
						
					}
					
					//write AVG after AVG and AVG after STDEV, usefulness unknown
					do {
						
						for(int l=0;l<doubleAvgCellCounter;l++) {
							
						}
						
						
						
						List<Compare> month1=null;
						Compare com=null;
						for(int j=0;j<realData.size();j++) {
							boolean ifFound=false;
							month1 = (List<Compare>) realData.get(j);
							for(int k=0;k<month1.size();k++) {
								com = month1.get(k);
								if(com.getMonth().indexOf(OutputDate2)>=0) {
									ifFound=true;
									break;
								}
							}
							if(ifFound) {
								break;
							}
						}
						int CurrentColumn=0;
						if(true)//DATE
						{
							jxl.write.Label label = new jxl.write.Label(CurrentColumn,19,"AVERAGE");
							jxl.write.Label label_1 = new jxl.write.Label(CurrentColumn,20,"STDEV");
							jxl.write.Label label_2 = new jxl.write.Label(CurrentColumn,21,"AVERAGE above 2 STDEVs");
							jxl.write.Label label_3 = new jxl.write.Label(CurrentColumn,22,"AVERAGE above 1 STDEV");
							jxl.write.Label label_4 = new jxl.write.Label(CurrentColumn,23,"AVERAGE below 1 STDEV");
							jxl.write.Label label_5 = new jxl.write.Label(CurrentColumn,24,"AVERAGE below 2 STDEVs");
							
							jxl.write.Label label_6 = new jxl.write.Label(CurrentColumn,19,"AVERAGE");
							jxl.write.Label label_7 = new jxl.write.Label(CurrentColumn,20,"STDEV");
							jxl.write.Label label_8 = new jxl.write.Label(CurrentColumn,21,"AVERAGE above 2 STDEVs");
							jxl.write.Label label_9 = new jxl.write.Label(CurrentColumn,22,"AVERAGE above 1 STDEV");
							jxl.write.Label label_10 = new jxl.write.Label(CurrentColumn,23,"AVERAGE below 1 STDEV");
							jxl.write.Label label_11 = new jxl.write.Label(CurrentColumn,24,"AVERAGE below 2 STDEVs");
							
							
							CurrentColumn++;
							OtherDataOutputMonthAvg.addCell(label);
							OtherDataOutputMonthAvg.addCell(label_1);
							OtherDataOutputMonthAvg.addCell(label_2);
							OtherDataOutputMonthAvg.addCell(label_3);
							OtherDataOutputMonthAvg.addCell(label_4);
							OtherDataOutputMonthAvg.addCell(label_5);
							
							OtherDataOutputMonthStdev.addCell(label_6);
							OtherDataOutputMonthStdev.addCell(label_7);
							OtherDataOutputMonthStdev.addCell(label_8);
							OtherDataOutputMonthStdev.addCell(label_9);
							OtherDataOutputMonthStdev.addCell(label_10);
							OtherDataOutputMonthStdev.addCell(label_11);
						}					
						if(numSO2==1) {	
							DoubleCalculation(SO2_AVG_AVG, SO2_STDEV_AVG, doubleAvgCellCounter, CurrentColumn, OtherDataOutputMonthAvg, OtherDataOutputMonthStdev, rounding_off_place);
							com.setSO2(double_AmountReduction(SO2_AVG_AVG, doubleAvgCellCounter));
							CurrentColumn++;
						}
						if(numCO==1) {
							DoubleCalculation(CO_AVG_AVG, CO_STDEV_AVG, doubleAvgCellCounter, CurrentColumn, OtherDataOutputMonthAvg, OtherDataOutputMonthStdev, rounding_off_place);
							com.setCO(double_AmountReduction(CO_AVG_AVG, doubleAvgCellCounter));
							CurrentColumn++;
						}
						if(numCO2==1) {
							DoubleCalculation(CO2_AVG_AVG, CO2_STDEV_AVG, doubleAvgCellCounter, CurrentColumn, OtherDataOutputMonthAvg, OtherDataOutputMonthStdev, rounding_off_place);
							com.setCO2(double_AmountReduction(CO2_AVG_AVG, doubleAvgCellCounter));
							CurrentColumn++;
						}
						if(numO3==1) {
							DoubleCalculation(O3_AVG_AVG, O3_STDEV_AVG, doubleAvgCellCounter, CurrentColumn, OtherDataOutputMonthAvg, OtherDataOutputMonthStdev, rounding_off_place);
							com.setO3(double_AmountReduction(O3_AVG_AVG, doubleAvgCellCounter));
							CurrentColumn++;
						}
						if(numNOx==1) {
							DoubleCalculation(NOx_AVG_AVG, NOx_STDEV_AVG, doubleAvgCellCounter, CurrentColumn, OtherDataOutputMonthAvg, OtherDataOutputMonthStdev, rounding_off_place);
							com.setNOx(double_AmountReduction(NOx_AVG_AVG, doubleAvgCellCounter));
							CurrentColumn++;
						}
						if(numNO==1) {
							DoubleCalculation(NO_AVG_AVG, NO_STDEV_AVG, doubleAvgCellCounter, CurrentColumn, OtherDataOutputMonthAvg, OtherDataOutputMonthStdev, rounding_off_place);
							com.setNO(double_AmountReduction(NO_AVG_AVG, doubleAvgCellCounter));
							CurrentColumn++;
						}
						if(numNO2==1) {
							DoubleCalculation(NO2_AVG_AVG, NO2_STDEV_AVG, doubleAvgCellCounter, CurrentColumn, OtherDataOutputMonthAvg, OtherDataOutputMonthStdev, rounding_off_place);
							com.setNO2(double_AmountReduction(NO2_AVG_AVG, doubleAvgCellCounter));
							CurrentColumn++;
						}
						if(numTHC==1) {
							DoubleCalculation(THC_AVG_AVG, THC_STDEV_AVG, doubleAvgCellCounter, CurrentColumn, OtherDataOutputMonthAvg, OtherDataOutputMonthStdev, rounding_off_place);
							com.setTHC(double_AmountReduction(THC_AVG_AVG, doubleAvgCellCounter));
							CurrentColumn++;
						}
						if(numNMHC==1) {
							DoubleCalculation(NMHC_AVG_AVG, NMHC_STDEV_AVG, doubleAvgCellCounter, CurrentColumn, OtherDataOutputMonthAvg, OtherDataOutputMonthStdev, rounding_off_place);
							com.setNMHC(double_AmountReduction(NMHC_AVG_AVG, doubleAvgCellCounter));;
							CurrentColumn++;
						}
						if(numCH4==1) {
							DoubleCalculation(CH4_AVG_AVG, CH4_STDEV_AVG, doubleAvgCellCounter, CurrentColumn, OtherDataOutputMonthAvg, OtherDataOutputMonthStdev, rounding_off_place);
							com.setCH4(double_AmountReduction(CH4_AVG_AVG, doubleAvgCellCounter));
							CurrentColumn++;
						}
						if(numWIND_SPEED==1) {
							DoubleCalculation(WIND_SPEED_AVG_AVG, WIND_SPEED_STDEV_AVG, doubleAvgCellCounter, CurrentColumn, OtherDataOutputMonthAvg, OtherDataOutputMonthStdev, rounding_off_place);
							com.setWIND_SPEED(double_AmountReduction(WIND_SPEED_AVG_AVG, doubleAvgCellCounter));
							CurrentColumn++;
						}
						if(numWS_HR==1) {
							DoubleCalculation(WS_HR_AVG_AVG, WS_HR_STDEV_AVG, doubleAvgCellCounter, CurrentColumn, OtherDataOutputMonthAvg, OtherDataOutputMonthStdev, rounding_off_place);
							com.setWS_HR(double_AmountReduction(WS_HR_AVG_AVG, doubleAvgCellCounter));
							CurrentColumn++;
						}					
						if(numAMB_TEMP==1) {
							DoubleCalculation(AMB_TEMP_AVG_AVG, AMB_TEMP_STDEV_AVG, doubleAvgCellCounter, CurrentColumn, OtherDataOutputMonthAvg, OtherDataOutputMonthStdev, rounding_off_place);
							com.setAMB_TEMP(double_AmountReduction(AMB_TEMP_AVG_AVG, doubleAvgCellCounter));
							CurrentColumn++;
						}
						if(numRAIN_INT==1) {
							DoubleCalculation(RAIN_INT_AVG_AVG, RAIN_INT_STDEV_AVG, doubleAvgCellCounter, CurrentColumn, OtherDataOutputMonthAvg, OtherDataOutputMonthStdev, rounding_off_place);
							com.setRAIN_INT(double_AmountReduction(RAIN_INT_AVG_AVG, doubleAvgCellCounter));
							CurrentColumn++;
						}
						if(numPH_RAIN==1) {
							DoubleCalculation(PH_RAIN_AVG_AVG, PH_RAIN_STDEV_AVG, doubleAvgCellCounter, CurrentColumn, OtherDataOutputMonthAvg, OtherDataOutputMonthStdev, rounding_off_place);
							com.setPH_RAIN(double_AmountReduction(PH_RAIN_AVG_AVG, doubleAvgCellCounter));
							CurrentColumn++;
						}
						if(numRH==1) {
							DoubleCalculation(RH_AVG_AVG, RH_STDEV_AVG, doubleAvgCellCounter, CurrentColumn, OtherDataOutputMonthAvg, OtherDataOutputMonthStdev, rounding_off_place);
							com.setRH(double_AmountReduction(RH_AVG_AVG, doubleAvgCellCounter));
							CurrentColumn++;
						}
						if(numRAIN_COND==1) {
							DoubleCalculation(RAIN_COND_AVG_AVG, RAIN_COND_STDEV_AVG, doubleAvgCellCounter, CurrentColumn, OtherDataOutputMonthAvg, OtherDataOutputMonthStdev, rounding_off_place);
							com.setRAIN_COND(double_AmountReduction(RAIN_COND_AVG_AVG, doubleAvgCellCounter));
							CurrentColumn++;
						}
					}while(false);
					
					OtherdataBookMonthSheetCounter++;
				}
				
			}
			
			OtherDataBookMonthAvg.write();
			OtherDataBookMonthAvg.close();
			
			OtherDataBookMonthStdev.write();
			OtherDataBookMonthStdev.close();
			System.gc();
			
			
			//output excel row, column counter
			int OtherdataOutputAllCurrentRow=1;
			int OtherdataOutputAllCurrentColumn;
			
			//calculate data of all year the each month in entire input file 
			doubleAvgCellCounter=0;
			for(int i=0;i<month.length;i++) {
				//OtherdataOutputAllCurrentColumn reset to default
				OtherdataOutputAllCurrentColumn=0;
				
				boolean ifexist=false;
				String OutputDate1= month[i];
				
				for(int j=0;j<ContainLocation;j++)//check whether the selected month exist
				{
					if(DATE[j].indexOf(OutputDate1)>=0)
					{
						ifexist=true;
						break;
					}
				}
				
				if(ifexist) {
					if(true) {//DATE
						jxl.write.Label label = new jxl.write.Label(OtherdataOutputAllCurrentColumn,OtherdataOutputAllCurrentRow,OutputDate1);
						jxl.write.Label label_1 = new jxl.write.Label(OtherdataOutputAllCurrentColumn,0,"DATE");
						jxl.write.Label label_2 = new jxl.write.Label(OtherdataOutputAllCurrentColumn,OtherdataOutputAllCurrentRow,OutputDate1);
						jxl.write.Label label_3 = new jxl.write.Label(OtherdataOutputAllCurrentColumn,0,"DATE");
						
						OtherdataOutputAllCurrentColumn++;
						OtherdataoutputAllAvg.addCell(label);
						OtherdataoutputAllAvg.addCell(label_1);
						
						OtherdataoutputAllStdev.addCell(label_2);
						OtherdataoutputAllStdev.addCell(label_3);
						
					}
					
					if(numSO2==1) {
						String name = "SO2";
						OtherdataOutput_Each_Month(SO2, OtherdataoutputAllAvg, OtherdataoutputAllStdev, DATE, OutputDate1, rounding_off_place, ContainLocation, OtherdataOutputAllCurrentColumn, OtherdataOutputAllCurrentRow, name);
						SO2_AVG_AVG[doubleAvgCellCounter]=ALL_AVERAGE(SO2, DATE, OutputDate1, rounding_off_place, ContainLocation);
						SO2_STDEV_AVG[doubleAvgCellCounter]=ALL_STDEV(SO2, DATE, OutputDate1, rounding_off_place, ContainLocation);
						OtherdataOutputAllCurrentColumn++;
					}
					if(numCO==1) {
						String name = "CO";
						OtherdataOutput_Each_Month(CO, OtherdataoutputAllAvg, OtherdataoutputAllStdev, DATE, OutputDate1, rounding_off_place, ContainLocation, OtherdataOutputAllCurrentColumn, OtherdataOutputAllCurrentRow, name);
						CO_AVG_AVG[doubleAvgCellCounter]=ALL_AVERAGE(CO, DATE, OutputDate1, rounding_off_place, ContainLocation);
						CO_STDEV_AVG[doubleAvgCellCounter]=ALL_STDEV(CO, DATE, OutputDate1, rounding_off_place, ContainLocation);
						OtherdataOutputAllCurrentColumn++;
					}
					if(numCO2==1) {
						String name = "CO2";
						OtherdataOutput_Each_Month(CO2, OtherdataoutputAllAvg, OtherdataoutputAllStdev, DATE, OutputDate1, rounding_off_place, ContainLocation, OtherdataOutputAllCurrentColumn, OtherdataOutputAllCurrentRow, name);
						CO2_AVG_AVG[doubleAvgCellCounter]=ALL_AVERAGE(CO2, DATE, OutputDate1, rounding_off_place, ContainLocation);
						CO2_STDEV_AVG[doubleAvgCellCounter]=ALL_STDEV(CO2, DATE, OutputDate1, rounding_off_place, ContainLocation);
						OtherdataOutputAllCurrentColumn++;
					}
					if(numO3==1) {
						String name = "O3";
						OtherdataOutput_Each_Month(O3, OtherdataoutputAllAvg, OtherdataoutputAllStdev, DATE, OutputDate1, rounding_off_place, ContainLocation, OtherdataOutputAllCurrentColumn, OtherdataOutputAllCurrentRow, name);
						O3_AVG_AVG[doubleAvgCellCounter]=ALL_AVERAGE(O3, DATE, OutputDate1, rounding_off_place, ContainLocation);
						O3_STDEV_AVG[doubleAvgCellCounter]=ALL_STDEV(O3, DATE, OutputDate1, rounding_off_place, ContainLocation);
						OtherdataOutputAllCurrentColumn++;
					}
					if(numNOx==1) {
						String name = "NOx";
						OtherdataOutput_Each_Month(NOx, OtherdataoutputAllAvg, OtherdataoutputAllStdev, DATE, OutputDate1, rounding_off_place, ContainLocation, OtherdataOutputAllCurrentColumn, OtherdataOutputAllCurrentRow, name);
						NOx_AVG_AVG[doubleAvgCellCounter]=ALL_AVERAGE(NOx, DATE, OutputDate1, rounding_off_place, ContainLocation);
						NOx_STDEV_AVG[doubleAvgCellCounter]=ALL_STDEV(NOx, DATE, OutputDate1, rounding_off_place, ContainLocation);
						OtherdataOutputAllCurrentColumn++;
					}
					if(numNO==1) {
						String name = "NO";
						OtherdataOutput_Each_Month(NO, OtherdataoutputAllAvg, OtherdataoutputAllStdev, DATE, OutputDate1, rounding_off_place, ContainLocation, OtherdataOutputAllCurrentColumn, OtherdataOutputAllCurrentRow, name);
						NO_AVG_AVG[doubleAvgCellCounter]=ALL_AVERAGE(NO, DATE, OutputDate1, rounding_off_place, ContainLocation);
						NO_STDEV_AVG[doubleAvgCellCounter]=ALL_STDEV(NO, DATE, OutputDate1, rounding_off_place, ContainLocation);
						OtherdataOutputAllCurrentColumn++;
					}
					if(numNO2==1) {
						String name = "NO2";
						OtherdataOutput_Each_Month(NO2, OtherdataoutputAllAvg, OtherdataoutputAllStdev, DATE, OutputDate1, rounding_off_place, ContainLocation, OtherdataOutputAllCurrentColumn, OtherdataOutputAllCurrentRow, name);
						NO2_AVG_AVG[doubleAvgCellCounter]=ALL_AVERAGE(NO2, DATE, OutputDate1, rounding_off_place, ContainLocation);
						NO2_STDEV_AVG[doubleAvgCellCounter]=ALL_STDEV(NO2, DATE, OutputDate1, rounding_off_place, ContainLocation);
						OtherdataOutputAllCurrentColumn++;
					}
					if(numTHC==1) {
						String name = "THC";
						OtherdataOutput_Each_Month(THC, OtherdataoutputAllAvg, OtherdataoutputAllStdev, DATE, OutputDate1, rounding_off_place, ContainLocation, OtherdataOutputAllCurrentColumn, OtherdataOutputAllCurrentRow, name);
						THC_AVG_AVG[doubleAvgCellCounter]=ALL_AVERAGE(THC, DATE, OutputDate1, rounding_off_place, ContainLocation);
						THC_STDEV_AVG[doubleAvgCellCounter]=ALL_STDEV(THC, DATE, OutputDate1, rounding_off_place, ContainLocation);
						OtherdataOutputAllCurrentColumn++;
					}
					if(numNMHC==1) {
						String name = "NMHC";
						OtherdataOutput_Each_Month(NMHC, OtherdataoutputAllAvg, OtherdataoutputAllStdev, DATE, OutputDate1, rounding_off_place, ContainLocation, OtherdataOutputAllCurrentColumn, OtherdataOutputAllCurrentRow, name);
						NMHC_AVG_AVG[doubleAvgCellCounter]=ALL_AVERAGE(NMHC, DATE, OutputDate1, rounding_off_place, ContainLocation);
						NMHC_STDEV_AVG[doubleAvgCellCounter]=ALL_STDEV(NMHC, DATE, OutputDate1, rounding_off_place, ContainLocation);
						OtherdataOutputAllCurrentColumn++;
					}
					if(numCH4==1) {
						String name = "CH4";
						OtherdataOutput_Each_Month(CH4, OtherdataoutputAllAvg, OtherdataoutputAllStdev, DATE, OutputDate1, rounding_off_place, ContainLocation, OtherdataOutputAllCurrentColumn, OtherdataOutputAllCurrentRow, name);
						CH4_AVG_AVG[doubleAvgCellCounter]=ALL_AVERAGE(CH4, DATE, OutputDate1, rounding_off_place, ContainLocation);
						CH4_STDEV_AVG[doubleAvgCellCounter]=ALL_STDEV(CH4, DATE, OutputDate1, rounding_off_place, ContainLocation);
						OtherdataOutputAllCurrentColumn++;
					}
					if(numWIND_SPEED==1) {
						String name = "WIND_SPEED";
						OtherdataOutput_Each_Month(WIND_SPEED, OtherdataoutputAllAvg, OtherdataoutputAllStdev, DATE, OutputDate1, rounding_off_place, ContainLocation, OtherdataOutputAllCurrentColumn, OtherdataOutputAllCurrentRow, name);
						WIND_SPEED_AVG_AVG[doubleAvgCellCounter]=ALL_AVERAGE(WIND_SPEED, DATE, OutputDate1, rounding_off_place, ContainLocation);
						WIND_SPEED_STDEV_AVG[doubleAvgCellCounter]=ALL_STDEV(WIND_SPEED, DATE, OutputDate1, rounding_off_place, ContainLocation);
						OtherdataOutputAllCurrentColumn++;
					}
					if(numWS_HR==1) {
						String name = "WS_HR";
						OtherdataOutput_Each_Month(WS_HR, OtherdataoutputAllAvg, OtherdataoutputAllStdev, DATE, OutputDate1, rounding_off_place, ContainLocation, OtherdataOutputAllCurrentColumn, OtherdataOutputAllCurrentRow, name);
						WS_HR_AVG_AVG[doubleAvgCellCounter]=ALL_AVERAGE(WS_HR, DATE, OutputDate1, rounding_off_place, ContainLocation);
						WS_HR_STDEV_AVG[doubleAvgCellCounter]=ALL_STDEV(WS_HR, DATE, OutputDate1, rounding_off_place, ContainLocation);
						OtherdataOutputAllCurrentColumn++;
					}					
					if(numAMB_TEMP==1) {
						String name = "AMB_TEMP";
						OtherdataOutput_Each_Month(AMB_TEMP, OtherdataoutputAllAvg, OtherdataoutputAllStdev, DATE, OutputDate1, rounding_off_place, ContainLocation, OtherdataOutputAllCurrentColumn, OtherdataOutputAllCurrentRow, name);
						AMB_TEMP_AVG_AVG[doubleAvgCellCounter]=ALL_AVERAGE(AMB_TEMP, DATE, OutputDate1, rounding_off_place, ContainLocation);
						AMB_TEMP_STDEV_AVG[doubleAvgCellCounter]=ALL_STDEV(AMB_TEMP, DATE, OutputDate1, rounding_off_place, ContainLocation);
						OtherdataOutputAllCurrentColumn++;
					}
					if(numRAIN_INT==1) {
						String name = "RAIN_INT";
						OtherdataOutput_Each_Month(RAIN_INT, OtherdataoutputAllAvg, OtherdataoutputAllStdev, DATE, OutputDate1, rounding_off_place, ContainLocation, OtherdataOutputAllCurrentColumn, OtherdataOutputAllCurrentRow, name);
						RAIN_INT_AVG_AVG[doubleAvgCellCounter]=ALL_AVERAGE(RAIN_INT, DATE, OutputDate1, rounding_off_place, ContainLocation);
						RAIN_INT_STDEV_AVG[doubleAvgCellCounter]=ALL_STDEV(RAIN_INT, DATE, OutputDate1, rounding_off_place, ContainLocation);
						OtherdataOutputAllCurrentColumn++;
					}
					if(numPH_RAIN==1) {
						String name = "PH_RAIN";
						OtherdataOutput_Each_Month(PH_RAIN, OtherdataoutputAllAvg, OtherdataoutputAllStdev, DATE, OutputDate1, rounding_off_place, ContainLocation, OtherdataOutputAllCurrentColumn, OtherdataOutputAllCurrentRow, name);
						PH_RAIN_AVG_AVG[doubleAvgCellCounter]=ALL_AVERAGE(PH_RAIN, DATE, OutputDate1, rounding_off_place, ContainLocation);
						PH_RAIN_STDEV_AVG[doubleAvgCellCounter]=ALL_STDEV(PH_RAIN, DATE, OutputDate1, rounding_off_place, ContainLocation);
						OtherdataOutputAllCurrentColumn++;
					}
					if(numRH==1) {
						String name = "RH";
						OtherdataOutput_Each_Month(RH, OtherdataoutputAllAvg, OtherdataoutputAllStdev, DATE, OutputDate1, rounding_off_place, ContainLocation, OtherdataOutputAllCurrentColumn, OtherdataOutputAllCurrentRow, name);
						RH_AVG_AVG[doubleAvgCellCounter]=ALL_AVERAGE(RH, DATE, OutputDate1, rounding_off_place, ContainLocation);
						RH_STDEV_AVG[doubleAvgCellCounter]=ALL_STDEV(RH, DATE, OutputDate1, rounding_off_place, ContainLocation);
						OtherdataOutputAllCurrentColumn++;
					}
					if(numRAIN_COND==1) {
						String name = "RAIN_COND";
						OtherdataOutput_Each_Month(RAIN_COND, OtherdataoutputAllAvg, OtherdataoutputAllStdev, DATE, OutputDate1, rounding_off_place, ContainLocation, OtherdataOutputAllCurrentColumn, OtherdataOutputAllCurrentRow, name);
						RAIN_COND_AVG_AVG[doubleAvgCellCounter]=ALL_AVERAGE(RAIN_COND, DATE, OutputDate1, rounding_off_place, ContainLocation);
						RAIN_COND_STDEV_AVG[doubleAvgCellCounter]=ALL_STDEV(RAIN_COND, DATE, OutputDate1, rounding_off_place, ContainLocation);
						OtherdataOutputAllCurrentColumn++;
					}
					/*
					for(int m=0;m<realData.size();m++) {
						List<Compare> listMonth = (List<Compare>) realData.get(m);
						for(int n=0;n<listMonth.size();n++) {
							Compare com = listMonth.get(n);
							if(com.getMonth().indexOf(OutputDate1)>=0) {
								if(com.getAVG_TEMP().length!=1) {
									
								}
							}
							
						}
					}
					*/
					
					/*if(true)//calculate data amount of all the each month in entire input file
					{
						int DATA_AMOUNT=DATA_AMOUNT(DATE, OutputDate1, ContainLocation);
						jxl.write.Number data_amount = new jxl.write.Number(OtherdataOutputAllCurrentColumn,OtherdataOutputAllCurrentRow,DATA_AMOUNT);
						jxl.write.Label data_amount_1 = new jxl.write.Label(OtherdataOutputAllCurrentColumn,0,"DATA_AMOUNT");
						jxl.write.Number data_amount_2 = new jxl.write.Number(OtherdataOutputAllCurrentColumn,OtherdataOutputAllCurrentRow,DATA_AMOUNT);
						jxl.write.Label data_amount_3 = new jxl.write.Label(OtherdataOutputAllCurrentColumn,0,"DATA_AMOUNT");
						jxl.write.Number data_amount_4 = new jxl.write.Number(OtherdataOutputAllCurrentColumn,15,ContainLocation);
						jxl.write.Number data_amount_5 = new jxl.write.Number(OtherdataOutputAllCurrentColumn,15,ContainLocation);
						
						OtherdataOutputAllCurrentColumn++;
						OtherdataoutputAllAvg.addCell(data_amount);
						OtherdataoutputAllAvg.addCell(data_amount_1);
						OtherdataoutputAllAvg.addCell(data_amount_4);
						
						OtherdataoutputAllStdev.addCell(data_amount_2);
						OtherdataoutputAllStdev.addCell(data_amount_3);
						OtherdataoutputAllStdev.addCell(data_amount_5);
					}*/
					
					
					OtherdataOutputAllCurrentRow++;
					doubleAvgCellCounter++;
					
				}
				
				
				
			}
			
			do {//write AVG after AVG and AVG after STDEV, usefulness unknown
				int CurrentColumn=0;
				if(true) {//DATE
					jxl.write.Label label = new jxl.write.Label(CurrentColumn,19,"AVERAGE");
					jxl.write.Label label_1 = new jxl.write.Label(CurrentColumn,20,"STDEV");
					jxl.write.Label label_2 = new jxl.write.Label(CurrentColumn,21,"AVERAGE above 2 STDEVs");
					jxl.write.Label label_3 = new jxl.write.Label(CurrentColumn,22,"AVERAGE above 1 STDEV");
					jxl.write.Label label_4 = new jxl.write.Label(CurrentColumn,23,"AVERAGE below 1 STDEV");
					jxl.write.Label label_5 = new jxl.write.Label(CurrentColumn,24,"AVERAGE below 2 STDEVs");
					
					jxl.write.Label label_6 = new jxl.write.Label(CurrentColumn,19,"AVERAGE");
					jxl.write.Label label_7 = new jxl.write.Label(CurrentColumn,20,"STDEV");
					jxl.write.Label label_8 = new jxl.write.Label(CurrentColumn,21,"AVERAGE above 2 STDEVs");
					jxl.write.Label label_9 = new jxl.write.Label(CurrentColumn,22,"AVERAGE above 1 STDEV");
					jxl.write.Label label_10 = new jxl.write.Label(CurrentColumn,23,"AVERAGE below 1 STDEV");
					jxl.write.Label label_11 = new jxl.write.Label(CurrentColumn,24,"AVERAGE below 2 STDEVs");
					
					
					CurrentColumn++;
					OtherdataoutputAllAvg.addCell(label);
					OtherdataoutputAllAvg.addCell(label_1);
					OtherdataoutputAllAvg.addCell(label_2);
					OtherdataoutputAllAvg.addCell(label_3);
					OtherdataoutputAllAvg.addCell(label_4);
					OtherdataoutputAllAvg.addCell(label_5);
					
					OtherdataoutputAllStdev.addCell(label_6);
					OtherdataoutputAllStdev.addCell(label_7);
					OtherdataoutputAllStdev.addCell(label_8);
					OtherdataoutputAllStdev.addCell(label_9);
					OtherdataoutputAllStdev.addCell(label_10);
					OtherdataoutputAllStdev.addCell(label_11);
				}
				
				if(numSO2==1) {	
					DoubleCalculation(SO2_AVG_AVG, SO2_STDEV_AVG, doubleAvgCellCounter, CurrentColumn, OtherdataoutputAllAvg, OtherdataoutputAllStdev, rounding_off_place);
					CurrentColumn++;
				}
				if(numCO==1) {
					DoubleCalculation(CO_AVG_AVG, CO_STDEV_AVG, doubleAvgCellCounter, CurrentColumn, OtherdataoutputAllAvg, OtherdataoutputAllStdev, rounding_off_place);
					CurrentColumn++;
				}
				if(numCO2==1) {
					DoubleCalculation(CO2_AVG_AVG, CO2_STDEV_AVG, doubleAvgCellCounter, CurrentColumn, OtherdataoutputAllAvg, OtherdataoutputAllStdev, rounding_off_place);
					CurrentColumn++;
				}
				if(numO3==1) {
					DoubleCalculation(O3_AVG_AVG, O3_STDEV_AVG, doubleAvgCellCounter, CurrentColumn, OtherdataoutputAllAvg, OtherdataoutputAllStdev, rounding_off_place);
					CurrentColumn++;
				}
				if(numNOx==1) {
					DoubleCalculation(NOx_AVG_AVG, NOx_STDEV_AVG, doubleAvgCellCounter, CurrentColumn, OtherdataoutputAllAvg, OtherdataoutputAllStdev, rounding_off_place);
					CurrentColumn++;
				}
				if(numNO==1) {
					DoubleCalculation(NO_AVG_AVG, NO_STDEV_AVG, doubleAvgCellCounter, CurrentColumn, OtherdataoutputAllAvg, OtherdataoutputAllStdev, rounding_off_place);
					CurrentColumn++;
				}
				if(numNO2==1) {
					DoubleCalculation(NO2_AVG_AVG, NO2_STDEV_AVG, doubleAvgCellCounter, CurrentColumn, OtherdataoutputAllAvg, OtherdataoutputAllStdev, rounding_off_place);
					CurrentColumn++;
				}
				if(numTHC==1) {
					DoubleCalculation(THC_AVG_AVG, THC_STDEV_AVG, doubleAvgCellCounter, CurrentColumn, OtherdataoutputAllAvg, OtherdataoutputAllStdev, rounding_off_place);
					CurrentColumn++;
				}
				if(numNMHC==1) {
					DoubleCalculation(NMHC_AVG_AVG, NMHC_STDEV_AVG, doubleAvgCellCounter, CurrentColumn, OtherdataoutputAllAvg, OtherdataoutputAllStdev, rounding_off_place);
					CurrentColumn++;
				}
				if(numCH4==1) {
					DoubleCalculation(CH4_AVG_AVG, CH4_STDEV_AVG, doubleAvgCellCounter, CurrentColumn, OtherdataoutputAllAvg, OtherdataoutputAllStdev, rounding_off_place);
					CurrentColumn++;
				}
				if(numWIND_SPEED==1) {
					DoubleCalculation(WIND_SPEED_AVG_AVG, WIND_SPEED_STDEV_AVG, doubleAvgCellCounter, CurrentColumn, OtherdataoutputAllAvg, OtherdataoutputAllStdev, rounding_off_place);
					CurrentColumn++;
				}
				if(numWS_HR==1) {
					DoubleCalculation(WS_HR_AVG_AVG, WS_HR_STDEV_AVG, doubleAvgCellCounter, CurrentColumn, OtherdataoutputAllAvg, OtherdataoutputAllStdev, rounding_off_place);
					CurrentColumn++;
				}					
				if(numAMB_TEMP==1) {
					DoubleCalculation(AMB_TEMP_AVG_AVG, AMB_TEMP_STDEV_AVG, doubleAvgCellCounter, CurrentColumn, OtherdataoutputAllAvg, OtherdataoutputAllStdev, rounding_off_place);
					CurrentColumn++;
				}
				if(numRAIN_INT==1) {
					DoubleCalculation(RAIN_INT_AVG_AVG, RAIN_INT_STDEV_AVG, doubleAvgCellCounter, CurrentColumn, OtherdataoutputAllAvg, OtherdataoutputAllStdev, rounding_off_place);
					CurrentColumn++;
				}
				if(numPH_RAIN==1) {
					DoubleCalculation(PH_RAIN_AVG_AVG, PH_RAIN_STDEV_AVG, doubleAvgCellCounter, CurrentColumn, OtherdataoutputAllAvg, OtherdataoutputAllStdev, rounding_off_place);
					CurrentColumn++;
				}
				if(numRH==1) {
					DoubleCalculation(RH_AVG_AVG, RH_STDEV_AVG, doubleAvgCellCounter, CurrentColumn, OtherdataoutputAllAvg, OtherdataoutputAllStdev, rounding_off_place);
					CurrentColumn++;
				}
				if(numRAIN_COND==1) {
					DoubleCalculation(RAIN_COND_AVG_AVG, RAIN_COND_STDEV_AVG, doubleAvgCellCounter, CurrentColumn, OtherdataoutputAllAvg, OtherdataoutputAllStdev, rounding_off_place);
					CurrentColumn++;
				}
			}while(false);
			//=======//
			
			OtherdataBookAll.write();
			OtherdataBookAll.close();
			System.gc();
			
			ModifyExcel(OtherdataRoute);
			readModifiedExcel_Other(OtherdataRoute,dataList);
		}
		catch (Exception e) {
			System.out.println(fileNameTxt+" OtherdataOutput "+e);
		}
	}
	
	
	
	public void WriteExcel(String[] DATE, double[] SO2, double[] CO, double[] CO2, double[] O3, int[] PM10, int[] PM25, double[] NOx, double[] NO, double[] NO2, double[] THC, double[] NMHC, double[] CH4, double[] WIND_SPEED, double[] WS_HR, double[] AMB_TEMP, double[] RAIN_INT, double[] PH_RAIN, double[] RH, double[] RAIN_COND, String newFileRoute, String newDataRoute, String fileNameTxt, String filename, int ContainLocation, int rounding_off_place,int numSO2,int numCO
			,int numCO2,int numO3,int numPM10,int numPM25,int numNOx,int numNO,int numNO2,int numTHC,int numNMHC,int numCH4,int numWIND_SPEED,int numWS_HR,int numAMB_TEMP,int numRAIN_INT,int numPH_RAIN,int numRH,int numRAIN_COND,int RowAmounts, List<Position> dataList, String nonStandardizationRoute, String fileName, String externalDataRoute, OriginalData od, List<Compare> realData) {
		ImageProc IP = new ImageProc();
		DataRelevance_Non_Standardization dns = new DataRelevance_Non_Standardization();
		//jxl.write. (Column,Row,DATA)
		try {			
			String OutMonthCurrent;
			String OutYearCurrent;
			
			String OutMonthFirst;
			String OutYearFirst;
			
			String[] date_year;
			date_year = new String[20];
			int date_yearCounter=0;
			
			//initialize date and row counter
			OutMonthFirst = StringUtils.substringBefore(DATE[0], " ");
			OutYearFirst = DATE[0].substring(DATE[0].length()-4, DATE[0].length());
			
			date_year[date_yearCounter]=OutYearFirst;
			date_yearCounter++;
			
			int CL=0;//row location of the last place in a excel
			int PreviousCL=0;//row location of the first place in a excel
			
			while(true) {//output data format:one file a month,separated from original excel
				if(CL>=ContainLocation) {
					break;
				}
				
				
				WritableWorkbook book = Workbook.createWorkbook(new File(newFileRoute + "/"+ OutMonthFirst+"__"+OutYearFirst+".xls"));
				WritableSheet Outsheet = book.createSheet("sheet1", 0);
				
				System.out.println(newFileRoute + "/"+ OutMonthFirst+"__"+OutYearFirst+".xls");
				
				int CurrentRow=1;
				
				for(;CL<ContainLocation;) {
					OutMonthCurrent = StringUtils.substringBefore(DATE[CL], " ");
					OutYearCurrent = DATE[CL].substring(DATE[CL].length()-4, DATE[CL].length());
					int CurrentColumn=0;
					
					if(!DATE[CL].substring(DATE[CL].length()-4, DATE[CL].length()).equals(OutYearFirst)) {
						date_year[date_yearCounter]=DATE[CL].substring(DATE[CL].length()-4, DATE[CL].length());
						date_yearCounter++;
					}
					
					//current month or year ended
					if(!OutMonthFirst.equals(OutMonthCurrent) || !OutYearFirst.equals(OutYearCurrent)) {//update for new excel's date beginning							
						OutMonthFirst = StringUtils.substringBefore(DATE[CL], " ");
						OutYearFirst = DATE[CL].substring(DATE[CL].length()-4, DATE[CL].length());
						break;
					}
					
					if(true) {//DATE
						jxl.write.Label label = new jxl.write.Label(CurrentColumn,CurrentRow,DATE[CL]);
						jxl.write.Label label_1 = new jxl.write.Label(CurrentColumn,0,"DATE");
						CurrentColumn++;
						Outsheet.addCell(label);
						Outsheet.addCell(label_1);
					}					
					if(numSO2==1) {
						String name ="SO2";
						WriteExcel_Write(SO2[CL], Outsheet, CurrentColumn, CurrentRow, name); 
						CurrentColumn++;
					}
					if(numCO==1) {
						String name ="CO";
						WriteExcel_Write(CO[CL], Outsheet, CurrentColumn, CurrentRow, name); 
						CurrentColumn++;
					}
					if(numCO2==1) {
						String name ="CO2";
						WriteExcel_Write(CO2[CL], Outsheet, CurrentColumn, CurrentRow, name); 
						CurrentColumn++;
					}
					if(numO3==1) {
						String name ="O3";
						WriteExcel_Write(O3[CL], Outsheet, CurrentColumn, CurrentRow, name); 
						CurrentColumn++;
					}
					if(numPM10==1) {
						String name ="PM 10";
						WriteExcel_Write(PM10[CL], Outsheet, CurrentColumn, CurrentRow, name); 
						CurrentColumn++;
					}
					if(numPM25==1) {
						String name ="PM 2.5";
						WriteExcel_Write(PM25[CL], Outsheet, CurrentColumn, CurrentRow, name); 
						CurrentColumn++;
					}
					if(numNOx==1) {
						String name ="NOx";
						WriteExcel_Write(NOx[CL], Outsheet, CurrentColumn, CurrentRow, name); 
						CurrentColumn++;
					}
					if(numNO==1) {
						String name ="NO";
						WriteExcel_Write(NO[CL], Outsheet, CurrentColumn, CurrentRow, name); 
						CurrentColumn++;
					}
					if(numNO2==1) {
						String name ="NO2";
						WriteExcel_Write(NO2[CL], Outsheet, CurrentColumn, CurrentRow, name); 
						CurrentColumn++;
					}
					if(numTHC==1) {
						String name ="THC";
						WriteExcel_Write(THC[CL], Outsheet, CurrentColumn, CurrentRow, name); 
						CurrentColumn++;
					}
					if(numNMHC==1) {
						String name ="NMHC";
						WriteExcel_Write(NMHC[CL], Outsheet, CurrentColumn, CurrentRow, name); 
						CurrentColumn++;
					}
					if(numCH4==1) {
						String name ="CH4";
						WriteExcel_Write(CH4[CL], Outsheet, CurrentColumn, CurrentRow, name); 
						CurrentColumn++;
					}
					if(numWIND_SPEED==1) {
						String name ="WIND_SPEED";
						WriteExcel_Write(WIND_SPEED[CL], Outsheet, CurrentColumn, CurrentRow, name); 
						CurrentColumn++;
					}
					if(numWS_HR==1) {
						String name ="WS_HR";
						WriteExcel_Write(WS_HR[CL], Outsheet, CurrentColumn, CurrentRow, name); 
						CurrentColumn++;
					}					
					if(numAMB_TEMP==1) {
						String name ="AMB_TEMP";
						WriteExcel_Write(AMB_TEMP[CL], Outsheet, CurrentColumn, CurrentRow, name); 
						CurrentColumn++;
					}
					if(numRAIN_INT==1) {
						String name ="RAIN_INT";
						WriteExcel_Write(RAIN_INT[CL], Outsheet, CurrentColumn, CurrentRow, name); 
						CurrentColumn++;
					}
					if(numPH_RAIN==1) {
						String name ="PH_RAIN";
						WriteExcel_Write(PH_RAIN[CL], Outsheet, CurrentColumn, CurrentRow, name); 
						CurrentColumn++;
					}
					if(numRH==1) {
						String name ="RH";
						WriteExcel_Write(RH[CL], Outsheet, CurrentColumn, CurrentRow, name); 
						CurrentColumn++;
					}
					if(numRAIN_COND==1) {
						String name ="RAIN_COND";
						WriteExcel_Write(RAIN_COND[CL], Outsheet, CurrentColumn, CurrentRow, name); 
						CurrentColumn++;
					}
						
					CL++;
					CurrentRow++;
				}
				
				System.out.println("File Data Amount="+(CL-PreviousCL));  //PreviousCL start from 0, but math calculation must start from 1
				
				PreviousCL=CL;
								
				book.write();
				book.close();
				System.gc();
				
				System.out.println("Done\n\n");
			}
			
			
			
			
			if(numPM10==1&&numPM25==1) {
				PM10PM25Output(PM10,PM25,DATE,newDataRoute,fileNameTxt,filename,ContainLocation,rounding_off_place,dataList, realData);
			}
			
			OtherdataOutput(DATE, SO2, CO, CO2, O3, NOx, NO, NO2, THC, NMHC, CH4, WIND_SPEED, WS_HR, AMB_TEMP, RAIN_INT, PH_RAIN, RH, RAIN_COND, newDataRoute, fileNameTxt, filename, ContainLocation, rounding_off_place, numSO2, numCO
					, numCO2, numO3, numNOx, numNO, numNO2, numTHC, numNMHC, numCH4, numWIND_SPEED, numWS_HR, numAMB_TEMP, numRAIN_INT, numPH_RAIN, numRH, numRAIN_COND,dataList,realData);
			
			dns.readExternalData(realData,externalDataRoute,fileName,string_AmountReduction(date_year, date_yearCounter),od);
			
			dns.creatExcel(realData,nonStandardizationRoute,fileName);
			
			IP.compareToPosition(dataList,realData,fileName);
			
		}		
		catch(Exception e) {
			System.out.println(fileNameTxt+" WriteExcel "+e);
		}
	}//end
	
}//class end