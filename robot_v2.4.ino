int button = A0; // Chân nút nhấn

int incomingByte = 0;   // dùng để lưu giá trị được gửi
//////////////////////////////////////////////////////////////

// Dieu khien Motor1
#define dir1 5
#define pwm1 6

// Dieu khien Motor2
#define dir2 9
#define pwm2 10

// Dieu khien Moto3
#define dir3 12
#define pwm3 11

//int flag1 = -1;               
//int flag2 = -1;
//////////////////////////
void dung()
{
  //flag1 = 0;                       
  //flag2 = 0; 
  digitalWrite(pwm1, LOW);
  digitalWrite(pwm2, LOW);
  digitalWrite(pwm3, LOW);  
}
//////////////////////////
void tien()
{
  digitalWrite(dir1, LOW);
  analogWrite(pwm1, 60);

  digitalWrite(dir2, LOW);
  analogWrite(pwm2, 60);  
}
////////////////////////////////
void lui()
{
  digitalWrite(dir1, HIGH);
  analogWrite(pwm1, 60);

  digitalWrite(dir2, HIGH);
  analogWrite(pwm2, 60);  
}
//////////////////////////
void quatrai()
{
  digitalWrite(dir1, LOW);
  analogWrite(pwm1, 100);

  digitalWrite(dir3, LOW);
  analogWrite(pwm3, 100);  
}
////////////////////////////////////
void quaphai()
{
  digitalWrite(dir2, LOW);
  analogWrite(pwm2, 100);

  digitalWrite(dir3, HIGH);
  analogWrite(pwm3, 100);
}
/////////////////////////////////////////
void xientrai()
{
   digitalWrite(dir1, LOW);
   analogWrite(pwm1, 50);

   digitalWrite(dir3, LOW);
   analogWrite(pwm3, 30);  
}
///////////////////////////////////////////
void xienphai()
{
   digitalWrite(dir2, LOW);
   analogWrite(pwm2, 50);

   digitalWrite(dir3, HIGH);
   analogWrite(pwm3, 30);  
}
///////////////////////////////////////////
void xeotrai()
{
   digitalWrite(dir1, HIGH);
   analogWrite(pwm1, 60);

   digitalWrite(dir3, HIGH);
   analogWrite(pwm3, 100);  
}
////////////////////////////////
void xeophai()
{
   digitalWrite(dir2, HIGH);
   analogWrite(pwm2, 60);

   digitalWrite(dir3, LOW);
   analogWrite(pwm3, 100);  
}

//////////////////////////////////////////////
void xoaytrai()
{
  digitalWrite(dir1, HIGH);
  analogWrite(pwm1, 40);

  digitalWrite(dir2, LOW);
  analogWrite(pwm2, 40);

  digitalWrite(dir3, LOW);
  analogWrite(pwm3, 40);
}

////////////////////////////////////////////////////
void xoayphai()
{
  digitalWrite(dir1, LOW);
  analogWrite(pwm1, 40);

  digitalWrite(dir2, HIGH);
  analogWrite(pwm2, 40);

  digitalWrite(dir3, HIGH);
  analogWrite(pwm3, 40);
}

char buff[10] = {};
float toadox = 0;
float khoangcach = 1.5;

// Value serial
char input[10]= {};
int p;
int dem=0;




void setup() {
  pinMode(button, INPUT);  //Cài đặt chân D11 ở trạng thái đọc dữ liệu
//////////////////////////////////////////////////////////////////////////////////
  //Các chân điều khiển motor
pinMode(dir1, OUTPUT);
pinMode(pwm1, OUTPUT);
//digitalWrite(pwm1, LOW);

pinMode(dir2, OUTPUT);
pinMode(pwm2, OUTPUT);
//digitalWrite(pwm2, LOW);

pinMode(dir3, OUTPUT);
pinMode(pwm3, OUTPUT);
//digitalWrite(pwm3, LOW);

//Giao tiếp serial
  Serial.setTimeout(10);
  Serial.begin(115200);     // mở serial với baudrate 115200
  
}
                                                                                                                                                                                                                 
void loop() {
  int buttonStatus = digitalRead(button);    //Đọc trạng thái button
  if (buttonStatus == HIGH) { // Nếu mà button bị nhấn
    if ( Serial.available () > 0 ) {
          char c = Serial.read ();
          if ( c == 'a' ) {
            toadox = atoi(input);
            del();  
          }
          else if ( c == 'b' ){
            khoangcach = atof(input);
            del();
          }
          else input[p++] = c;
        }         
        controled();   

  } 
  //else { // ngược lại
  //dung();
  //}
}

//Xoa buffer
void del(){
  for(int i=0; i<10; i++){
    input[i] = ' ';
  }
  p=0;
}

//Ham dieu khien robot
void controled(){
                //0. Neu khoang cach (1.1;1.4) va o giua thi dung robot
                if ((khoangcach >= 1.1)&&(khoangcach < 1.4 ))
                {
                  dung();
                }
                
                //1. Nếu khoảng cách lớn hơn 1.4 thì tiến
                if ((khoangcach >= 1.4)&&(toadox>-150)&&(toadox<150))    
                {
                  tien();
                  digitalWrite(pwm3,LOW);
                } 
                
              //2. Nếu khoảng cách nhỏ hơn 1.1 thì lùi
                if ((khoangcach < 1.1 )&&(toadox>-150)&&(toadox<150))
                {
                  lui();
                  digitalWrite(pwm3,LOW);
                } 
                
             //3. khoảng cách lớn hơn 1.4 và tọa độ x lớn hơn 150 thì xoay trai
                 if ((khoangcach >= 1.4 )&&(toadox>=150))
                {
                  xoaytrai();
                  
                }

              //4. khoảng cách lớn hơn 2 và tọa độ x nhỏ hơn -150 thì xoay phai
               if ((khoangcach >= 1.4 )&&(toadox<=-150))
                {
                  xoayphai();
                 
                }  
}

