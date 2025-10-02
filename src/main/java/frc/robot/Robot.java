package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.XboxController;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.configs.CurrentLimitsConfigs;
import com.ctre.phoenix6.controls.DutyCycleOut;

public class Robot extends TimedRobot {

  private XboxController controller;
  private TalonFX elevatorMotor;
  private TalonFX elevatorMotor2;
  private final DutyCycleOut dutyOut = new DutyCycleOut(0);


  @Override
  public void robotInit() {
    controller = new XboxController(0);
    elevatorMotor = new TalonFX(10);
    elevatorMotor2 = new TalonFX(11);
    System.out.println("Robot initialized");
    var currentconfiguration = new CurrentLimitsConfigs();
    currentconfiguration.SupplyCurrentLimit = 40;
    currentconfiguration.SupplyCurrentLimitEnable = true;
    elevatorMotor.getConfigurator().refresh(currentconfiguration);
    elevatorMotor2.getConfigurator().refresh(currentconfiguration);
    elevatorMotor.getConfigurator().apply(currentconfiguration);
    elevatorMotor2.getConfigurator().apply(currentconfiguration);
  
  }

  @Override
  public void teleopPeriodic() {
    boolean yPressed = controller.getYButton();
    boolean aPressed = controller.getAButton();
    boolean xPressed = controller.getXButton();
    boolean bPressed = controller.getBButton();
    double rotations = elevatorMotor.getPosition().getValueAsDouble();
    // Always print button states for debugging
    System.out.println("Teleoperated loop  Y=" + yPressed + " A=" + aPressed);

    if (aPressed && rotations<7.0) {
      while(rotations<=7.0){
        elevatorMotor.setControl(dutyOut.withOutput(0.1));
        elevatorMotor2.setControl(dutyOut.withOutput(0.1));
        System.out.println("Elevator moving up");
        System.out.println(rotations);
      }      
    } else if (yPressed && rotations>-7.0) {
       while(rotations>=-7.0){
         elevatorMotor.setControl(dutyOut.withOutput(-0.1));
         elevatorMotor2.setControl(dutyOut.withOutput(-0.1));
         System.out.println("Elevator moving down");
         System.out.println(rotations);
      }
      
    } else if (xPressed){
        System.out.print("Elevator Moving to Option 2");
        System.out.print(rotations);
        if (rotations<4.0){
          while(rotations<=4.0){
            elevatorMotor.setControl(dutyOut.withOutput(0.1));
            elevatorMotor2.setControl(dutyOut.withOutput(0.1));
            System.out.print("Elevator Moving to Option 2");
            System.out.print(rotations);
          }
        } else if(rotations>4.0){
            while(rotations>=4.0){
              elevatorMotor.setControl(dutyOut.withOutput(-0.1));
              elevatorMotor2.setControl(dutyOut.withOutput(-0.1));              
        }        
        }
    }else if (bPressed){
      if (rotations<-4.0){
        System.out.print("Elevator Moving to Option 3");
        System.out.print(rotations);
        while(rotations<=-4.0){
          elevatorMotor.setControl(dutyOut.withOutput(0.1));
          elevatorMotor2.setControl(dutyOut.withOutput(0.1));        
        }
      } else if(rotations>-4.0){
          System.out.print("Elevator Moving to Option 3");
          System.out.print(rotations);
          while(rotations>=-4.0){
            elevatorMotor.setControl(dutyOut.withOutput(-0.1));
            elevatorMotor2.setControl(dutyOut.withOutput(-0.1));
          }        
      }
    }
    else {
      elevatorMotor.setControl(dutyOut.withOutput(0.0));
      elevatorMotor2.setControl(dutyOut.withOutput(0.0));
      System.out.println("Elevator stopped");
    }

    
    System.out.flush(); 
  
  
}
}