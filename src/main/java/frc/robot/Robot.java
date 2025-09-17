package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.XboxController;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.controls.DutyCycleOut;

public class Robot extends TimedRobot {

  private XboxController controller;
  private TalonFX elevatorMotor;
  private final DutyCycleOut dutyOut = new DutyCycleOut(0);

  @Override
  public void robotInit() {
    controller = new XboxController(0);
    elevatorMotor = new TalonFX(1);
    System.out.println("Robot initialized");
  }

  @Override
  public void teleopPeriodic() {
    boolean yPressed = controller.getYButton();
    boolean aPressed = controller.getAButton();

    // Always print button states for debugging
    System.out.println("teleoperated loop  Y=" + yPressed + " A=" + aPressed);

    if (yPressed) {
      elevatorMotor.setControl(dutyOut.withOutput(0.5));
      System.out.println("Elevator moving up");
    } else if (aPressed) {
      elevatorMotor.setControl(dutyOut.withOutput(-0.5));
      System.out.println("Elevator moving down");
    } else {
      elevatorMotor.setControl(dutyOut.withOutput(0.0));
      System.out.println("Elevator stopped");
    }

    
    System.out.flush(); 
  
  }
}
