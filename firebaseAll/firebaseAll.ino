#include <FirebaseESP32.h>
#include <WiFi.h>
#include <Wire.h>
#include <Adafruit_MPU6050.h>
#include <Adafruit_Sensor.h>
#include <OneWire.h>
#include <DallasTemperature.h>

// WiFi credentials
#define WIFI_SSID "suza-nG"
#define WIFI_PASSWORD "12345678"

// Firebase credentials
#define FIREBASE_HOST "coma-patient-care-default-rtdb.firebaseio.com"
#define FIREBASE_AUTH "aVUdivqDRMRgTVCWus9GY2HtU77jIm70fVHmCT4v"

// Thresholds and sensor pins
const float MOTION_THRESHOLD = 1;
#define ONE_WIRE_BUS 5
const float TEMP_MIN = 36.1;
const float TEMP_MAX = 37.2;
#define TRIGGER_PIN 2
#define ECHO_PIN 4
const int bagHeight = 25;
const int fullThreshold = 22;

// MPU6050 and temperature sensor
Adafruit_MPU6050 mpu;
OneWire oneWire(ONE_WIRE_BUS);
DallasTemperature sensors(&oneWire);

// Firebase objects
FirebaseData firebaseData;
FirebaseJson json;
FirebaseConfig config;
FirebaseAuth auth;

void setup() {
  Serial.begin(115200);

  // Initialize MPU6050
  Serial.println("Initializing the MPU6050...");
  if (!mpu.begin()) {
    Serial.println("MPU6050 connection failed!");
    while (1);
  } else {
    Serial.println("MPU6050 connection successful!");
  }

  // Initialize temperature sensor
  sensors.begin();

  // Initialize ultrasonic sensor pins
  pinMode(TRIGGER_PIN, OUTPUT);
  pinMode(ECHO_PIN, INPUT);

  // Connect to WiFi
  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
  Serial.print("Connecting...");
  while (WiFi.status() != WL_CONNECTED) {
    Serial.print(".");
    delay(300);
  }
  Serial.println();
  Serial.print("IP Address: ");
  Serial.println(WiFi.localIP());
  Serial.println();

  // Configure Firebase
  config.host = FIREBASE_HOST;
  config.signer.tokens.legacy_token = FIREBASE_AUTH;
  Firebase.begin(&config, &auth);
}

void loop() {
  // Variables to store accelerometer values
  sensors_event_t a, g, temp;
  mpu.getEvent(&a, &g, &temp);

  // Calculate magnitude of acceleration
  float magnitude = sqrt(sq(a.acceleration.x) + sq(a.acceleration.y) + sq(a.acceleration.z));
  Serial.print("Magnitude of acceleration: ");
  Serial.println(magnitude);

  // Determine motion status
  String motionStatus = abs(magnitude - 9.81) > MOTION_THRESHOLD ? "Patient1: Motion detected" : "Patient1: Not responding to the environment";
  Serial.println(motionStatus);

  // Request temperature
  sensors.requestTemperatures();
  float temperatureC = sensors.getTempCByIndex(0);
  Serial.print("Celsius temperature: ");
  Serial.println(temperatureC);

  // Determine temperature status
  String tempStatus = (temperatureC >= TEMP_MIN && temperatureC <= TEMP_MAX) ? "Normal" : "Abnormal";
  Serial.println("Status: " + tempStatus);

  // Ultrasonic sensor reading
  long duration, distance;
  digitalWrite(TRIGGER_PIN, LOW);
  delayMicroseconds(2);
  digitalWrite(TRIGGER_PIN, HIGH);
  delayMicroseconds(10);
  digitalWrite(TRIGGER_PIN, LOW);
  duration = pulseIn(ECHO_PIN, HIGH);
  distance = duration * 0.034 / 2;

  // Calculate urine level
  int urineLevel = bagHeight - distance;
  Serial.print("Urine Level: ");
  Serial.print(urineLevel);
  Serial.println(" cm");

  // Determine bag status
  String bagStatus = urineLevel >= fullThreshold ? "Leg bag is full! Please empty it." : "Leg bag is not full.";
  Serial.println(bagStatus);

  // Prepare JSON payload
  json.clear();
  json.set("magnitude", magnitude);
  json.set("motionStatus", motionStatus);
  json.set("temperature", temperatureC);
  json.set("tempStatus", tempStatus);
  json.set("urineLevel", urineLevel);
  json.set("bagStatus", bagStatus);

  // Send data to Firebase
  String path = "/ComaPatient/data";
  if (Firebase.set(firebaseData, path, json)) {
    Serial.println("Data sent to Firebase successfully");
  } else {
    Serial.println("Failed to send data to Firebase");
    Serial.println("REASON: " + firebaseData.errorReason());
  }

  delay(5000);
}
