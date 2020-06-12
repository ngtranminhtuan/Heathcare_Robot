class axis{
  private:
    float Q_angle=    0.001;
    float Q_bias=     0.003;
    float R_measure=  0.03;
    float p_00= 0.0;
    float p_01= 0.0;
    float p_10= 0.0;
    float p_11= 0.0;
    float bias = 0.0;
    float angle= 0.0;   //goc loc
    //float sampletime= 0.005;    //thoi gian lau mau ms
    //float inv_sampletime= 200;
    float kp= 1;   
    float ki= 1;   
    float kd= 1; 
    float kb= 01;    //he so anti windup kb= can(ki/kd)
    float err_p= 0;
    float ui_p= 0;
    float ud_f_p= 0;
    float e_anti= 0;
  public:
  /////
    void set(float a, float b, float c,float d){
      kp= a;
      ki= b;
      kd= c;
      kb= d;
    }
  /////
    float kalman(float newAngle, float newRate, float dt){
      // Discrete Kalman filter time update equations - Time Update ("Predict")
      // Update xhat - Project the state ahead
            float rate = newRate - bias;
            angle += dt * rate;
    // Update estimation error covariance - Project the error covariance ahead
            p_00 += dt *(dt *p_11 -p_01 -p_10 +Q_angle);
            p_01 -= dt *p_11;
            p_10 -= dt *p_11;
            p_11 += Q_bias *dt;
    // Discrete Kalman filter measurement update equations - Measurement Update ("Correct")
    // Calculate Kalman gain - Compute the Kalman gain
            float S = p_00 +R_measure; // Estimate error
    // Kalman gain - This is a 2x1 vector
            float k_1= p_00 /S;
            float k_2= p_10 /S;
    // Calculate angle and bias - Update estimate with measurement zk (newAngle)
            float y = newAngle -angle; // Angle difference
            angle += k_1 *y;
            bias += k_2 *y;
    // Calculate estimation error covariance - Update the error covariance
            float p_00_temp = p_00;
            float p_01_temp = p_01;
            p_00 -= k_1 * p_00_temp;
            p_01 -= k_1 * p_01_temp;
            p_10 -= k_2 * p_00_temp;
            p_11 -= k_2 * p_01_temp;
    return angle;
    }
  /////
    int pid(float sp, float pv, float sampletime){
        float err= sp- pv;
      //khau ti le
        float up= kp* err; 
      //khau tich phan, anti windup 
        float ui= ki*(err+e_anti)*sampletime +ui_p;
      //khau dao ham, loc thong thap a= wt/(wt+1)
        float inv_sampletime= 1.0/sampletime;
        float ud= kd*(err- err_p)*inv_sampletime;
        float ud_f= 0.2*ud_f_p + 0.8*ud;   
      //
        float ut=up+ ui+ ud_f;
        float u_out= ut;
      //khau bao hoa
        if(u_out > 255){ u_out=255;}
        if(u_out < -255){ u_out=-255;}
      //cap nhat
        err_p= err;
        ui_p= ui;
        ud_f_p= ud_f;
        e_anti= (u_out- ut)*kb;
      
    return u_out;
    }
};
//========================================>  khai bao
  axis axis_x;
  axis axis_y;
  uint8_t i2c_dat[14];    //buffer for I2C data
  uint32_t time_0;
  int16_t acc_x, acc_y, acc_z;
  int16_t gyr_x, gyr_y; // gyr_z;
  //int16_t temp;
  #define moto_1a     5
  #define moto_1b     6
  #define moto_2a     9
  #define moto_2b     10
  #include <Wire.h>
