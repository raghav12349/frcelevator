package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.XboxController;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.configs.CurrentLimitsConfigs;
import com.ctre.phoenix6.controls.DutyCycleOut;
import com.ctre.phoenix6.controls.PositionDutyCycle;

public class Robot extends TimedRobot {

  private XboxController controller;
  private TalonFX elevatorMotor;
  private TalonFX elevatorMotor2;

  private final DutyCycleOut dutyOut = new DutyCycleOut(0);
  private final PositionDutyCycle positionControl = new PositionDutyCycle(0);

  // Replace with your calibrated revolution values
  private final double HEIGHT_ONE = -3.85;   // Example revolutions for X
  private final double HEIGHT_TWO = -13.85;  // Example revolutions for B

  private double targetPos = 0.0;  // default

  @Override
  public void robotInit() {
    controller = new XboxController(0);
    elevatorMotor = new TalonFX(9);
    elevatorMotor2 = new TalonFX(10);
    System.out.println("Robot initialized");

    var currentconfiguration = new CurrentLimitsConfigs();
    currentconfiguration.SupplyCurrentLimit = 40;
    currentconfiguration.SupplyCurrentLimitEnable = true;
    elevatorMotor.getConfigurator().apply(currentconfiguration);
    elevatorMotor2.getConfigurator().apply(currentconfiguration);
  }

  @Override
  public void teleopPeriodic() {
    boolean yPressed = controller.getYButton();
    boolean aPressed = controller.getAButton();
    boolean xPressed = controller.getXButtonPressed();
    boolean bPressed = controller.getBButtonPressed();

    double rotations = elevatorMotor.getPosition().getValueAsDouble();

    // Preset heights
    if (xPressed) {
      targetPos = HEIGHT_ONE;
      System.out.println("Target = HEIGHT_ONE (" + HEIGHT_ONE + " revs)");
    } else if (bPressed) {
      targetPos = HEIGHT_TWO;
      System.out.println("Target = HEIGHT_TWO (" + HEIGHT_TWO + " revs)");
    }

    if (aPressed && rotations <= -1.3) {
      // Manual jog up
      elevatorMotor.setControl(dutyOut.withOutput(0.1));
      elevatorMotor2.setControl(dutyOut.withOutput(0.1));
      System.out.println("Elevator moving up (manual)");
    } else if (yPressed && rotations >= -18) {
      // Manual jog down
      elevatorMotor.setControl(dutyOut.withOutput(-0.1));
      elevatorMotor2.setControl(dutyOut.withOutput(-0.1));
      System.out.println("Elevator moving down (manual)");
    } else if (xPressed || bPressed) {
      // Move to preset position
      elevatorMotor.setControl(positionControl.withPosition(targetPos));
      elevatorMotor2.setControl(positionControl.withPosition(targetPos));
      System.out.println("Elevator going to preset | Target = " + targetPos);
    } else {
      // Hold last commanded position
      elevatorMotor.setControl(positionControl.withPosition(targetPos));
      elevatorMotor2.setControl(positionControl.withPosition(targetPos));
      System.out.println("Elevator holding position | Target = " + targetPos);
    }

    System.out.println("Current rotations = " + rotations);
    System.out.flush();
  }
}
