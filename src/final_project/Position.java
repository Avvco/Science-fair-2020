package final_project;
/*
 * 0  Represents "NO_DATA"				 , image color:BLACK
 * 1  Represents "NOMAL" 				 , image color:WHITE
 * 2  Represents "AVERAGE above 1 STDEV" , image color:YELLOW
 * 3  Represents "AVERAGE above 2 STDEVs", image color:RED
 * -1 Represents "AVERAGE below 1 STDEV" , image color:GREEN
 * -2 Represents "AVERAGE below 2 STDEVs", image color:BLUE
 */
public class Position { //dataList
	
	private String name; //location
	private String month;
	private int square_x;//left side
	private int square_y;
	private int x;
	private int y;
	private int color = 0xffffffff;
	private int PM10 = 0;
	private int PM25 = 0;
	private int SO2 = 0;
	private int CO = 0;
	private int CO2 = 0;
	private int O3 = 0;
	private int NOx = 0;
	private int NO = 0;
	private int NO2 = 0;
	private int THC = 0;
	private int NMHC = 0;
	private int CH4 = 0;
	private int WIND_SPEED = 0;
	private int WS_HR = 0;
	private int AMB_TEMP = 0;
	private int RAIN_INT = 0;
	private int PH_RAIN = 0;
	private int RH = 0;
	private int RAIN_COND = 0;
	
	private int PM10_SO2 = 0;
	private int PM10_CO = 0;
	private int PM10_O3 = 0;
	private int PM10_NOx = 0;
	private int PM10_THC = 0;
	
	private int PM25_SO2 = 0;
	private int PM25_CO = 0;
	private int PM25_O3 = 0;
	private int PM25_NOx = 0;
	private int PM25_THC = 0;
	
	private int PM10_RH = 0;
	private int PM10_AVG_TEMP = 0;
	private int PM10_CUMULATIVE_RAINFALL = 0;
	private int PM10_RAINY_DAYS = 0;
	private int PM10_SUNSHINE_HOURS = 0;
	
