package final_project;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import jxl.Sheet;
import jxl.Workbook;
import jxl.format.Colour;
import jxl.read.biff.BiffException;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

public class DataRelevance_Non_Standardization {
	
	
	public void readExternalData(List<Compare> realData, String externalDataRoute, String location, String[] year, OriginalData od) throws BiffException, IOException, InterruptedException {
		Final_project Fp =  new Final_project();
		File file = new File(externalDataRoute);
		String month[]= {"December", "November", "October", "September", "August", "July", "June", "May", "April", "March", "February", "January"};
		double[] AVG_TEMP;
		double[] CUMULATIVE_RAINFALL;
		double[] RAINY_DAYS;
		double[] SUNSHINE_HOURS;
		
		AVG_TEMP=new double[20];
		CUMULATIVE_RAINFALL=new double[20];
		RAINY_DAYS=new double[20];
		SUNSHINE_HOURS=new double[20];
		
		int dataCounter;
		if(file.isDirectory()) {
			int readCounter=1;
			File[] files = file.listFiles();
			for(int i=0;i<month.length;i++) {
				dataCounter=0;
				for(int l=0;l<year.length;l++) {
					for(File f:files) {
						if(f.getName().indexOf(".xls")>0) {							
							if(f.getName().indexOf(year[l])>=0) {
								System.out.println(location+"   "+month[i]+" "+f.getName().replace(".xls", "")+"\t"+readCounter+"/"+month.length*year.length);
								readCounter++;
								InputStream is = new FileInputStream(f.getAbsolutePath());
								Workbook wb = Workbook.getWorkbook(is);
								
								for(int j=0;j<wb.getNumberOfSheets();j++) {
									Sheet Insheet = wb.getSheet(j);
									String sheetName=Insheet.getName();
									if(sheetName.indexOf(month[i])>=0) {
										String dataName[];
										dataName = new String[Insheet.getColumns()];
										String[] locationName;
										locationName=new String[Insheet.getRows()];
										for(int k=0;k<Insheet.getColumns();k++) {
											dataName[k]=Insheet.getCell(k,0).getContents();//getCell(Columns,Rows)
										}
										for(int k=0;k<Insheet.getRows();k++) {
											locationName[k]=Insheet.getCell(0,k).getContents();//getCell(Columns,Rows)
										}
										
										for(int m=0;m<Insheet.getRows();m++) {
											if(locationName[m].indexOf(location)>=0) {
												for(int n=0;n<Insheet.getColumns();n++) {
													if(dataName[n].indexOf("AVG_TEMP")>=0) {
														AVG_TEMP[dataCounter]=Double.valueOf(Insheet.getCell(n,m).getContents()).doubleValue();//getCell(Columns,Rows)
													}
													if(dataName[n].indexOf("CUMULATIVE_RAINFALL")>=0){
														CUMULATIVE_RAINFALL[dataCounter]=Double.valueOf(Insheet.getCell(n,m).getContents()).doubleValue();//getCell(Columns,Rows)
													}
													if(dataName[n].indexOf("RAINY_DAYS")>=0||dataName[n].indexOf("RAIN_DAYS")>=0){//may have to modify
														RAINY_DAYS[dataCounter]=Double.valueOf(Insheet.getCell(n,m).getContents()).doubleValue();//getCell(Columns,Rows)
													}
													if(dataName[n].indexOf("SUNSHINE_HOURS")>=0){
														SUNSHINE_HOURS[dataCounter]=Double.valueOf(Insheet.getCell(n,m).getContents()).doubleValue();//getCell(Columns,Rows)
													}
												}
											}
										}			
									}
								}
							}	
						}
						
					}
					dataCounter++;
				}
				
				od.setAVG_TEMP(Fp.double_AmountReduction(AVG_TEMP,dataCounter));
				od.setCUMULATIVE_RAINFALL(Fp.double_AmountReduction(CUMULATIVE_RAINFALL,dataCounter));
				od.setRAINY_DAYS(Fp.double_AmountReduction(RAINY_DAYS,dataCounter));
				od.setSUNSHINE_HOURS(Fp.double_AmountReduction(SUNSHINE_HOURS,dataCounter));
				
				for(int j=0;j<realData.size();j++) {
					List<Compare> listMonth = (List<Compare>) realData.get(j);
					for(int k=0;k<listMonth.size();k++) {
						Compare com = listMonth.get(k);
						if(com.getMonth().equals(month[i])) {
							com.setAVG_TEMP(Fp.double_AmountReduction(AVG_TEMP,dataCounter));
							com.setCUMULATIVE_RAINFALL(Fp.double_AmountReduction(CUMULATIVE_RAINFALL,dataCounter));
							com.setRAINY_DAYS(Fp.double_AmountReduction(RAINY_DAYS,dataCounter));
							com.setSUNSHINE_HOURS(Fp.double_AmountReduction(SUNSHINE_HOURS,dataCounter));
						}
					}
				}	
			}
		}else {
			System.out.println("EXTERNAL DATA ROUTE MUST BE A FOLDER");
			TimeUnit.SECONDS.sleep(5);//delay for 5 seconds
		}
	}//end
	
	
	public List createYear() {
		DataRelevance_Non_Standardization dns = new DataRelevance_Non_Standardization();
		List year = new ArrayList();
		List december = dns.createPosition("December");
		year.add(december);
		List november = dns.createPosition("November");
		year.add(november);
		List october = dns.createPosition("October");
		year.add(october);
		List september = dns.createPosition("September");
		year.add(september);
		List august = dns.createPosition("August");
		year.add(august);
		List july = dns.createPosition("July");;
		year.add(july);
		List june = dns.createPosition("June");
		year.add(june);
		List may = dns.createPosition("May");
		year.add(may);
		List april = dns.createPosition("April");
		year.add(april);
		List march = dns.createPosition("March");
		year.add(march);
		List february =dns.createPosition("February");
		year.add(february);
		List january = dns.createPosition("January");
		year.add(january);
		return year;
	}//end
	
