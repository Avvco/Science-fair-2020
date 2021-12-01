package final_project;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

public class ImageProc {
		
	static final int RED=0xffce0000;
	static final int YELLOW=0xffffff37;
	static final int GREEN=0xff007500;
	static final int BLUE=0xff000093;
	static final int WHITE=0xffffffff;
	static final int BLACK=0xff000000;
	static final int GRAY=0xffd0d0d0; 
	
	static final int Radius = 45;
	static final int Square_Radius = 17;
	
	//static List year = new ArrayList();
	
	
	
	//ImageProc imgProc = new ImageProc();
	
	/* public static void main(String[] args) {
		Scanner scanner1 = new Scanner(System.in);
		
		String FileOutput = null;
		FileOutput=scanner1.nextLine();
		scanner1.close();
		
		
		ImageProc imgProc2 = new ImageProc();
		List year = imgProc2.createYear();
		for(int i=0; i<year.size(); i++) {
			List<Position> mon = (List<Position>)year.get(i);
			//imgProc2.proceedImageDATA(FileOutput, mon);
		}
			
		
		
		//imgProc2.proceedImageDATA(FileOutput);
		
	} */
	
	public void compareToPosition(List<Position> dataList, List<Compare> realdata, String location) {
		Final_project Fp = new Final_project();
		for(int i=0;i<dataList.size();i++) {
			List<Position> month = (List<Position>) dataList.get(i);
			for(int j=0;j<month.size();j++) {
				Position pos = month.get(j);
				if(pos.getName().equals(location)) {
					for(int k=0;k<realdata.size();k++) {
						List<Compare> month1 = (List<Compare>) realdata.get(k);
						for(int l=0;l<month1.size();l++) {
							Compare com = month1.get(l);
							if(com.getMonth().indexOf(pos.getMonth())>=0) {								
								pos.setPM10_SO2(compareToPositionCalculation(com.getPM10(),com.getSO2()));
								pos.setPM10_CO(compareToPositionCalculation(com.getPM10(),com.getCO()));
								pos.setPM10_O3(compareToPositionCalculation(com.getPM10(),com.getO3()));
								pos.setPM10_NOx(compareToPositionCalculation(com.getPM10(),com.getNOx()));
								pos.setPM10_THC(compareToPositionCalculation(com.getPM10(),com.getTHC()));
								pos.setPM10_RH(compareToPositionCalculation(com.getPM10(),com.getRH()));
								pos.setPM10_AVG_TEMP(compareToPositionCalculation(com.getPM10(),com.getAVG_TEMP()));
								pos.setPM10_CUMULATIVE_RAINFALL(compareToPositionCalculation(com.getPM10(),com.getCUMULATIVE_RAINFALL()));
								pos.setPM10_RAINY_DAYS(compareToPositionCalculation(com.getPM10(),com.getRAINY_DAYS()));
								pos.setPM10_SUNSHINE_HOURS(compareToPositionCalculation(com.getPM10(),com.getSUNSHINE_HOURS()));
								
								pos.setPM25_SO2(compareToPositionCalculation(com.getPM25(),com.getSO2()));
								pos.setPM25_CO(compareToPositionCalculation(com.getPM25(),com.getCO()));
								pos.setPM25_O3(compareToPositionCalculation(com.getPM25(),com.getO3()));
								pos.setPM25_NOx(compareToPositionCalculation(com.getPM25(),com.getNOx()));
								pos.setPM25_THC(compareToPositionCalculation(com.getPM25(),com.getTHC()));
								pos.setPM25_RH(compareToPositionCalculation(com.getPM25(),com.getRH()));
								pos.setPM25_AVG_TEMP(compareToPositionCalculation(com.getPM25(),com.getAVG_TEMP()));
								pos.setPM25_CUMULATIVE_RAINFALL(compareToPositionCalculation(com.getPM25(),com.getCUMULATIVE_RAINFALL()));
								pos.setPM25_RAINY_DAYS(compareToPositionCalculation(com.getPM25(),com.getRAINY_DAYS()));
								pos.setPM25_SUNSHINE_HOURS(compareToPositionCalculation(com.getPM25(),com.getSUNSHINE_HOURS()));
							}
						}
					}
				}
			}
		}
	}//end
	