	private int PM25_RH = 0;
	private int PM25_AVG_TEMP = 0;
	private int PM25_CUMULATIVE_RAINFALL = 0;
	private int PM25_RAINY_DAYS = 0;
	private int PM25_SUNSHINE_HOURS = 0;
		
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getMonth() {
		return month;
	}
	public void setMonth(String month) {
		this.month = month;
	}
	public int getSquare_x() {
		return square_x;
	}
	public void setSquare_x(int square_x) {
		this.square_x = square_x;
	}
	public int getSquare_y() {
		return square_y;
	}
	public void setSquare_y(int square_y) {
		this.square_y = square_y;
	}
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
	public int getColor() {
		return color;
	}
	public void setColor(int color) {
		this.color = color;
	}
	public int getPM10() {
		return PM10;
	}
	public void setPM10(int pM10) {
		PM10 = pM10;
	}
	public int getPM25() {
		return PM25;
	}
	public void setPM25(int pM25) {
		PM25 = pM25;
	}
	public int getSO2() {
		return SO2;
	}
	public void setSO2(int sO2) {
		SO2 = sO2;
	}
	public int getCO() {
		return CO;
	}
	public void setCO(int cO) {
		CO = cO;
	}
	public int getCO2() {
		return CO2;
	}
	public void setCO2(int cO2) {
		CO2 = cO2;
	}
	public int getO3() {
		return O3;
	}
	public void setO3(int o3) {
		O3 = o3;
	}
	public int getNOx() {
		return NOx;
	}
	public void setNOx(int nOx) {
		NOx = nOx;
	}
	public int getNO() {
		return NO;
	}
	public void setNO(int nO) {
		NO = nO;
	}
	public int getNO2() {
		return NO2;
	}
	public void setNO2(int nO2) {
		NO2 = nO2;
	}
	public int getTHC() {
		return THC;
	}
	public void setTHC(int tHC) {
		THC = tHC;
	}
	public int getNMHC() {
		return NMHC;
	}
	public void setNMHC(int nMHC) {
		NMHC = nMHC;
	}
	public int getCH4() {
		return CH4;
	}
	public void setCH4(int cH4) {
		CH4 = cH4;
	}
	public int getWIND_SPEED() {
		return WIND_SPEED;
	}
	public void setWIND_SPEED(int wIND_SPEED) {
		WIND_SPEED = wIND_SPEED;
	}
	public int getWS_HR() {
		return WS_HR;
	}
	public void setWS_HR(int wS_HR) {
		WS_HR = wS_HR;
	}
	public int getAMB_TEMP() {
		return AMB_TEMP;
	}
	public void setAMB_TEMP(int aMB_TEMP) {
		AMB_TEMP = aMB_TEMP;
	}
	public int getRAIN_INT() {
		return RAIN_INT;
	}
	public void setRAIN_INT(int rAIN_INT) {
		RAIN_INT = rAIN_INT;
	}
	public int getPH_RAIN() {
		return PH_RAIN;
	}
	public void setPH_RAIN(int pH_RAIN) {
		PH_RAIN = pH_RAIN;
	}
	public int getRH() {
		return RH;
	}
	public void setRH(int rH) {
		RH = rH;
	}
	public int getRAIN_COND() {
		return RAIN_COND;
	}
	public void setRAIN_COND(int rAIN_COND) {
		RAIN_COND = rAIN_COND;
	}
	public int getPM10_SO2() {
		return PM10_SO2;
	}
	public void setPM10_SO2(int pM10_SO2) {
		PM10_SO2 = pM10_SO2;
	}
	public int getPM10_CO() {
		return PM10_CO;
	}
	public void setPM10_CO(int pM10_CO) {
		PM10_CO = pM10_CO;
	}
	public int getPM10_O3() {
		return PM10_O3;
	}
	public void setPM10_O3(int pM10_O3) {
		PM10_O3 = pM10_O3;
	}
	public int getPM10_NOx() {
		return PM10_NOx;
	}
	public void setPM10_NOx(int pM10_NOx) {
		PM10_NOx = pM10_NOx;
	}
	public int getPM10_THC() {
		return PM10_THC;
	}
	public void setPM10_THC(int pM10_THC) {
		PM10_THC = pM10_THC;
	}
	public int getPM25_SO2() {
		return PM25_SO2;
	}
	public void setPM25_SO2(int pM25_SO2) {
		PM25_SO2 = pM25_SO2;
	}
	public int getPM25_CO() {
		return PM25_CO;
	}
	public void setPM25_CO(int pM25_CO) {
		PM25_CO = pM25_CO;
	}
	public int getPM25_O3() {
		return PM25_O3;
	}
	public void setPM25_O3(int pM25_O3) {
		PM25_O3 = pM25_O3;
	}
	public int getPM25_NOx() {
		return PM25_NOx;
	}
	public void setPM25_NOx(int pM25_NOx) {
		PM25_NOx = pM25_NOx;
	}
	public int getPM25_THC() {
		return PM25_THC;
	}
	public void setPM25_THC(int pM25_THC) {
		PM25_THC = pM25_THC;
	}
	public int getPM10_AVG_TEMP() {
		return PM10_AVG_TEMP;
	}
	public void setPM10_AVG_TEMP(int pM10_AVG_TEMP) {
		PM10_AVG_TEMP = pM10_AVG_TEMP;
	}
	public int getPM10_CUMULATIVE_RAINFALL() {
		return PM10_CUMULATIVE_RAINFALL;
	}
	public void setPM10_CUMULATIVE_RAINFALL(int pM10_CUMULATIVE_RAINFALL) {
		PM10_CUMULATIVE_RAINFALL = pM10_CUMULATIVE_RAINFALL;
	}
	public int getPM10_RAINY_DAYS() {
		return PM10_RAINY_DAYS;
	}
	public void setPM10_RAINY_DAYS(int pM10_RAINY_DAYS) {
		PM10_RAINY_DAYS = pM10_RAINY_DAYS;
	}
	public int getPM10_SUNSHINE_HOURS() {
		return PM10_SUNSHINE_HOURS;
	}
	public void setPM10_SUNSHINE_HOURS(int pM10_SUNSHINE_HOURS) {
		PM10_SUNSHINE_HOURS = pM10_SUNSHINE_HOURS;
	}
	public int getPM10_RH() {
		return PM10_RH;
	}
	public void setPM10_RH(int pM10_RH) {
		PM10_RH = pM10_RH;
	}
	public int getPM25_AVG_TEMP() {
		return PM25_AVG_TEMP;
	}
	public void setPM25_AVG_TEMP(int pM25_AVG_TEMP) {
		PM25_AVG_TEMP = pM25_AVG_TEMP;
	}
	public int getPM25_CUMULATIVE_RAINFALL() {
		return PM25_CUMULATIVE_RAINFALL;
	}
	public void setPM25_CUMULATIVE_RAINFALL(int pM25_CUMULATIVE_RAINFALL) {
		PM25_CUMULATIVE_RAINFALL = pM25_CUMULATIVE_RAINFALL;
	}
	public int getPM25_RAINY_DAYS() {
		return PM25_RAINY_DAYS;
	}
	public void setPM25_RAINY_DAYS(int pM25_RAINY_DAYS) {
		PM25_RAINY_DAYS = pM25_RAINY_DAYS;
	}
	public int getPM25_SUNSHINE_HOURS() {
		return PM25_SUNSHINE_HOURS;
	}
	public void setPM25_SUNSHINE_HOURS(int pM25_SUNSHINE_HOURS) {
		PM25_SUNSHINE_HOURS = pM25_SUNSHINE_HOURS;
	}
	public int getPM25_RH() {
		return PM25_RH;
	}
	public void setPM25_RH(int pM25_RH) {
		PM25_RH = pM25_RH;
	}
	
}