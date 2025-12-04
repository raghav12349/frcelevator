package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.XboxController;

import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.configs.CurrentLimitsConfigs;
import com.ctre.phoenix6.hardware.TalonFX;

import com.ctre.phoenix6.controls.DutyCycleOut;
import com.ctre.phoenix6.controls.PositionDutyCycle;

public class Robot extends TimedRobot {

  private XboxController controller;
  private TalonFX elevatorMotor;
  private TalonFX elevatorMotor2;

  private final DutyCycleOut manualDuty = new DutyCycleOut(0);

  private final PositionDutyCycle positionRequest = new PositionDutyCycle(0);

  private final double HEIGHT_ONE = -3.85;     
  private final double HEIGHT_TWO = -13.85;    

  private double targetPos = 0.0;
  private boolean initialized = false;

  @Override
  public void robotInit() {

    controller = new XboxController(0);
    elevatorMotor = new TalonFX(9);
    elevatorMotor2 = new TalonFX(10);

    // Configure motors
    TalonFXConfiguration cfg = new TalonFXConfiguration();

    // PID (slow + stable)
    cfg.Slot0.kP = 3.0;
    cfg.Slot0.kI = 0.0;
    cfg.Slot0.kD = 0.0;


    cfg.MotorOutput.PeakForwardDutyCycle = 0.05; 
    cfg.MotorOutput.PeakReverseDutyCycle = -0.05;

    elevatorMotor.getConfigurator().apply(cfg);
    elevatorMotor2.getConfigurator().apply(cfg);


    CurrentLimitsConfigs cur = new CurrentLimitsConfigs();
    cur.SupplyCurrentLimit = 40;
    cur.SupplyCurrentLimitEnable = true;

    elevatorMotor.getConfigurator().apply(cur);
    elevatorMotor2.getConfigurator().apply(cur);

    System.out.println("Robot initialized safely.");
  }

  @Override
  public void teleopPeriodic() {

    double rotations = elevatorMotor.getPosition().getValueAsDouble();

  
    if (!initialized) {
      targetPos = rotations;
      initialized = true;
      System.out.println("Holding starting position: " + targetPos);
    }

    boolean a = controller.getAButton();
    boolean y = controller.getYButton();
    boolean x = controller.getXButtonPressed();
    boolean b = controller.getBButtonPressed();


    if (x) {
      targetPos = HEIGHT_ONE;
      System.out.println("AUTO → MID");
    }
    if (b) {
      targetPos = HEIGHT_TWO;
      System.out.println("AUTO → TOP");
    }


    if (a && rotations <= -1.3) {
      elevatorMotor.setControl(manualDuty.withOutput(0.03));
      elevatorMotor2.setControl(manualDuty.withOutput(0.03));
      targetPos = rotations;
      return;
    }


    if (y && rotations >= -18) {
      elevatorMotor.setControl(manualDuty.withOutput(-0.03));
      elevatorMotor2.setControl(manualDuty.withOutput(-0.03));
      targetPos = rotations;
      return;
    }


    elevatorMotor.setControl(positionRequest.withPosition(targetPos));
    elevatorMotor2.setControl(positionRequest.withPosition(targetPos));
  }
}
