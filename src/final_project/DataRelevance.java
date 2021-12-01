package final_project;

import java.io.File;
import java.io.IOException;
import java.util.List;

import jxl.Workbook;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public class DataRelevance {
	Final_project Fp = new Final_project();
	
	public void readData(String FileOutput, List dataList) throws IOException, RowsExceededException, WriteException {
		System.out.println("\n\n\n----------DataRelevance Process Start----------\n\n\n");
		
		//create new folder
		String dataRoute=FileOutput+"/DATA_Relevance";
		File dir_file = new File(dataRoute);
		dir_file.mkdir();
		String[] currentType= {"SO2","CO","CO2","O3","PM10","PM2.5","NOx","NO","NO2","THC","NMHC","CH4","WIND_SPEED","WS_HR"
				,"AMB_TEMP","RAIN_INT","PH_RAIN","RH","RAIN_COND"};
		
		for(int i=0;i<currentType.length;i++) {
			int sheetCounter=0;
			WritableWorkbook DataRelevance = Workbook.createWorkbook(new File(dataRoute +"/"+ currentType[i]+ ".xls"));
			
			if(true) {
				writeExcel(DataRelevance, currentType[i], null, sheetCounter, dataList,1);//data info for itself
				sheetCounter++;
			}
			
			if(!currentType[i].equals("SO2")) {
				writeExcel(DataRelevance, currentType[i], "SO2", sheetCounter, dataList, 2);
				sheetCounter++;
			}
			if(!currentType[i].equals("CO")) {
				writeExcel(DataRelevance, currentType[i], "CO", sheetCounter, dataList, 2);
				sheetCounter++;
			}
			if(!currentType[i].equals("CO2")) {
				writeExcel(DataRelevance, currentType[i], "CO2", sheetCounter, dataList, 2);
				sheetCounter++;
			}
			if(!currentType[i].equals("O3")) {
				writeExcel(DataRelevance, currentType[i], "O3", sheetCounter, dataList, 2);
				sheetCounter++;
			}
			if(!currentType[i].equals("PM10")) {
				writeExcel(DataRelevance, currentType[i], "PM10", sheetCounter, dataList, 2);
				sheetCounter++;
			}
			if(!currentType[i].equals("PM2.5")) {
				writeExcel(DataRelevance, currentType[i], "PM2.5", sheetCounter, dataList, 2);
				sheetCounter++;
			}
			if(!currentType[i].equals("NOx")) {
				writeExcel(DataRelevance, currentType[i], "NOx", sheetCounter, dataList, 2);
				sheetCounter++;
			}
			if(!currentType[i].equals("NO")) {
				writeExcel(DataRelevance, currentType[i], "NO", sheetCounter, dataList, 2);
				sheetCounter++;
			}
			if(!currentType[i].equals("NO2")) {
				writeExcel(DataRelevance, currentType[i], "NO2", sheetCounter, dataList, 2);
				sheetCounter++;
			}
			if(!currentType[i].equals("THC")) {
				writeExcel(DataRelevance, currentType[i], "THC", sheetCounter, dataList, 2);
				sheetCounter++;
			}
			if(!currentType[i].equals("NMHC")) {
				writeExcel(DataRelevance, currentType[i], "NMHC", sheetCounter, dataList, 2);
				sheetCounter++;
			}
			if(!currentType[i].equals("CH4")) {
				writeExcel(DataRelevance, currentType[i], "CH4", sheetCounter, dataList, 2);
				sheetCounter++;
			}
			if(!currentType[i].equals("WIND_SPEED")) {
				writeExcel(DataRelevance, currentType[i], "WIND_SPEED", sheetCounter, dataList, 2);
				sheetCounter++;
			}
			if(!currentType[i].equals("WS_HR")) {
				writeExcel(DataRelevance, currentType[i], "WS_HR", sheetCounter, dataList, 2);
				sheetCounter++;
			}
			if(!currentType[i].equals("AMB_TEMP")) {
				writeExcel(DataRelevance, currentType[i], "AMB_TEMP", sheetCounter, dataList, 2);
				sheetCounter++;
			}
			if(!currentType[i].equals("RAIN_INT")) {
				writeExcel(DataRelevance, currentType[i], "RAIN_INT", sheetCounter, dataList, 2);
				sheetCounter++;
			}
			if(!currentType[i].equals("PH_RAIN")) {
				writeExcel(DataRelevance, currentType[i], "PH_RAIN", sheetCounter, dataList, 2);
				sheetCounter++;
			}
			if(!currentType[i].equals("RH")) {
				writeExcel(DataRelevance, currentType[i], "RH", sheetCounter, dataList, 2);
				sheetCounter++;
			}
			if(!currentType[i].equals("RAIN_COND")) {
				writeExcel(DataRelevance, currentType[i], "RAIN_COND", sheetCounter, dataList, 2);
				sheetCounter++;
			}
			
			
			DataRelevance.write();
			DataRelevance.close();
			System.gc();
			System.out.println(currentType[i]+ " Done\n");
		}
	}//end
	
	public void writeExcel(WritableWorkbook DataRelevance, String currentType, String compareType, int sheetCounter, List dataList ,int mode) 
			throws RowsExceededException, WriteException {
		if(mode==1) {
			WritableSheet sheet = DataRelevance.createSheet(currentType,sheetCounter);
			jxl.write.Label DATA1 = new jxl.write.Label(0,1, currentType+" AVERAGE above 2 STDEVs");//(column,row,data)
			jxl.write.Label DATA2 = new jxl.write.Label(0,2, currentType+" AVERAGE above 1 STDEV");
			jxl.write.Label DATA3 = new jxl.write.Label(0,3, currentType+" NORMAL");
			jxl.write.Label DATA4 = new jxl.write.Label(0,4, currentType+" AVERAGE below 1 STDEV");
			jxl.write.Label DATA5 = new jxl.write.Label(0,5, currentType+" AVERAGE below 2 STDEVs");
			jxl.write.Label DATA6 = new jxl.write.Label(3,5,"Unit: %");
			
			jxl.write.Label DATA14 = new jxl.write.Label(0,8,"DATA_AMOUNT");
			sheet.addCell(DATA1);
			sheet.addCell(DATA2);
			sheet.addCell(DATA3);
			sheet.addCell(DATA4);
			sheet.addCell(DATA5);
			sheet.addCell(DATA6);
			sheet.addCell(DATA14);
			double dataAmount=0;
			double matchedDataAmount=0;
			int readData[] = {3, 2, 1, -1, -2};
			int outputRow[] = {1, 2, 3, 4, 5};
			
			for(int i=0;i<readData.length;i++) {
				dataAmount=0;
				matchedDataAmount=0;
				
				//read entire list
				for(int j=0;j<dataList.size();j++) {
					List<Position> month = (List<Position>) dataList.get(j);
					for(int k=0;k<month.size();k++) {
						Position pos = month.get(k);
						
						// System.out.println(currentType);
						// System.out.println(listData(currentType,pos));
						// System.out.println(readData[i]);
						if(listData(currentType,pos)!=0) {
							dataAmount++;
							if(listData(currentType,pos)==readData[i]){
								matchedDataAmount++;
							}
						}
						
					}
				}
				// System.out.println(matchedDataAmount);
				if(dataAmount!=0) {
					jxl.write.Number DATA12 = new jxl.write.Number(1,outputRow[i],100*Fp.rounding(matchedDataAmount/dataAmount,4));
					sheet.addCell(DATA12);
				}else {
					jxl.write.Number DATA12 = new jxl.write.Number(1,outputRow[i],0);
					sheet.addCell(DATA12);
				}
				jxl.write.Number DATA13 = new jxl.write.Number(1,8,dataAmount);
				sheet.addCell(DATA13);
			}
			
		}
		if(mode==2) {
			WritableSheet sheet = DataRelevance.createSheet(compareType,sheetCounter);
			//average above 2 standard deviations
			jxl.write.Label DATA1 = new jxl.write.Label(0,1, currentType+" AVERAGE above 2 STDEVs");
			jxl.write.Label DATA2 = new jxl.write.Label(0,2, currentType+" AVERAGE above 1 STDEV");
			jxl.write.Label DATA3 = new jxl.write.Label(0,3, currentType+" NORMAL");
			jxl.write.Label DATA4 = new jxl.write.Label(0,4, currentType+" AVERAGE below 1 STDEV");
			jxl.write.Label DATA5 = new jxl.write.Label(0,5, currentType+" AVERAGE below 2 STDEVs");
			
			jxl.write.Label DATA6 = new jxl.write.Label(1,0, compareType+" AVERAGE above 2 STDEVs");
			jxl.write.Label DATA7 = new jxl.write.Label(2,0, compareType+" AVERAGE above 1 STDEV");
			jxl.write.Label DATA8 = new jxl.write.Label(3,0, compareType+" NORMAL");
			jxl.write.Label DATA9 = new jxl.write.Label(4,0, compareType+" AVERAGE below 1 STDEV");
			jxl.write.Label DATA10 = new jxl.write.Label(5,0, compareType +" AVERAGE below 2 STDEVs");
			jxl.write.Label DATA11 = new jxl.write.Label(7,5,"Unit: %");
			
			sheet.addCell(DATA1);
			sheet.addCell(DATA2);
			sheet.addCell(DATA3);
			sheet.addCell(DATA4);
			sheet.addCell(DATA5);
			sheet.addCell(DATA6);
			sheet.addCell(DATA7);
			sheet.addCell(DATA8);
			sheet.addCell(DATA9);
			sheet.addCell(DATA10);
			sheet.addCell(DATA11);
			
			double dataAmount = 0;
			double matchedDataAmount = 0;
			int readData[] = {3, 2, 1, -1, -2};
			int outputColumn[] = {1, 2, 3, 4, 5};
			int outputRow[] = {1, 2, 3, 4, 5};
			
			
			
			for(int jj=0;jj<outputRow.length;jj++) {
				for(int kk=0;kk<outputColumn.length;kk++) {//a cell
					//reset dataAmount, matchedDataAmount to 0
					dataAmount=0;
					matchedDataAmount=0;
					
					//read entire list
					for(int j=0;j<dataList.size();j++) {
						List<Position> month = (List<Position>) dataList.get(j);
						for(int k=0;k<month.size();k++) {
							Position pos = month.get(k);
							// System.out.println(currentType);
							// System.out.println(listData(currentType,pos));
							if(listData(currentType,pos)!=0) {
								if(listData(currentType,pos)==readData[jj]&&listData(compareType,pos)!=0){
									dataAmount++;
									if(listData(compareType,pos)==readData[kk]) {
										matchedDataAmount++;
									}
								}
							}
							
						}
					}
					if(dataAmount!=0) {
						jxl.write.Number DATA12 = new jxl.write.Number(outputColumn[kk],outputRow[jj],100*Fp.rounding(matchedDataAmount/dataAmount,4));
						//jxl.write.Label DATA13 = new jxl.write.Label(0,9,"Compared amount:");
						//jxl.write.Number DATA14 = new jxl.write.Number(1,9,dataAmount);
						sheet.addCell(DATA12);
						//sheet.addCell(DATA13);
						//sheet.addCell(DATA14);
					}else {
						jxl.write.Number DATA12 = new jxl.write.Number(outputColumn[kk],outputRow[jj],0);
						sheet.addCell(DATA12);
					}
					
				}
			}
		}
		System.gc();
	}//end
	
	public int listData(String currentType,Position pos) {
		if(currentType.indexOf("SO2")>=0) {
			return pos.getSO2();
		}else if(currentType.indexOf("CO")>=0) {
			return pos.getCO();
		}else if(currentType.indexOf("CO2")>=0) {
			return pos.getCO2();
		}else if(currentType.indexOf("O3")>=0) {
			return pos.getO3();
		}else if(currentType.indexOf("PM10")>=0) {
			return pos.getPM10();
		}else if(currentType.indexOf("PM2.5")>=0) {
			return pos.getPM25();
		}else if(currentType.indexOf("NOx")>=0) {
			return pos.getNOx();
		}else if(currentType.indexOf("NO")>=0) {
			return pos.getNO();
		}else if(currentType.indexOf("NO2")>=0) {
			return pos.getNO2();
		}else if(currentType.indexOf("THC")>=0) {
			return pos.getTHC();
		}else if(currentType.indexOf("NMHC")>=0) {
			return pos.getNMHC();
		}else if(currentType.indexOf("CH4")>=0) {
			return pos.getCH4();
		}else if(currentType.indexOf("WIND_SPEED")>=0) {
			return pos.getWIND_SPEED();
		}else if(currentType.indexOf("WS_HR")>=0) {
			return pos.getWS_HR();
		}else if(currentType.indexOf("AMB_TEMP")>=0) {
			return pos.getAMB_TEMP();
		}else if(currentType.indexOf("RAIN_INT")>=0) {
			return pos.getRAIN_INT();
		}else if(currentType.indexOf("PH_RAIN")>=0) {
			return pos.getPH_RAIN();
		}else if(currentType.indexOf("RH")>=0) {
			return pos.getRH();
		}else if(currentType.indexOf("RAIN_COND")>=0) {
			return pos.getRAIN_COND();
		}
		return 10;
	}//end
	
	public int listData2(String currentType,Position pos) {
		if(currentType.equals("PM10_SO2")) {
			return pos.getPM10_SO2();
		}else if(currentType.equals("PM10_CO")) {
			return pos.getPM10_CO();
		}else if(currentType.equals("PM10_O3")) {
			return pos.getPM10_O3();
		}else if(currentType.equals("PM10_NOx")) {
			return pos.getPM10_NOx();
		}else if(currentType.equals("PM10_THC")) {
			return pos.getPM10_THC();
		}else if(currentType.equals("PM25_SO2")) {
			return pos.getPM25_SO2();
		}else if(currentType.equals("PM25_CO")) {
			return pos.getPM25_CO();
		}else if(currentType.equals("PM25_O3")) {
			return pos.getPM25_O3();
		}else if(currentType.equals("PM25_NOx")) {
			return pos.getPM25_NOx();
		}else if(currentType.equals("PM25_THC")) {
			return pos.getPM25_THC();
		}else if(currentType.equals("PM10_RH")) {
			return pos.getPM10_RH();
		}else if(currentType.equals("PM10_AVG_TEMP")) {
			return pos.getPM10_AVG_TEMP();
		}else if(currentType.equals("PM10_CUMULATIVE_RAINFALL")) {
			return pos.getPM10_CUMULATIVE_RAINFALL();
		}else if(currentType.equals("PM10_RAINY_DAYS")) {
			return pos.getPM10_RAINY_DAYS();
		}else if(currentType.equals("PM10_SUNSHINE_HOURS")) {
			return pos.getPM10_SUNSHINE_HOURS();
		}else if(currentType.equals("PM25_RH")) {
			return pos.getPM25_RH();
		}else if(currentType.equals("PM25_AVG_TEMP")) {
			return pos.getPM25_AVG_TEMP();
		}else if(currentType.equals("PM25_CUMULATIVE_RAINFALL")) {
			return pos.getPM25_CUMULATIVE_RAINFALL();
		}else if(currentType.equals("PM25_RAINY_DAYS")) {
			return pos.getPM25_RAINY_DAYS();
		}else if(currentType.equals("PM25_SUNSHINE_HOURS")) {
			return pos.getPM25_SUNSHINE_HOURS();
		}
		return 10;
	}
	
}//class end