	public int compareToPositionCalculation(double[] data1, double[] data2) {
		Final_project Fp = new Final_project();
		if(data1.length!=data2.length) {
			return 0;
		}else if(data2.length==1) {
			return 0;
		}else if(Fp.CORREL(data1,data2)>0.7) {
			return 3;
		}else if(Fp.CORREL(data1,data2)<=0.7&&Fp.CORREL(data1,data2)>=0.3) {
			return 2;
		}else if(Fp.CORREL(data1,data2)<0.3&&Fp.CORREL(data1,data2)>-0.3) {
			return 1;
		}else if(Fp.CORREL(data1,data2)>=-0.7&&Fp.CORREL(data1,data2)<=-0.3) {
			return -1;
		}else if(Fp.CORREL(data1,data2)<-0.7) {
			return -2;
		}
		return 0;
	}//end
	
	public List createYear() {
		ImageProc imgProc = new ImageProc();
		List year = new ArrayList();
		List december = imgProc.createPosition("December");
		year.add(december);
		List november = imgProc.createPosition("November");
		year.add(november);
		List october = imgProc.createPosition("October");
		year.add(october);
		List september = imgProc.createPosition("September");
		year.add(september);
		List august = imgProc.createPosition("August");
		year.add(august);
		List july = imgProc.createPosition("July");;
		year.add(july);
		List june = imgProc.createPosition("June");
		year.add(june);
		List may = imgProc.createPosition("May");
		year.add(may);
		List april = imgProc.createPosition("April");
		year.add(april);
		List march = imgProc.createPosition("March");
		year.add(march);
		List february =imgProc.createPosition("February");
		year.add(february);
		List january = imgProc.createPosition("January");
		year.add(january);
		return year;
	}//end
	
