package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.XboxController;

import com.ctre.phoenix6.configs.TalonFXConfiguration;
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

  // Your ORIGINAL calibrated heights:
  private final double HEIGHT_ONE = -3.85;   // X → mid position
  private final double HEIGHT_TWO = -13.85;  // B → high position

  private double targetPos = 0.0;  // last commanded position

  @Override
  public void robotInit() {

    controller = new XboxController(0);
    elevatorMotor = new TalonFX(9);
    elevatorMotor2 = new TalonFX(10);

    // -----------------------------
    // REQUIRED: PID CONFIG
    // -----------------------------
    TalonFXConfiguration cfg = new TalonFXConfiguration();
    cfg.Slot0.kP = 4.0;        // good starting point for elevator
    cfg.Slot0.kI = 0.0;
    cfg.Slot0.kD = 0.0;
    cfg.Slot0.kV = 0.0;        // use if you want gravity FF later
    cfg.Slot0.kS = 0.0;

    elevatorMotor.getConfigurator().apply(cfg);
    elevatorMotor2.getConfigurator().apply(cfg);

    // -----------------------------
    // CURRENT LIMITS (your original)
    // -----------------------------
    var currentconfiguration = new CurrentLimitsConfigs();
    currentconfiguration.SupplyCurrentLimit = 40;
    currentconfiguration.SupplyCurrentLimitEnable = true;
    elevatorMotor.getConfigurator().apply(currentconfiguration);
    elevatorMotor2.getConfigurator().apply(currentconfiguration);

    System.out.println("Robot initialized with PID + limits");
  }

  @Override
  public void teleopPeriodic() {

    boolean yPressed = controller.getYButton();
    boolean aPressed = controller.getAButton();
    boolean xPressed = controller.getXButtonPressed();  // use Pressed
    boolean bPressed = controller.getBButtonPressed();  // use Pressed

    double rotations = elevatorMotor.getPosition().getValueAsDouble();

    // ================================
    // AUTO HEIGHTS (X and B)
    // ================================
    if (xPressed) {
      targetPos = HEIGHT_ONE;
      System.out.println("AUTO → HEIGHT_ONE " + HEIGHT_ONE);
    }

    if (bPressed) {
      targetPos = HEIGHT_TWO;
      System.out.println("AUTO → HEIGHT_TWO " + HEIGHT_TWO);
    }

    // ================================
    // MANUAL CONTROL (A=UP, Y=DOWN)
    // your old height limits restored
    // ================================
    if (aPressed && rotations <= -1.3) {
      elevatorMotor.setControl(dutyOut.withOutput(0.1));
      elevatorMotor2.setControl(dutyOut.withOutput(0.1));
      targetPos = rotations;  // update hold position
      System.out.println("Manual UP");
      return;
    }

    if (yPressed && rotations >= -18) {
      elevatorMotor.setControl(dutyOut.withOutput(-0.1));
      elevatorMotor2.setControl(dutyOut.withOutput(-0.1));
      targetPos = rotations;
      System.out.println("Manual DOWN");
      return;
    }

    // ================================
    // HOLD LAST POSITION (closed loop)
    // ================================
    elevatorMotor.setControl(positionControl.withPosition(targetPos));
    elevatorMotor2.setControl(positionControl.withPosition(targetPos));

    System.out.println("HOLDING | Target = " + targetPos + " | Current = " + rotations);
  }
}
