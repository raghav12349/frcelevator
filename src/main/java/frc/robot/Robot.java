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

  private double targetPos = 0.0;  // default target

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
    boolean xPressed = controller.getXButton();  // Use getXButton (true while held)
    boolean bPressed = controller.getBButton();  // Use getBButton (true while held)

    double rotations = elevatorMotor.getPosition().getValueAsDouble();

    // Update target position when X/B is pressed
    if (xPressed) {
      targetPos = HEIGHT_ONE;
      System.out.println("Target updated to HEIGHT_ONE: " + HEIGHT_ONE);
    }
    if (bPressed) {
      targetPos = HEIGHT_TWO;
      System.out.println("Target updated to HEIGHT_TWO: " + HEIGHT_TWO);
    }

    // Manual override with A/Y
    if (aPressed && rotations <= -1.3) {
      elevatorMotor.setControl(dutyOut.withOutput(0.1));
      elevatorMotor2.setControl(dutyOut.withOutput(0.1));
      System.out.println("Manual up");
      targetPos = rotations; // update target so holding works after manual
    } else if (yPressed && rotations >= -18) {
      elevatorMotor.setControl(dutyOut.withOutput(-0.1));
      elevatorMotor2.setControl(dutyOut.withOutput(-0.1));
      System.out.println("Manual down");
      targetPos = rotations; // update target so holding works after manual
    } else {
      // Default: hold last target position
      elevatorMotor.setControl(positionControl.withPosition(targetPos));
      elevatorMotor2.setControl(positionControl.withPosition(targetPos));
      System.out.println("Holding position | Target = " + targetPos);
    }

    System.out.println("Current rotations = " + rotations);
    System.out.flush();
  }
}
