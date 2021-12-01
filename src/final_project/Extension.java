package final_project;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;

import jxl.Workbook;
import jxl.format.Colour;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public class Extension {
	
	static final String month[] = {"December", "November", "October", "September", "August", "July", "June", "May", "April", "March", "February", "January"};
	static final String elements[] = {"PM10", "PM25","SO2","CO","CO2","O3","NOx","NO","NO2","THC","NMHC","CH4","WIND_SPEED","WS_HR","AMB_TEMP","RAIN_INT","PH_RAIN","RH","RAIN_COND","AVG_TEMP","CUMULATIVE_RAINFALL","RAINY_DAYS","SUNSHINE_HOURS"};
	
	
	public List<OriginalData> createList() {
		List<OriginalData> list = new ArrayList<OriginalData>();
		return list;
	}

	
	public double SLOPE_ONLY(double[] y, double[] x,int rounding_off_place) {//SLOPE(known_y's, known_x's) ) 
		Final_project Fp =  new Final_project();
		double sumY=0;
		double sumX=0;
		
		for(int i=0;i<y.length;i++) {
			sumY=sumY+y[i];
		}
		for(int i=0;i<x.length;i++) {
			sumX=sumX+x[i];
		}
		
		if(sumY==0||sumX==0) {//return 0 if all data equal 0
			return 0;
		}
		
		double avgY=sumY/(double)y.length;
		double avgX=sumX/(double)x.length;
		double Molecule=0;//up
		double Denominator=0;//bottom
		for(int i=0;i<y.length;i++) {
			Molecule=Molecule+((x[i]-avgX)*(y[i]-avgY));
			Denominator=Denominator+Math.pow((x[i]-avgX), 2);
		}	
		return Fp.rounding(Molecule/Denominator ,rounding_off_place);
	}
	
	public void extensionCenter(String fileOutput, List<OriginalData> originalData, int rounding_off_place, List bigRealData) throws IOException, RowsExceededException, WriteException {
		String allYearRoute=fileOutput+"/10 Year";
		File dir_file = new File(allYearRoute);
		dir_file.mkdir();
		
		String allYearRSQRoute=allYearRoute+"/RSQ";
		File dir_file1 = new File(allYearRSQRoute);
		dir_file1.mkdir();
		
		allYear_regression_analysis(allYearRoute,originalData,rounding_off_place);
		
		allCORREL(allYearRSQRoute,rounding_off_place,bigRealData);
		
		normality_test(fileOutput,originalData,rounding_off_place);
		
	}
	
	public void normality_test(String fileOutput, List originalData, int rounding_off_place) throws IOException {
		Final_project Fp =  new Final_project();
		String ntRoute=fileOutput+"/Normality test";
		File dir_file = new File(ntRoute);
		dir_file.mkdir();
		
		for(int a=0;a<originalData.size();a++) {
			List<OriginalData> origin = (List<OriginalData>) originalData.get(a);
			for(int i=0;i<origin.size();i++) {
				OriginalData ori = origin.get(i);
				String innerRoute=ntRoute+"/"+ori.getLocation();
				File dir_file1 = new File(innerRoute);
				dir_file1.mkdir();
				for(int j=0;j<Extension.month.length;j++) {
					WritableWorkbook book = Workbook.createWorkbook(new File(innerRoute + "/"+Extension.month[j]+".xls"));
					for(int k=0;k<Extension.elements.length;k++) {
						WritableSheet sheet = book.createSheet(Extension.elements[k],k);
						double[] current = listData(Extension.elements[k],ori);
						double avg = avgMonth(current, ori.getDate(), Extension.month[j], rounding_off_place);
						double stdev = stdevMonth(current, ori.getDate(), Extension.month[j], rounding_off_place);
						/*System.out.println("avg "+avg);
						System.out.println("stdev "+stdev);
						try {
							TimeUnit.SECONDS.sleep(2);
						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}*/
						jxl.write.Label DATA1 = new jxl.write.Label(1,0,"n3-n2");//column row
						jxl.write.Label DATA2 = new jxl.write.Label(2,0,"n2-n1");//column row
						jxl.write.Label DATA3 = new jxl.write.Label(3,0,"n1-0");//column row
						jxl.write.Label DATA4 = new jxl.write.Label(4,0,"0-1");//column row
						jxl.write.Label DATA5 = new jxl.write.Label(5,0,"1-2");//column row
						jxl.write.Label DATA6 = new jxl.write.Label(6,0,"2-3");//column row
						jxl.write.Label DATA7 = new jxl.write.Label(0,1,"count");//column row
						jxl.write.Label DATA8 = new jxl.write.Label(0,2,"percentage");//column row
						
						
						jxl.write.Number DATA11 = new jxl.write.Number(1,1,dataCounter(current,avg-3*stdev,avg-2*stdev));//column row
						jxl.write.Number DATA12 = new jxl.write.Number(2,1,dataCounter(current,avg-2*stdev,avg-1*stdev));//column row
						jxl.write.Number DATA13 = new jxl.write.Number(3,1,dataCounter(current,avg-1*stdev,avg));//column row
						jxl.write.Number DATA14 = new jxl.write.Number(4,1,dataCounter(current,avg,avg+1*stdev));//column row
						jxl.write.Number DATA15 = new jxl.write.Number(5,1,dataCounter(current,avg+1*stdev,avg+2*stdev));//column row
						jxl.write.Number DATA16 = new jxl.write.Number(6,1,dataCounter(current,avg+2*stdev,avg+3*stdev));//column row
						
						jxl.write.Number DATA21 = new jxl.write.Number(1,2,100*Fp.rounding((double)dataCounter(current,avg-3*stdev,avg-2*stdev)/(double)dataCounter_nonZero(current), rounding_off_place+2));//column row
						jxl.write.Number DATA22 = new jxl.write.Number(2,2,100*Fp.rounding((double)dataCounter(current,avg-2*stdev,avg-1*stdev)/(double)dataCounter_nonZero(current), rounding_off_place+2));//column row
						jxl.write.Number DATA23 = new jxl.write.Number(3,2,100*Fp.rounding((double)dataCounter(current,avg-1*stdev,avg)/(double)dataCounter_nonZero(current), rounding_off_place+2));//column row
						jxl.write.Number DATA24 = new jxl.write.Number(4,2,100*Fp.rounding((double)dataCounter(current,avg,avg+1*stdev)/(double)dataCounter_nonZero(current), rounding_off_place+2));//column row
						jxl.write.Number DATA25 = new jxl.write.Number(5,2,100*Fp.rounding((double)dataCounter(current,avg+1*stdev,avg+2*stdev)/(double)dataCounter_nonZero(current), rounding_off_place+2));//column row
						jxl.write.Number DATA26 = new jxl.write.Number(6,2,100*Fp.rounding((double)dataCounter(current,avg+2*stdev,avg+3*stdev)/(double)dataCounter_nonZero(current), rounding_off_place+2));//column row
						
						jxl.write.Label DATA31 = new jxl.write.Label(7,4,"Data amount");//column row
						jxl.write.Number DATA32 = new jxl.write.Number(8,4,dataCounter_nonZero(current));//column row
						
						try {
							sheet.addCell(DATA1);
							sheet.addCell(DATA2);
							sheet.addCell(DATA3);
							sheet.addCell(DATA4);
							sheet.addCell(DATA5);
							sheet.addCell(DATA6);
							sheet.addCell(DATA7);
							sheet.addCell(DATA8);
							
							sheet.addCell(DATA11);
							sheet.addCell(DATA12);
							sheet.addCell(DATA13);
							sheet.addCell(DATA14);
							sheet.addCell(DATA15);
							sheet.addCell(DATA16);
							
							sheet.addCell(DATA21);
							sheet.addCell(DATA22);
							sheet.addCell(DATA23);
							sheet.addCell(DATA24);
							sheet.addCell(DATA25);
							sheet.addCell(DATA26);
							
							sheet.addCell(DATA31);
							sheet.addCell(DATA32);
							
						} catch (WriteException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					book.write();
					try {
						book.close();
					} catch (WriteException | IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
			}
		}
		
		
	}
	
	public void allYear_regression_analysis(String newFileRoute, List originalData, int rounding_off_place) throws IOException, RowsExceededException, WriteException {//for PM10 & PM25
		Final_project Fp =  new Final_project();
		WritableWorkbook allYearBookRegressionAnalysis = Workbook.createWorkbook(new File(newFileRoute + "/"+ "All_Year_Regression_Line_SLOPE.xls"));
		WritableSheet PM10BookRegressionAnalysisOutput = allYearBookRegressionAnalysis.createSheet("PM10",0);//Monthly y=ax+b
		WritableSheet PM25BookRegressionAnalysisOutput = allYearBookRegressionAnalysis.createSheet("PM25",1);//Monthly y=ax+b
		
		
		for(int j=0;j<originalData.size();j++) {//location counter
			double[] years = new double[20];
			double[] dPM10 = new double[20];
			double[] dPM25 = new double[20];
			List<OriginalData> ori = (List<OriginalData>) originalData.get(j);
			for(int k=0;k<ori.size();k++) {
				OriginalData orData = ori.get(k);
				for(int i=0;i<Extension.month.length;i++) {//month
					//CL, PreviousCL, DataRow, dataCounter reset to default
					int dataCounter=0;
					int CL=0;
					int PreviousCL=0;
					
					boolean ifexist=false;
					String OutputDate12=Extension.month[i];
					String[] DATE;
					DATE = orData.getDate();
					/*for(int l=0;l<orData.getDate().length;l++)	{//check whether the selecting month exist
						if(DATE[l].indexOf(OutputDate12)>=0) {
							ifexist=true;
							break;
						}
					}*/
					if(true)	{
						//OutMonthFirst,OutYearFirst reset to default
						String OutMonthFirst = StringUtils.substringBefore(DATE[0], " ");
						String OutYearFirst = DATE[0].substring(DATE[0].length()-4, DATE[0].length());
						
						
						
						while(true) {//calculate for the beginning and the ending of the month 
							if(CL>=orData.getContainLocation()-1) {
								break;
							}
							
							CL=Fp.DATE_CALCULATION(CL, orData.getContainLocation(), OutMonthFirst, OutYearFirst, DATE);
							
							
							
							OutMonthFirst = StringUtils.substringBefore(DATE[CL], " ");
							OutYearFirst = DATE[CL].substring(DATE[CL].length()-4, DATE[CL].length());
							if(DATE[PreviousCL].indexOf(OutputDate12)>=0) {
								String OutputDate = DATE[PreviousCL].substring(DATE[PreviousCL].length()-4, DATE[PreviousCL].length());
								
								dPM10[dataCounter]=Fp.AVERAGE_INT(orData.getPM10(),CL,PreviousCL,rounding_off_place);
								dPM25[dataCounter]=Fp.AVERAGE_INT(orData.getPM25(),CL,PreviousCL,rounding_off_place);
								years[dataCounter] = Double.valueOf(OutputDate);
								dataCounter++;
							}
							PreviousCL=CL;
						}
						
						dPM10 = Fp.double_AmountReduction(dPM10, dataCounter);
						dPM25 = Fp.double_AmountReduction(dPM25, dataCounter);
						years = Fp.double_AmountReduction(years, dataCounter);
						double slopePM10 = SLOPE_ONLY(dPM10,years,rounding_off_place);
						double slopePM25 = SLOPE_ONLY(dPM25,years,rounding_off_place);
						
						writeExcelForRLS(PM10BookRegressionAnalysisOutput, i+1, j+1, slopePM10);//DATA8
						jxl.write.Label DATA1 = new jxl.write.Label(i+1,0,month[i]);//column row
						//jxl.write.Number DATA8 = new jxl.write.Number(i+1,j+1,slopePM10);
						jxl.write.Label DATA11 = new jxl.write.Label(0, j+1,orData.getLocation());
						
						writeExcelForRLS(PM25BookRegressionAnalysisOutput, i+1, j+1, slopePM25);//DATA9
						jxl.write.Label DATA2 = new jxl.write.Label(i+1,0,month[i]);//column row
						//jxl.write.Number DATA9 = new jxl.write.Number(i+1,j+1,slopePM25);
						jxl.write.Label DATA12 = new jxl.write.Label(0, j+1,orData.getLocation());
						
						PM10BookRegressionAnalysisOutput.addCell(DATA1);
						//PM10BookRegressionAnalysisOutput.addCell(DATA8);
						PM10BookRegressionAnalysisOutput.addCell(DATA11);
						
						PM25BookRegressionAnalysisOutput.addCell(DATA2);
						//PM25BookRegressionAnalysisOutput.addCell(DATA9);
						PM25BookRegressionAnalysisOutput.addCell(DATA12);
					}
				}
			}
		}
		allYearBookRegressionAnalysis.write();
		allYearBookRegressionAnalysis.close();
	}
	public void allCORREL(String allYearRSQRoute, int rounding_off_place, List bigRealData) throws IOException, WriteException {
		DataRelevance_Non_Standardization dns = new DataRelevance_Non_Standardization();
		String[] currentType= {"PM10","PM2.5","R2","a","b","SO2","CO","CO2","O3","NOx","NO","NO2","THC","NMHC","CH4","WIND_SPEED","WS_HR","AMB_TEMP","RAIN_INT","PH_RAIN","RH","RAIN_COND","AVG_TEMP","CUMULATIVE_RAINFALL"
        		,"RAINY_DAYS","SUNSHINE_HOURS"};
		
		for(int i=0;i<bigRealData.size();i++) {//location define
			OuterCompare outC =  (OuterCompare) bigRealData.get(i);
			List<Compare> realData = outC.getList();
			WritableWorkbook book = Workbook.createWorkbook(new File(allYearRSQRoute + "/"+outC.getLocation()+".xls"));
			int sheetCounter=0;
			for(int m=0;m<currentType.length;m++) {//sheet
			   	if(currentType[m].equals("PM10")) {
			  		readExcelData_REVERSED(realData, book, currentType, currentType[m],sheetCounter);
			  		sheetCounter++;
			   	}
			   	if(currentType[m].equals("PM2.5")) {
			 		readExcelData_REVERSED(realData, book, currentType, currentType[m],sheetCounter);
			   		sheetCounter++;
			   	}
			   	if(currentType[m].equals("R2")) {
			   		readExcelData_REVERSED(realData, book, currentType, currentType[m],sheetCounter);
			   		sheetCounter++;
			   	}
			  	if(currentType[m].equals("a")) {
			   		readExcelData_REVERSED(realData, book, currentType, currentType[m],sheetCounter);
			   		sheetCounter++;
			   	}
			   	if(currentType[m].equals("b")) {
			   		readExcelData_REVERSED(realData, book, currentType, currentType[m],sheetCounter);
			   		sheetCounter++;
			   	}
			   	if(currentType[m].equals("SO2")) {
			   		readExcelData_REVERSED(realData, book, currentType, currentType[m],sheetCounter);
			   		sheetCounter++;
			   	}
			   	if(currentType[m].equals("CO")) {
			   		readExcelData_REVERSED(realData, book, currentType, currentType[m],sheetCounter);
			   		sheetCounter++;
			   	}
			   	if(currentType[m].equals("CO2")) {
			   		readExcelData_REVERSED(realData, book, currentType, currentType[m],sheetCounter);
			   		sheetCounter++;
			   	}
			   	if(currentType[m].equals("O3")) {
			   		readExcelData_REVERSED(realData, book, currentType, currentType[m],sheetCounter);
			   		sheetCounter++;
			   	}
			   	if(currentType[m].equals("NOx")) {
			   		readExcelData_REVERSED(realData, book, currentType, currentType[m],sheetCounter);
			   		sheetCounter++;
			   	}
			   	if(currentType[m].equals("NO")) {
			   		readExcelData_REVERSED(realData, book, currentType, currentType[m],sheetCounter);
			   		sheetCounter++;
			   	}
			   	if(currentType[m].equals("NO2")) {
			   		readExcelData_REVERSED(realData, book, currentType, currentType[m],sheetCounter);
			   		sheetCounter++;
			   	}
			   	if(currentType[m].equals("THC")) {
			   		readExcelData_REVERSED(realData, book, currentType, currentType[m],sheetCounter);
			   		sheetCounter++;
			  	}
			   	if(currentType[m].equals("NMHC")) {
			   		readExcelData_REVERSED(realData, book, currentType, currentType[m],sheetCounter);
			   		sheetCounter++;
			   	}
			   	if(currentType[m].equals("CH4")) {
			   		readExcelData_REVERSED(realData, book, currentType, currentType[m],sheetCounter);
			   		sheetCounter++;
			   	}
			   	if(currentType[m].equals("WIND_SPEED")) {
			   		readExcelData_REVERSED(realData, book, currentType, currentType[m],sheetCounter);
			   		sheetCounter++;
			   	}
			   	if(currentType[m].equals("WS_HR")) {
			   		readExcelData_REVERSED(realData, book, currentType, currentType[m],sheetCounter);
			   		sheetCounter++;
			  	}
			   	if(currentType[m].equals("AMB_TEMP")) {
			   		readExcelData_REVERSED(realData, book, currentType, currentType[m],sheetCounter);
			   		sheetCounter++;
			   	}
			    if(currentType[m].equals("RAIN_INT")) {
			    	readExcelData_REVERSED(realData, book, currentType, currentType[m],sheetCounter);
			    	sheetCounter++;
			    }
			    if(currentType[m].equals("PH_RAIN")) {
			    	readExcelData_REVERSED(realData, book, currentType, currentType[m],sheetCounter);
			    	sheetCounter++;
			    }
			    if(currentType[m].equals("RH")) {
			    	readExcelData_REVERSED(realData, book, currentType, currentType[m],sheetCounter);
			    	sheetCounter++;
			    }
			    if(currentType[m].equals("RAIN_COND")) {
			    	readExcelData_REVERSED(realData, book, currentType, currentType[m],sheetCounter);
			    	sheetCounter++;
			    }
			    if(currentType[m].equals("AVG_TEMP")) {
			    	readExcelData_REVERSED(realData, book, currentType, currentType[m],sheetCounter);
			    	sheetCounter++;
			    }
			    if(currentType[m].equals("CUMULATIVE_RAINFALL")) {
			    	readExcelData_REVERSED(realData, book, currentType, currentType[m],sheetCounter);
			    	sheetCounter++;
			    }
			    if(currentType[m].equals("RAINY_DAYS")) {
			    	readExcelData_REVERSED(realData, book, currentType, currentType[m],sheetCounter);
			    	sheetCounter++;
			    }
			    if(currentType[m].equals("SUNSHINE_HOURS")) {
			    	readExcelData_REVERSED(realData, book, currentType, currentType[m],sheetCounter);
			    	sheetCounter++;
			    }
			}
			book.write();
			book.close();
		}
	}
	public void readExcelData_REVERSED(List realData, WritableWorkbook book, String[] compareType, String currentType, int sheetCounter) throws WriteException {
		Final_project Fp = new Final_project();
		DataRelevance_Non_Standardization dns = new DataRelevance_Non_Standardization();
		WritableSheet sheet = book.createSheet(currentType, sheetCounter);
		int currentRow=1;
		for(int i=0;i<realData.size();i++) {
        	List<Compare> month = (List<Compare>) realData.get(i);
        	for(int j=0;j<month.size();j++) {
        		Compare com = month.get(j);
        		int currentColumn=0;
        		do {
        			jxl.write.Label label = new jxl.write.Label(currentRow,currentColumn,com.getMonth());
        			currentColumn++;
        			jxl.write.Label label1 = new jxl.write.Label(currentRow,currentColumn,currentType);
        			currentColumn++;
        			sheet.addCell(label);
        			sheet.addCell(label1);
        		}while(false);
        		for(int k=0;k<compareType.length;k++) { //for sheet
        			if(!compareType[k].equals(currentType)) {
        				double currentData[]= {0};
        				currentData=dns.listData(compareType[k],com);
        				int zeroAmount=0;
        				for(int l=0;l<currentData.length;l++) {
        					if(currentData[l]==0) {
        						zeroAmount++;
        					}
        				}
        				if(zeroAmount<=5&&currentData.length!=1) {//if amount of "0" greater than 5 or data doesn't exist, abandon this type of data
        					if(currentData.length!=dns.listData(currentType,com).length) {
            					jxl.write.Label label = new jxl.write.Label(currentRow,currentColumn, "# N/A");
            					jxl.write.Label label1 = new jxl.write.Label(0,0,compareType[k]);
            					sheet.addCell(label);
            					sheet.addCell(label1);
            					currentColumn++;
            				}else {
            					dns.writeExcel(sheet, currentRow, currentColumn, Fp.CORREL(dns.listData(currentType,com), currentData));
            					jxl.write.Label label = new jxl.write.Label(0,currentColumn,compareType[k]);
            					sheet.addCell(label);
            					currentColumn++;
            				}
        					
        				}else if(currentData.length==1) {
        					jxl.write.Label label = new jxl.write.Label(currentRow,currentColumn, "NON-EXIST");
        					jxl.write.Label label1 = new jxl.write.Label(0,currentColumn,compareType[k]);
        					sheet.addCell(label);
        					sheet.addCell(label1);
        					currentColumn++;
        				}else if(zeroAmount>5) {
        					jxl.write.Label label = new jxl.write.Label(currentRow,currentColumn, "INVALID");
        					jxl.write.Label label1 = new jxl.write.Label(0,currentColumn,compareType[k]);
        					sheet.addCell(label);
        					sheet.addCell(label1);
        					currentColumn++;
        				}     
        			}     			
        		}
        		currentRow++;
        	}
        }
	}//end
	public void writeExcelForRLS(WritableSheet sheet, int currentColumn, int currentRow, double outputData) throws WriteException {//regression line slope
		WritableCellFormat red = new WritableCellFormat();
        red.setBackground(Colour.ROSE);
        
        WritableCellFormat purple = new WritableCellFormat();
        purple.setBackground(Colour.LAVENDER);
        

        
        if(outputData>=0) {
			jxl.write.Number number = new jxl.write.Number(currentColumn,currentRow,outputData);
			number.setCellFormat(red);
			sheet.addCell(number);
		}else {
			jxl.write.Number number = new jxl.write.Number(currentColumn,currentRow,outputData);
			number.setCellFormat(purple);
			sheet.addCell(number);
		}
	}//end
	
	public double[] listData(String currentType, OriginalData org) {
		if(currentType.equals("SO2")) {
			return org.getSO2();
		}else if(currentType.equals("CO")) {
			return org.getCO();
		}else if(currentType.equals("CO2")) {
			return org.getCO2();
		}else if(currentType.equals("O3")) {
			return org.getO3();
		}else if(currentType.equals("PM10")) {
			return int_to_double(org.getPM10());
		}else if(currentType.equals("PM25")) {
			return int_to_double(org.getPM25());
		}else if(currentType.equals("NOx")) {
			return org.getNOx();
		}else if(currentType.equals("NO")) {
			return org.getNO();
		}else if(currentType.equals("NO2")) {
			return org.getNO2();
		}else if(currentType.equals("THC")) {
			return org.getTHC();
		}else if(currentType.equals("NMHC")) {
			return org.getNMHC();
		}else if(currentType.equals("CH4")) {
			return org.getCH4();
		}else if(currentType.equals("WIND_SPEED")) {
			return org.getWIND_SPEED();
		}else if(currentType.equals("WS_HR")) {
			return org.getWS_HR();
		}else if(currentType.equals("AMB_TEMP")) {
			return org.getAMB_TEMP();
		}else if(currentType.equals("RAIN_INT")) {
			return org.getRAIN_INT();
		}else if(currentType.equals("PH_RAIN")) {
			return org.getPH_RAIN();
		}else if(currentType.equals("RH")) {
			return org.getRH();
		}else if(currentType.equals("RAIN_COND")) {
			return org.getRAIN_COND();
		}else if(currentType.equals("AVG_TEMP")) {
			return org.getAVG_TEMP();
		}else if(currentType.equals("CUMULATIVE_RAINFALL")) {
			return org.getCUMULATIVE_RAINFALL();
		}else if(currentType.equals("RAINY_DAYS")) {
			return org.getRAINY_DAYS();
		}else if(currentType.equals("SUNSHINE_HOURS")) {
			return org.getSUNSHINE_HOURS();
		}
		return null;
	}//end
	
	
	public double[] int_to_double(int[] input) {
		double[] output = new double[input.length];
		for(int i=0;i<output.length;i++) {
			output[i] = Double.valueOf(input[i]);
		}
		return output;
	}//end
	
	public double avgMonth(double[] data, String[] date, String month, int rounding_off_place) {
		Final_project Fp = new Final_project();
		double counter=0;
		double current=0;
		for(int i=0;i<data.length;i++) {
			if(date[i].indexOf(month)>=0&&data[i]!=0) {
				current = current+data[i];
				counter++;
			}
		}
		if(counter==0) {
			return -1;
		}
		return Fp.rounding(current/counter, rounding_off_place);
	}
	
	public double stdevMonth(double[] data, String[] date, String month, int rounding_off_place) {
		Final_project Fp = new Final_project();
		double counter=0;
		double current=0;
		
		for(int i=0;i<data.length;i++) {
			if(date[i].indexOf(month)>=0&&data[i]!=0) {
				current = current+data[i];
				counter++;
			}
		}
		
		
		if(counter==0) {//return 0 if all data equal 0
			return -1;
		}
		
		double AverageInteger=current/counter;
		
		//each element minus the average then add them together
		double Molecule=0;//up
		for(int i=0;i<data.length;i++) {
			if(date[i].indexOf(month)>=0&&data[i]!=0) {
				Molecule=Molecule+Math.pow(AverageInteger-data[i], 2);
			}
		}
		return Fp.rounding(Math.pow(Molecule/(counter-1), 0.5) ,rounding_off_place);
		
	}
	
	public int dataCounter(double[] data, double buttom, double top) {
		int counter=0;
		for(int i=0;i<data.length;i++) {
			if(data[i]<top&&data[i]>=buttom&&data[i]!=0) {
				counter++;
			}
		}
		return counter;
	}//end
	
	public int dataCounter_nonZero(double[] data) {
		int counter=0;
		for(int i=0;i<data.length;i++) {
			if(data[i]!=0) {
				counter++;
			}
		}
		return counter;
	}
	
}//class end
