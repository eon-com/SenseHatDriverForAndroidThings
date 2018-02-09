# SenseHat Driver for AndoridThings

**HINT: Project use GitFlow (https://www.atlassian.com/git/tutorials/comparing-workflows/gitflow-workflow)
Short: Current development is done in "develop" branch and "master" includes the current release!**

##"Another" Sense Hat driver for Android Things
This driver connects the "[SenseHat](https://www.raspberrypi.org/products/sense-hat/)" with AndroidThings environment.  

It includes the following features:

- [x] LED 8x8 Matrix: draw pixels in differnt colors
- [x] Joystick: detects movement and button press
- [x] Temperature and Humidity: reads temperature and humidity
- [ ] OPEN: other sensors


##How to use
Include the "SenseHatDriverLibrary" to your AndroidThings project.

*settings.gradle*
 ````
    include ':sensehatdriverdemo', ':sensehatdriverlibrary'
````

*HomeActicity.java*
````
    SensorManager sensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);

    SenseHat senseHat = SenseHat.init(sensorManager);
    LedMatrix ledMatrix = senseHat.getLedMatrix();
    ledMatrix.draw(Color.RED);   

    senseHat.addJoystickListener(new JoystickListener() {
        @Override
        public void stickMoved(JoystickDirectionEnum direction) throws IOException {
        ...
        }
    });

````

##Version overview
- V1.0: Initial release 

##Other resources
- AndroidThings Homepage: https://developer.android.com/things/index.html
- "offical" AndroidThings SenseHatDriver: https://github.com/androidthings/contrib-drivers/blob/master/sensehat/README.md
- SenseHat: https://www.raspberrypi.org/products/sense-hat/
- SenseHat KURA (Java implementation): https://github.com/eclipse/kura/tree/develop/kura/examples/org.eclipse.kura.raspberrypi.sensehat
- SenseHat (C# implementation): https://www.hackster.io/laserbrain/windows-iot-sense-hat-10cac2

