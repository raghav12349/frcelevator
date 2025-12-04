package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.XboxController;

import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.configs.CurrentLimitsConfigs;
import com.ctre.phoenix6.controls.DutyCycleOut;
import com.ctre.phoenix6.controls.PositionDutyCycle;

public class Robot extends TimedRobot {

  private XboxController controller;
  private TalonFX elevator;
  private TalonFX elevator2;

  private final DutyCycleOut dutyCtrl = new DutyCycleOut(0);
  private final PositionDutyCycle posCtrl = new PositionDutyCycle(0);

  // Auto setpoints (adjust for your robot)
  private final double POS_BOTTOM = 0.0;     // A → all the way down
  private final double POS_HALF   = -8.0;    // X → halfway
  private final double POS_TOP    = -16.0;   // B → full up

  private double targetPos = 0.0;            // What the elevator should hold

  @Override
  public void robotInit() {
    controller = new XboxController(0);
    elevator = new TalonFX(9);
    elevator2 = new TalonFX(10);

    // Current limits
    var limits = new CurrentLimitsConfigs();
    limits.SupplyCurrentLimit = 40;
    limits.SupplyCurrentLimitEnable = true;

    elevator.getConfigurator().apply(limits);
    elevator2.getConfigurator().apply(limits);

    System.out.println("Elevator Robot Initialized");
  }

  @Override
  public void teleopPeriodic() {

    double currentPos = elevator.getPosition().getValueAsDouble();

    // ================================
    // AUTO CONTROL (X and B)
    // ================================
    if (controller.getXButtonPressed()) {
      targetPos = POS_HALF;
      System.out.println("Auto → HALF (" + targetPos + ")");
    }

    if (controller.getBButtonPressed()) {
      targetPos = POS_TOP;
      System.out.println("Auto → TOP (" + targetPos + ")");
    }

    // ================================
    // MANUAL CONTROL (A = up, Y = down)
    // ================================
    boolean a = controller.getAButton();
    boolean y = controller.getYButton();

    if (a) {
      // Manual up
      elevator.setControl(dutyCtrl.withOutput(0.12));
      elevator2.setControl(dutyCtrl.withOutput(0.12));
      targetPos = currentPos; // Prevents it from fighting the manual movement
      System.out.println("Manual UP");
      return;  // Skip holding logic
    }

    if (y) {
      // Manual down
      elevator.setControl(dutyCtrl.withOutput(-0.12));
      elevator2.setControl(dutyCtrl.withOutput(-0.12));
      targetPos = currentPos;
      System.out.println("Manual DOWN");
      return;  // Skip holding logic
    }

    // =====================================
    // NO MANUAL INPUT → HOLD TARGET
    // =====================================
    elevator.setControl(posCtrl.withPosition(targetPos));
    elevator2.setControl(posCtrl.withPosition(targetPos));

    System.out.println("Holding | Target = " + targetPos +
                       " | Current = " + currentPos);
  }
}