//========================================> setup
void setup(){
  Serial.begin(115200);
  Wire.begin();
  //Wire.setClock(400000UL);

  axis_x.set( 6, 2, 0.5,0 );
  //axis_y.set( 8, 8, 0,1 );

  i2c_write(0x19, 0x07);   //Set the sample rate to 8kHz/(7+1) = 1000Hz
  i2c_write(0x1A, 0x00);   //Disable FSYNC and set 260 Hz Acc filtering, 256 Hz Gyro filtering, 8 KHz sampling
  i2c_write(0x1B, 0x00);    //Set Gyro Full Scale Range to ±250deg/s
  i2c_write(0x1C, 0x00);   //Set Accelerometer Full Scale Range to ±2g
  i2c_write(0x6B, 0x01);   //PLL with X axis gyroscope reference and disable sleep mode
  i2c_write(0x23, 0x00);    //tat bo nho dem fifo
  while(i2c_read(0x75, i2c_dat, 1));
  if(i2c_dat[0] != 0x68){
    Serial.println("Error reading sensor  ");
    while (1);
  }
  pinMode(moto_1a,OUTPUT);
  pinMode(moto_1b,OUTPUT);
  pinMode(moto_2a,OUTPUT);
  pinMode(moto_2b,OUTPUT);
  time_0 = micros();
  delay(100); //wait for sensor to stabilize
}
//========================================> loop
void loop() {
    while (i2c_read(0x3B, i2c_dat, 14));   //0x3B dia chi thanh gi acc x hight
    acc_x = (i2c_dat[0] << 8) | i2c_dat[1];
    acc_y = (i2c_dat[2] << 8) | i2c_dat[3];
    acc_z = (i2c_dat[4] << 8) | i2c_dat[5];
    //temp=(i2c_dat[6] << 8) | i2c_dat[7];    //nhiet do
    gyr_x = ((i2c_dat[8] << 8) | i2c_dat[9]) -310 ;
    gyr_y = ((i2c_dat[10] << 8) | i2c_dat[11]) -410 ;
    //gyr_z = ((i2c_dat[12] << 8) | i2c_dat[13]) +350 ;

    float roll  = atan2(acc_y, acc_z)* RAD_TO_DEG;    //cong thuc sai
    float pitch = -atan2(acc_x, acc_z)* RAD_TO_DEG;

    float gyr_x_rate= gyr_x*1.0/ 131; // Convert to deg/s
    float gyr_y_rate= gyr_y*1.0/ 131; // Convert to deg/s

    float dt= (float)(micros()- time_0)/ 1000000;
    time_0 = micros();

    float kalman_x= axis_x.kalman(roll, gyr_x_rate, dt);
    //float kalman_y= axis_y.kalman(pitch, gyr_y_rate, dt);
    int pwm_x= axis_x.pid(0, kalman_x, dt);
    //int pwm_y= axis_y.pid(0, kalman_y, dt);

    if(pwm_x> 0){
        analogWrite(moto_1a, pwm_x);
        digitalWrite(moto_1b,0);
    }else{
        analogWrite(moto_1b, -pwm_x);
        digitalWrite(moto_1a,0);
    }

   /*
    if(pwm_y> 0){
      analogWrite(moto_2a,pwm_y);
      digitalWrite(moto_2b,0);
    }else{
        analogWrite(moto_2b,-pwm_y);
        digitalWrite(moto_2a,0);
    }

  */
  Serial.print(kalman_x); Serial.print("\t");
  //Serial.print(kalman_y); Serial.print("\t");

  Serial.print("\n");

  delay(1);
}
//========================================> write read i2c
const uint8_t dev_add= 0x68;
const uint16_t i2c_timeout= 1000;
//sent 1 byte
uint8_t i2c_write(uint8_t reg_add, uint8_t dat){
  Wire.beginTransmission(dev_add);
  Wire.write(reg_add);
  Wire.write(dat);
  uint8_t ack= Wire.endTransmission(true); //tra ve 0 >> ko loi
  if(ack){
    Serial.print("i2c write failed: ");
    Serial.println(ack);
    while (1);
  }
  return ack;
}
//recive n byte
uint8_t i2c_read(uint8_t reg_add, uint8_t *dat, uint8_t num){
  uint32_t tim_0;
  Wire.beginTransmission(dev_add);
  Wire.write(reg_add);
  uint8_t ack = Wire.endTransmission(false); //don't release the bus
  if(ack){
        Serial.print(F("i2c read failed: "));
        Serial.println(ack);
        return ack;
  }
  Wire.requestFrom(dev_add, num, (uint8_t)true);
  for( uint8_t i= 0; i< num; i++){
        if(Wire.available())
            dat[i]= Wire.read();
        else{
        tim_0= micros();
            while (( (micros()- tim_0 ) < i2c_timeout) && !Wire.available());
            if (Wire.available()){
            dat[i]= Wire.read();
            }
            else{
               Serial.println("i2c_read timeout  ");
               return 5;  //loi gi do
            }
        }
  }
  return 0; //ok
}
//========================================> end