	public List createPosition (String month) {
		List list = new ArrayList<Position>();
		Position pos = new Position();
		pos.setName("Keelung");
		pos.setX(2220);
		pos.setY(507);
		pos.setSquare_x(2167);
		pos.setSquare_y(483);
		pos.setMonth(month);
		list.add(pos);
		
		pos = new Position();
		pos.setName("Wanhua");
		pos.setX(2038);
		pos.setY(576);
		pos.setSquare_x(1985);
		pos.setSquare_y(554);
		pos.setMonth(month);
		list.add(pos);
		
		pos = new Position();
		pos.setName("Miaoli");
		pos.setX(1553);
		pos.setY(953);
		pos.setSquare_x(1499);
		pos.setSquare_y(935);
		pos.setMonth(month);
		list.add(pos);
		
		pos = new Position();
		pos.setName("Dayuan");
		pos.setX(1825);
		pos.setY(560);
		pos.setSquare_x(1772);
		pos.setSquare_y(541);
		pos.setMonth(month);
		list.add(pos);
		
		pos = new Position();
		pos.setName("Hukou");
		pos.setX(1704);
		pos.setY(678);
		pos.setSquare_x(1651);
		pos.setSquare_y(659);
		pos.setMonth(month);
		list.add(pos);
		
		pos = new Position();
		pos.setName("Tamsui");
		pos.setX(1987);
		pos.setY(482);
		pos.setSquare_x(1934);
		pos.setSquare_y(461);
		pos.setMonth(month);
		list.add(pos);
		
		pos = new Position();
		pos.setName("Zhongli");
		pos.setX(1824);
		pos.setY(641);
		pos.setSquare_x(1771);
		pos.setSquare_y(617);
		pos.setMonth(month);
		list.add(pos);
		
		pos = new Position();
		pos.setName("Yilan");
		pos.setX(2203);
		pos.setY(813);
		pos.setSquare_x(2152);
		pos.setSquare_y(787);
		pos.setMonth(month);
		list.add(pos);
		//nor
		
		pos = new Position();
		pos.setName("Changhua");
		pos.setX(1345);
		pos.setY(1339);
		pos.setSquare_x(1296);
		pos.setSquare_y(1323);
		pos.setMonth(month);
		list.add(pos);		
		
		pos = new Position();
		pos.setName("Shalu");
		pos.setX(1374);
		pos.setY(1211);
		pos.setSquare_x(1322);
		pos.setSquare_y(1190);
		pos.setMonth(month);
		list.add(pos);
		
		pos = new Position();
		pos.setName("Xitun");
		pos.setX(1409);
		pos.setY(1264);
		pos.setSquare_x(1358);
		pos.setSquare_y(1252);
		pos.setMonth(month);
		list.add(pos);
		
		pos = new Position();
		pos.setName("Erlin");
		pos.setX(1253);
		pos.setY(1443);
		pos.setSquare_x(1200);
		pos.setSquare_y(1427);
		pos.setMonth(month);
		list.add(pos);
		//mid
		
		pos = new Position();
		pos.setName("Hengchun");
		pos.setX(1528);
		pos.setY(2866);
		pos.setSquare_x(1475);
		pos.setSquare_y(2846);
		pos.setMonth(month);
		list.add(pos);
		
		pos = new Position();
		pos.setName("Lunbei");
		pos.setX(1204);
		pos.setY(1571);
		pos.setSquare_x(1151);
		pos.setSquare_y(1557);
		pos.setMonth(month);
		list.add(pos);
		
		pos = new Position();
		pos.setName("Nanzih");
		pos.setX(1197);
		pos.setY(2365);
		pos.setSquare_x(1143);
		pos.setSquare_y(2346);
		pos.setMonth(month);
		list.add(pos);
		
		pos = new Position();
		pos.setName("Pingtung");
		pos.setX(1310);
		pos.setY(2411);
		pos.setSquare_x(1256);
		pos.setSquare_y(2393);
		pos.setMonth(month);
		list.add(pos);
		
		pos = new Position();
		pos.setName("Siaogang");
		pos.setX(1202);
		pos.setY(2505);
		pos.setSquare_x(1151);
		pos.setSquare_y(2486);
		pos.setMonth(month);
		list.add(pos);
		
		pos = new Position();
		pos.setName("Singang");
		pos.setX(1203);
		pos.setY(1728);
		pos.setSquare_x(1150);
		pos.setSquare_y(1711);
		pos.setMonth(month);
		list.add(pos);
		
		pos = new Position();
		pos.setName("Tainan");
		pos.setX(1109);
		pos.setY(2172);
		pos.setSquare_x(1057);
		pos.setSquare_y(2154);
		pos.setMonth(month);
		list.add(pos);
		
		pos = new Position();
		pos.setName("Xinying");
		pos.setX(1195);
		pos.setY(1924);
		pos.setSquare_x(1146);
		pos.setSquare_y(1903);
		pos.setMonth(month);
		list.add(pos);
		//sou
		
		pos = new Position();
		pos.setName("Hualien");
		pos.setX(2096);
		pos.setY(1406);
		pos.setSquare_x(2039);
		pos.setSquare_y(1382);
		pos.setMonth(month);
		list.add(pos);
		
		pos = new Position();
		pos.setName("Taitung");
		pos.setX(1788);
		pos.setY(2346);
		pos.setSquare_x(1733);
		pos.setSquare_y(2328);
		pos.setMonth(month);
		list.add(pos);
		
		return list;
	}//end
	
	public static boolean Distance_Calculation(int Current_X, int Current_Y, int Target_X, int Target_Y, int Radius) {
		if(Math.pow(Math.pow((double)Current_X-(double)Target_X,2)+Math.pow((double)Current_Y-(double)Target_Y,2), 0.5)<=(double)Radius) {
			return true;
		}
		return false;
	}//end
	
	public static boolean Distance_Calculation2(int x, int y, int centerX, int centerY, int Radius) {
		if(x>=centerX-Radius&&x<=centerX+Radius&&y>=centerY-Radius&&y<=centerY+Radius) {
			return true;
		}
		return false;
	}//end
	
	public int imageColor(int data) {
		if(data==3) {
			return ImageProc.RED;
		}else if(data==2) {
			return ImageProc.YELLOW;
		}else if(data==1) {
			return ImageProc.GRAY;
		}else if(data==-1) {
			return ImageProc.GREEN;
		}else if(data==-2) {
			return ImageProc.BLUE;
		}
		return ImageProc.BLACK;
	}//end