	public List createPosition (String month) {
		List list = new ArrayList<Compare>();
		Compare com = new Compare();
		com.setMonth(month);
		list.add(com);
		return list;
	}//end
	
	public void creatExcel(List<Compare> realData, String nonStandardizationRoute, String fileName) throws IOException, WriteException{
		Final_project Fp = new Final_project();
		DataRelevance_Non_Standardization dns = new DataRelevance_Non_Standardization();
		
		WritableWorkbook book = Workbook.createWorkbook(new File(nonStandardizationRoute + "/"+ fileName+".xls"));
		
		// WritableSheet PM10sheet = book.createSheet("PM10", 0);
		// WritableSheet PM25sheet = book.createSheet("PM2.5", 1);
		
        /*String[] currentTypePM10= {"PM2.5","R2","a","b","SO2","CO","CO2","O3","NOx","NO"
        		,"NO2","THC","NMHC","CH4","WIND_SPEED","WS_HR","AMB_TEMP","RAIN_INT","PH_RAIN","RH","RAIN_COND"};*/
		
        String[] currentType= {"PM10","PM2.5","R2","a","b","SO2","CO","CO2","O3","NOx","NO"
        		,"NO2","THC","NMHC","CH4","WIND_SPEED","WS_HR","AMB_TEMP","RAIN_INT","PH_RAIN","RH","RAIN_COND","AVG_TEMP","CUMULATIVE_RAINFALL"
        		,"RAINY_DAYS","SUNSHINE_HOURS"};
        int sheetCounter=0;
        for(int m=0;m<currentType.length;m++) {
        	
        	if(currentType[m].equals("PM10")) {
        		dns.readExcelData(realData, book, currentType, currentType[m],sheetCounter);
        		sheetCounter++;
        	}
        	if(currentType[m].equals("PM2.5")) {
        		dns.readExcelData(realData, book, currentType, currentType[m],sheetCounter);
        		sheetCounter++;
        	}
        	if(currentType[m].equals("R2")) {
        		dns.readExcelData(realData, book, currentType, currentType[m],sheetCounter);
        		sheetCounter++;
        	}
        	if(currentType[m].equals("a")) {
        		dns.readExcelData(realData, book, currentType, currentType[m],sheetCounter);
        		sheetCounter++;
        	}
        	if(currentType[m].equals("b")) {
        		dns.readExcelData(realData, book, currentType, currentType[m],sheetCounter);
        		sheetCounter++;
        	}
        	
        	if(currentType[m].equals("SO2")) {
        		dns.readExcelData(realData, book, currentType, currentType[m],sheetCounter);
        		sheetCounter++;
        	}
        	if(currentType[m].equals("CO")) {
        		dns.readExcelData(realData, book, currentType, currentType[m],sheetCounter);
        		sheetCounter++;
        	}
        	if(currentType[m].equals("CO2")) {
        		dns.readExcelData(realData, book, currentType, currentType[m],sheetCounter);
        		sheetCounter++;
        	}
        	if(currentType[m].equals("O3")) {
        		dns.readExcelData(realData, book, currentType, currentType[m],sheetCounter);
        		sheetCounter++;
        	}
        	if(currentType[m].equals("NOx")) {
        		dns.readExcelData(realData, book, currentType, currentType[m],sheetCounter);
        		sheetCounter++;
        	}
        	if(currentType[m].equals("NO")) {
        		dns.readExcelData(realData, book, currentType, currentType[m],sheetCounter);
        		sheetCounter++;
        	}
        	if(currentType[m].equals("NO2")) {
        		dns.readExcelData(realData, book, currentType, currentType[m],sheetCounter);
        		sheetCounter++;
        	}
        	if(currentType[m].equals("THC")) {
        		dns.readExcelData(realData, book, currentType, currentType[m],sheetCounter);
        		sheetCounter++;
        	}
        	if(currentType[m].equals("NMHC")) {
        		dns.readExcelData(realData, book, currentType, currentType[m],sheetCounter);
        		sheetCounter++;
        	}
        	if(currentType[m].equals("CH4")) {
        		dns.readExcelData(realData, book, currentType, currentType[m],sheetCounter);
        		sheetCounter++;
        	}
        	if(currentType[m].equals("WIND_SPEED")) {
        		dns.readExcelData(realData, book, currentType, currentType[m],sheetCounter);
        		sheetCounter++;
        	}
        	if(currentType[m].equals("WS_HR")) {
        		dns.readExcelData(realData, book, currentType, currentType[m],sheetCounter);
        		sheetCounter++;
        	}
        	if(currentType[m].equals("AMB_TEMP")) {
        		dns.readExcelData(realData, book, currentType, currentType[m],sheetCounter);
        		sheetCounter++;
        	}
        	if(currentType[m].equals("RAIN_INT")) {
        		dns.readExcelData(realData, book, currentType, currentType[m],sheetCounter);
        		sheetCounter++;
        	}
        	if(currentType[m].equals("PH_RAIN")) {
        		dns.readExcelData(realData, book, currentType, currentType[m],sheetCounter);
        		sheetCounter++;
        	}
        	if(currentType[m].equals("RH")) {
        		dns.readExcelData(realData, book, currentType, currentType[m],sheetCounter);
        		sheetCounter++;
        	}
        	if(currentType[m].equals("RAIN_COND")) {
        		dns.readExcelData(realData, book, currentType, currentType[m],sheetCounter);
        		sheetCounter++;
        	}
        	if(currentType[m].equals("AVG_TEMP")) {
        		dns.readExcelData(realData, book, currentType, currentType[m],sheetCounter);
        		sheetCounter++;
        	}
        	if(currentType[m].equals("CUMULATIVE_RAINFALL")) {
        		dns.readExcelData(realData, book, currentType, currentType[m],sheetCounter);
        		sheetCounter++;
        	}
        	if(currentType[m].equals("RAINY_DAYS")) {
        		dns.readExcelData(realData, book, currentType, currentType[m],sheetCounter);
        		sheetCounter++;
        	}
        	if(currentType[m].equals("SUNSHINE_HOURS")) {
        		dns.readExcelData(realData, book, currentType, currentType[m],sheetCounter);
        		sheetCounter++;
        	}
        }
        /*
        		//for PM10 sheet
        		do {
        			currentColumn=0;
        			jxl.write.Label label = new jxl.write.Label(currentColumn,currentRow,com.getMonth());
        			currentColumn++;
        			jxl.write.Label label1 = new jxl.write.Label(currentColumn,currentRow,"PM10");
        			currentColumn++;
        			PM10sheet.addCell(label);
        			PM10sheet.addCell(label1);
        		}while(false);
        		for(int k=0;k<currentTypePM10.length;k++) { //for PM10 sheet	
        			double currentData[]= {0};
        			currentData=listData(currentTypePM10[k],com);
        			int zeroAmount=0;
        			for(int l=0;l<currentData.length;l++) {
        				if(currentData[l]==0) {
        					zeroAmount++;
        				}
        			}
        			if(zeroAmount<5&&currentData.length!=1) {//if amount of "0" greater than 5, abandon this type of data
        				writeExcel(PM10sheet, currentColumn, currentRow, Fp.CORREL(com.getPM10(), currentData));
        				jxl.write.Label label = new jxl.write.Label(currentColumn,0,currentTypePM10[k]);
        				PM10sheet.addCell(label);
        				currentColumn++;
        			}        			
        		}
        		
        		//for PM2.5 sheet
        		do {
        			currentColumn=0;
        			jxl.write.Label label = new jxl.write.Label(currentColumn,currentRow,com.getMonth());
        			currentColumn++;
        			jxl.write.Label label1 = new jxl.write.Label(currentColumn,currentRow,"PM2.5");
        			currentColumn++;
        			PM25sheet.addCell(label);
        			PM25sheet.addCell(label1);
        		}while(false);
        		
        		for(int k=0;k<currentTypePM25.length;k++) {// for PM2.5 sheet
        			double currentData[]= {0};
        			currentData=listData(currentTypePM25[k],com);
        			
        			int zeroAmount=0;
        			for(int l=0;l<currentData.length;l++) {
        				if(currentData[l]==0) {
        					zeroAmount++;
        				}
        			}
        			
        			if(zeroAmount<5&&currentData.length!=1) {//if amount of "0" greater than 5, abandon this type of data
        				writeExcel(PM25sheet, currentColumn, currentRow, Fp.CORREL(com.getPM25(), currentData));
        				jxl.write.Label label = new jxl.write.Label(currentColumn,0,currentTypePM25[k]);
        				PM25sheet.addCell(label);
        				currentColumn++;
        			}
        		}
        		currentRow++;
        		
        */
        
        book.write();
        book.close();
	}//end
	
	public void readExcelData(List realData, WritableWorkbook book, String[] compareType, String currentType, int sheetCounter) throws WriteException {
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
        			jxl.write.Label label = new jxl.write.Label(currentColumn,currentRow,com.getMonth());//column, row
        			currentColumn++;
        			jxl.write.Label label1 = new jxl.write.Label(currentColumn,currentRow,currentType);
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
            					jxl.write.Label label = new jxl.write.Label(currentColumn,currentRow, "# N/A");
            					jxl.write.Label label1 = new jxl.write.Label(currentColumn,0,compareType[k]);
            					sheet.addCell(label);
            					sheet.addCell(label1);
            					currentColumn++;
            				}else {
            					writeExcel(sheet, currentColumn, currentRow, Fp.CORREL(dns.listData(currentType,com), currentData));
            					jxl.write.Label label = new jxl.write.Label(currentColumn,0,compareType[k]);
            					sheet.addCell(label);
            					currentColumn++;
            				}
        					
        				}else if(currentData.length==1) {
        					jxl.write.Label label = new jxl.write.Label(currentColumn,currentRow, "NON-EXIST");
        					jxl.write.Label label1 = new jxl.write.Label(currentColumn,0,compareType[k]);
        					sheet.addCell(label);
        					sheet.addCell(label1);
        					currentColumn++;
        				}else if(zeroAmount>5) {
        					jxl.write.Label label = new jxl.write.Label(currentColumn,currentRow, "INVALID");
        					jxl.write.Label label1 = new jxl.write.Label(currentColumn,0,compareType[k]);
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
	
	public void writeExcel(WritableSheet sheet, int currentColumn, int currentRow, double outputData) throws WriteException {
		WritableCellFormat red = new WritableCellFormat();
        red.setBackground(Colour.RED);
        
        WritableCellFormat yellow = new WritableCellFormat();
        yellow.setBackground(Colour.YELLOW);
        
        WritableCellFormat green = new WritableCellFormat();
        green.setBackground(Colour.BRIGHT_GREEN);
        
        WritableCellFormat blue = new WritableCellFormat();
        blue.setBackground(Colour.SKY_BLUE);
        
        if(outputData>0.7) {
			jxl.write.Number number = new jxl.write.Number(currentColumn,currentRow,outputData);
			number.setCellFormat(red);
			sheet.addCell(number);
		}else if(outputData<=0.7&&outputData>=0.3) {
			jxl.write.Number number = new jxl.write.Number(currentColumn,currentRow,outputData);
			number.setCellFormat(yellow);
			sheet.addCell(number);
		}else if(outputData<=-0.3&&outputData>=-0.7) {
			jxl.write.Number number = new jxl.write.Number(currentColumn,currentRow,outputData);
			number.setCellFormat(green);
			sheet.addCell(number);
		}else if(outputData<-0.7) {
			jxl.write.Number number = new jxl.write.Number(currentColumn,currentRow,outputData);
			number.setCellFormat(blue);
			sheet.addCell(number);
		}else {
			jxl.write.Number number = new jxl.write.Number(currentColumn,currentRow,outputData);
			sheet.addCell(number);
		}
	}//end
	
	public double[] listData(String currentType, Compare com) {
		if(currentType.equals("SO2")) {
			return com.getSO2();
		}else if(currentType.equals("CO")) {
			return com.getCO();
		}else if(currentType.equals("CO2")) {
			return com.getCO2();
		}else if(currentType.equals("O3")) {
			return com.getO3();
		}else if(currentType.equals("PM10")) {
			return com.getPM10();
		}else if(currentType.equals("PM2.5")) {
			return com.getPM25();
		}else if(currentType.equals("NOx")) {
			return com.getNOx();
		}else if(currentType.equals("NO")) {
			return com.getNO();
		}else if(currentType.equals("NO2")) {
			return com.getNO2();
		}else if(currentType.equals("THC")) {
			return com.getTHC();
		}else if(currentType.equals("NMHC")) {
			return com.getNMHC();
		}else if(currentType.equals("CH4")) {
			return com.getCH4();
		}else if(currentType.equals("WIND_SPEED")) {
			return com.getWIND_SPEED();
		}else if(currentType.equals("WS_HR")) {
			return com.getWS_HR();
		}else if(currentType.equals("AMB_TEMP")) {
			return com.getAMB_TEMP();
		}else if(currentType.equals("RAIN_INT")) {
			return com.getRAIN_INT();
		}else if(currentType.equals("PH_RAIN")) {
			return com.getPH_RAIN();
		}else if(currentType.equals("RH")) {
			return com.getRH();
		}else if(currentType.equals("RAIN_COND")) {
			return com.getRAIN_COND();
		}else if(currentType.equals("R2")) {
			return com.getR2();
		}else if(currentType.equals("a")) {
			return com.getA();
		}else if(currentType.equals("b")) {
			return com.getB();
		}else if(currentType.equals("AVG_TEMP")) {
			return com.getAVG_TEMP();
		}else if(currentType.equals("CUMULATIVE_RAINFALL")) {
			return com.getCUMULATIVE_RAINFALL();
		}else if(currentType.equals("RAINY_DAYS")) {
			return com.getRAINY_DAYS();
		}else if(currentType.equals("SUNSHINE_HOURS")) {
			return com.getSUNSHINE_HOURS();
		}
		return null;
	}//end
	
}//class end