	public void proceedImageDATA(String FileOutput, List dataList, String dataIn) {
		System.out.println("\n\n----------Image Process Start----------\n\n");
		File file = new File(dataIn);
		
		String newFileRoute=FileOutput+"/IMAGE";
		File dir_file = new File(newFileRoute);
		dir_file.mkdir();
		
		String routePM10=newFileRoute+"/PM10";
		File dir_file1 = new File(routePM10);
		dir_file1.mkdir();
		
		String routePM25=newFileRoute+"/PM25";
		File dir_file2 = new File(routePM25);
		dir_file2.mkdir();
		
		/* for (int i =0; i< imageList.size(); i++) {
			List<Position> month = (List<Position>) imageList.get(i);
			/if ("Wanhua".equals(pos.getName())) {
				pos.setColor(ImageProc2.RED);
				System.out.println("--- [" + pos.getName() + "][" + pos.getX() + "][" + pos.getY() + "][" + pos.getColor() + "]");
			}
		}*/
		
		if (file.isDirectory()) {
			
			File[] files = file.listFiles();
			for(File f:files) {
				if(f.getName().indexOf(".png")>0){
					String route = f.getAbsolutePath();
					BufferedImage bufImage=null;
					BufferedImage imagePM10=null;
					BufferedImage imagePM25=null;
					try {											//initialize the image_array for each image
						bufImage = ImageIO.read(new File(route));
						imagePM10 = ImageIO.read(new File(route));
						imagePM25 = ImageIO.read(new File(route));
						
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					int width = bufImage.getWidth();		//get width and height for the original image
			        int height = bufImage.getHeight();
			        
			        int[] rgbs = bufImage.getRGB(0, 0, width, height, null, 0, width);
			        int rgbsCounter=0;
			        for(int i=0;i<dataList.size();i++) {
			        	List<Position> month = (List<Position>) dataList.get(i);
			        	int rgbPM10[][];
			        	int rgbPM25[][];
			        	rgbPM10=new int[width][height];
			        	rgbPM25=new int[width][height];
			        	rgbsCounter=0;
				        for(int j=0;j<height;j++) { //convert original RGB-integer to multiple two-dimensional array, for easy to access x,y axis
				        	for(int k=0;k<width;k++) {
				        		rgbPM10[k][j]=rgbs[rgbsCounter];
				        		rgbPM25[k][j]=rgbs[rgbsCounter];
				        		rgbsCounter++;
				        	}
				        }
			        	String outputMonth=null;
						//System.out.println("[" + month.getName() + "][" + month.getX() + "][" + month.getY() + "][" + month.getColor() + "]");
			        	for(int j=0;j<month.size();j++) {			//coloring the circle for the image, one station a loop
			        		Position pos = (Position) month.get(j);
			        		outputMonth=pos.getMonth();//get current month for output-image
			        		//====//
			        		for(int k=0;k<height;k++) {
					        	for(int l=0;l<width;l++) {
					        		if(Distance_Calculation(l, k, pos.getX(), pos.getY(),ImageProc.Radius)) {
					        			rgbPM10[l][k] = imageColor(pos.getPM10());
					        			//System.out.println("HI [" + i + "][" + j + "] = " + RGB[j][i]);
					        		}
					        	} 
					        }
			        		//====//
			        		for(int k=0;k<height;k++) {
					        	for(int l=0;l<width;l++) {
					        		if(Distance_Calculation(l, k, pos.getX(), pos.getY(),ImageProc.Radius)) {
					        			rgbPM25[l][k] = imageColor(pos.getPM25());
					        			//System.out.println(rgbPM25[l][k]);
					        			//System.out.println("HI [" + i + "][" + j + "] = " + RGB[j][i]);
					        			//System.out.println(pos.getMonth()+" "+pos.getName()+" "+pos.getPM25());
					        		}
					        	} 
					        }
			        		//====//			        		
			        	}
			        	int[]outputPM10;
			        	int[]outputPM25;
			        	outputPM10 = new int[rgbsCounter];
			        	outputPM25 = new int[rgbsCounter];
			        	int rgbsLotaion=0;
						for(int ii=0;ii<height;ii++)//convert two-dimensional array to output one-dimensional array
				        {
				        	for(int jj=0;jj<width;jj++)
				        	{
				        		outputPM10[rgbsLotaion]=rgbPM10[jj][ii];
				        		outputPM25[rgbsLotaion]=rgbPM25[jj][ii];
				        		rgbsLotaion++;
				        	}
				        }
						
						//set color for each pixel at new image
						imagePM10.setRGB(0, 0, width, height, outputPM10, 0, width);
						imagePM25.setRGB(0, 0, width, height, outputPM25, 0, width);
						
						//image output
						try {
							// ImageIO.write(bufImage, "PNG", new File(file+"/bb.png"));
							 ImageIO.write(imagePM10, "PNG", new File(routePM10+"/"+outputMonth+"_PM10.png"));
							 System.out.println(outputMonth+"_PM10.png\tDone");
							 ImageIO.write(imagePM25, "PNG", new File(routePM25+"/"+outputMonth+"_PM25.png"));
							 System.out.println(outputMonth+"_PM25.png\tDone");
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						System.out.println();
						System.gc();
			        }
			        /*
			        int RGB[][];
			        RGB=new int[width][height];
			        
			        
					
			        int rgbsCounter=0;
			        for(int i=0;i<height;i++) {
			        	for(int j=0;j<width;j++) {
			        		RGB[j][i]=rgbs[rgbsCounter];
			        		rgbsCounter++;
			        	}
			        }
			        System.out.println(width);
					System.out.println(height);
					
					
					
					for(int ii=0; ii<mon.size(); ii++) {
						Position pos = mon.get(ii);
						for(int i=0;i<height;i++) {
				        	for(int j=0;j<width;j++) {
				        		if(Distance_Calculation(j, i, pos.getX(), pos.getY(),Radius)) {
				        			RGB[j][i] = pos.getColor();
				        			//System.out.println("HI [" + i + "][" + j + "] = " + RGB[j][i]);
				        		}
				        	} 
				        }
					}
					
	
					
					int rgbsLotaion=0;
					for(int i=0;i<height;i++)
			        {
			        	for(int j=0;j<width;j++)
			        	{
			        		rgbs[rgbsLotaion]=RGB[j][i];
			        		// if(Distance_Calculation(j,i,Hualien_X,Hualien_Y,Radius)) {
			        		// System.out.println(rgbs[rgbsLotaion]);
			        		// rgbs[rgbsLotaion]=RGB[j][i];
			        		// System.out.println(rgbs[rgbsLotaion]);
			        		// }
			        		rgbsLotaion++;
			        	}
			        }
					bufImage.setRGB(0, 0, width, height, rgbs, 0, width);
					 
					 try {
						// ImageIO.write(bufImage, "PNG", new File(file+"/bb.png"));
						 ImageIO.write(bufImage, "PNG", new File(newFileRoute+"/bb.png"));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					*/
			
				}
			}
		}
	}//end
	
	public void proceedImageDATA2(String FileOutput, List dataList, String PM10in, String PM25in, String PM10in2, String PM25in2) throws InterruptedException {
		String newFileRoute=FileOutput+"/IMAGE_Relevance";
		File dir_file = new File(newFileRoute);
		dir_file.mkdir();
		
		String newFileRoute_interior1=newFileRoute+"/1";
		File dir_file3 = new File(newFileRoute_interior1);
		dir_file3.mkdir();
		
		String newFileRoute_interior2=newFileRoute+"/2";
		File dir_file4 = new File(newFileRoute_interior2);
		dir_file4.mkdir();
		
		String routePM10=newFileRoute_interior1+"/PM10";
		File dir_file1 = new File(routePM10);
		dir_file1.mkdir();
		
		String routePM25=newFileRoute_interior1+"/PM25";
		File dir_file2 = new File(routePM25);
		dir_file2.mkdir();
		
		String routePM10_1=newFileRoute_interior2+"/PM10";
		File dir_file5 = new File(routePM10_1);
		dir_file5.mkdir();
		
		String routePM25_1=newFileRoute_interior2+"/PM25";
		File dir_file6 = new File(routePM25_1);
		dir_file6.mkdir();
		
		String PM10CompareType[] = {"PM10_SO2","PM10_CO","PM10_O3","PM10_NOx","PM10_THC"};
		String PM25CompareType[] = {"PM25_SO2","PM25_CO","PM25_O3","PM25_NOx","PM25_THC"};
		String PM10CompareType2[] = {"PM10_RH", "PM10_AVG_TEMP", "PM10_CUMULATIVE_RAINFALL", "PM10_RAINY_DAYS", "PM10_SUNSHINE_HOURS"};
		String PM25CompareType2[] = {"PM25_RH", "PM25_AVG_TEMP", "PM25_CUMULATIVE_RAINFALL", "PM25_RAINY_DAYS", "PM25_SUNSHINE_HOURS"};
		writeImage2(PM10in,routePM10,dataList,"PM10",PM10CompareType);
		writeImage2(PM25in,routePM25,dataList,"PM25",PM25CompareType);
		writeImage2(PM10in2,routePM10_1,dataList,"PM10_2",PM10CompareType2);
		writeImage2(PM25in2,routePM25_1,dataList,"PM25_2",PM25CompareType2);
		
	}//end
	
	public void writeImage2(String imageLocation, String outputLocation, List dataList, String currentType, String[] compareType) throws InterruptedException {
		DataRelevance DR = new DataRelevance();
		File file=new File(imageLocation);
		if (file.isDirectory()) {	
			File[] files = file.listFiles();
			for(File f:files) {
				if(f.getName().indexOf(".png")>0){
					String route = f.getAbsolutePath();
					BufferedImage bufImage=null;
					BufferedImage image=null;
					try {											//initialize the image_array for each image
						bufImage = ImageIO.read(new File(route));
						image = ImageIO.read(new File(route));
						
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					int width = bufImage.getWidth();		//get width and height for the original image
			        int height = bufImage.getHeight();
			        
			        int[] rgbs = bufImage.getRGB(0, 0, width, height, null, 0, width);
			        int rgbsCounter=0;
			        for(int i=0;i<dataList.size();i++) {//run 12 times
			        	List<Position> month = (List<Position>) dataList.get(i);
			        	int converted_rgb[][];
			        	converted_rgb=new int[width][height];
			        	rgbsCounter=0;
				        for(int j=0;j<height;j++) { //convert original RGB-integer to multiple two-dimensional array, for easy to access x,y axis
				        	for(int k=0;k<width;k++) {
				        		converted_rgb[k][j]=rgbs[rgbsCounter];
				        		rgbsCounter++;
				        	}
				        }
			        	String outputMonth=null;
						//System.out.println("[" + month.getName() + "][" + month.getX() + "][" + month.getY() + "][" + month.getColor() + "]");
			        	for(int j=0;j<month.size();j++) {		//coloring the circle for the image, one station a loop
			        		Position pos = (Position) month.get(j);
			        		outputMonth=pos.getMonth();//get current month for output-image
			        		//====//
			        		int x[] = {pos.getSquare_x()+ImageProc.Square_Radius, pos.getSquare_x()+3*ImageProc.Square_Radius,
			        				pos.getSquare_x()+5*ImageProc.Square_Radius, pos.getSquare_x()+ImageProc.Square_Radius+ImageProc.Square_Radius, 
			        				pos.getSquare_x()+3*ImageProc.Square_Radius+ImageProc.Square_Radius};
			        		int y[] = {pos.getSquare_y(), pos.getSquare_y(), pos.getSquare_y(), pos.getSquare_y()+2*ImageProc.Square_Radius,
			        				pos.getSquare_y()+2*ImageProc.Square_Radius};
			        		for(int m=0;m<compareType.length;m++) {
			        			for(int k=0;k<height;k++) {
						        	for(int l=0;l<width;l++) {
						        		if(Distance_Calculation2(l,k,x[m],y[m],ImageProc.Square_Radius)) {
						        			converted_rgb[l][k] = imageColor(DR.listData2(compareType[m],pos));
						        			//System.out.println("HI [" + i + "][" + j + "] = " + RGB[j][i]);
						        		}
						        	} 
						        }	
			        		}
			        			        		
			        	}
			        	int[] outputrgb;
			        	outputrgb = new int[rgbsCounter];
			        	int rgbsLotaion=0;
						for(int ii=0;ii<height;ii++) {//convert two-dimensional array to output one-dimensional array
				        	for(int jj=0;jj<width;jj++) {
				        		outputrgb[rgbsLotaion]=converted_rgb[jj][ii];
				        		rgbsLotaion++;
				        	}
				        }
						
						//set color for each pixel at new image
						image.setRGB(0, 0, width, height, outputrgb, 0, width);
						
						//image output
						try {
							// ImageIO.write(bufImage, "PNG", new File(file+"/bb.png"));
							 ImageIO.write(image, "PNG", new File(outputLocation+"/"+outputMonth+"_"+currentType+".png"));
							 System.out.println(outputMonth+"_"+currentType+".png\tDone");
							 TimeUnit.SECONDS.sleep(3);//delay for 3 seconds
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						System.out.println();
						System.gc();
			        }

				}
			}
		}
	}
}//class end